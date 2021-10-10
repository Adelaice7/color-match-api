# Color Match API

This is an API designed to match products of similar colors.

## API
The REST API has multiple endpoints for handling the products.

### Important API endpoints
1. `/importProducts` This is an endpoint for importing the products from a CSV file.
It takes a file path String, in simple plain text format from the Response Body.
The format *must* include the extension of the file. The API accepts paths existing on the Docker container. Example:
`/usr/api-service/res/products_test.csv`. 
The path is case-sensitive and has to be exact, otherwise the Batch Job cannot run properly.
2. `/getColor/{id}` This retrieves the dominant color stored in the database for a given product. A product id 
is needed in the path. Example: `/getColor/PH4012-00-CNQ`
3. `/loadColor/{id}` This endpoint uses the Google Vision API to load the image from the database for 
a given product, and find the first dominant color and save it for the product. A product id is needed in the path.
It also checks whether a product already has a dominant color, and only loads a new one if it doesn't 
(the value is null). Example: `/loadColor/PH4012-00-CNQ` will load the dominant color using the Vision API,
and persist it to the database.
4. `/loadColorForAllProducts` This is the endpoint that loads the dominant colors for all products in the database.
This will run the Google Vision API for the entire database. It checks all product elements and if they
do not have an existing dominant color, it will load all of them.
The API does not re-load dominant colors for records that already have a dominant color.
It also checks whether the image found in the database is from a working path.
5. `/getProductsOfColor/{id}/{n}` This endpoint is responsible for the main functionality of this API.
It searches the database for products that have a dominant color close to the one of the product provided.
This needs a product `ID` and an `n` number for retrieving the `n` closest elements to the given product. Example:
`/getProductsOfColor/L1212-00-132/15` will retrieve the 15 products that are closest in color to the provided 
`L1212-00-132`.

### Tech
The application uses Java 11 with Spring Boot, to create a RESTful API to manage product colors.
It uses a PostgreSQL database as PostgreSQL is a great choice for dealing with CSV files.
It also uses Docker to connect the server to the database in the same container, and for easier management.

There are three main parts to this application.

#### Part I

You can import products from a CSV file through a REST API endpoint. This uses a Spring Batch job
to read the CSV file using a FlatFileItemReader and then persists them to the database. This uses a chunk-based,
multi-threaded implementation to handle bigger data sets with consistent memory and CPU usage.

The import happens in a way that it does not overwrite records in the database that already have a dominant color.
Because the CSV data does not contain a dominant color, it would normally overwrite the database-stored ones with 
null values.

The database I am using is PostgreSQL, and I use a regular `RepositoryItemWriter` to use Spring JPA to persist 
the entities. This is a thread-safe solution given the multi-threaded implementation.

The `ThreadPoolTaskExecutor` is used to separate the processing task to multiple, reusable threads.
Better configuration of this could be used.

I believe, currently the Big O notation for this is O(n). The performance of the reading and writing job
could be further improved by using remote chunking of a Step, or by partitioning.
Spring Batch provides solutions for this.

Functionality-wise, this part could be improved by handling other sources of data instead of just a CSV file. 
I could define a new ItemReader in the Spring Batch job, and decide based on JobProperties, what type of data 
to process with which reader. The Batch job is configured in `ImportJobBatchConfiguration` 
and the job is initiated from `importProductsFromFilePath(filePath)` within `ProductService`.

I have tested the import on large, generated data sets. On average, I achieved 1m 4s with a data set of 200000 items.
This is drastically better than a simple single-threaded implementation. I also tried taking care of thread safety,
but a multi-threaded implementation implies that the Batch Job cannot be restarted. If a job fails,
it will not be able to pick up from where it left off. As a solution, Spring Batch could be replaced with better
alternatives, such as Apache Spark.

#### Part II

I have implemented the usage of the Google Vision API with the help of Spring GCP. Using the `/loadColorForAllProducts`
endpoint, it starts a Batch job to read the data from the database, then run the Vision API algorithm on each image path,
then finally persist all the updated data to the database.

The Batch job configuration is found in `DomColorJobBatchConfiguration`. It is very similar to the job configured for
the import of products. In this case, I use a `RepositoryItemReader` to read from the database. This is a paging
ItemReader which is a better option to use with a multi-threaded implementation, as unlike cursor-based ItemReaders,
this is thread-safe. There is a `pageSize` set for it, and it reads that many records from the database. If a chunk is 
not processed successfully, data can be lost. A reasonably high page size will result in better performance.

This implementation uses the same `ThreadPoolTaskExecutor` as the import job does. It also has an ItemProcessor
called `DomColorProductItemProcessor` to perform the call to the Google Vision API implementation in `VisionService`.
It also checks for null values in dominant colors and filters out the non-null values.

This could be improved by adding a switch to be able to overwrite existing dominant colors in the database.

`VisionService` contains the details to retrieving the Dominant Color for a given product's image.
The implementation includes loading the image path, currently using a `HttpsURLConnection`, but this could be
improved by adding multiple ways of loading the picture (e.g. a local path file loader).

Once the image has been loaded as a Spring Resource, this will be used for the Google Vision API
to retrieve an RGB vector in the form of an int array.

I cannot say for certain the Big O notation for this part, as the Google Vision API's performance is majorly impacted
depending on the type and size of images used. I believe the reading, processing and writing part would be O(n2)
simply because in the ItemProcessor, for every read item I check the database-stored version to see if it has a
dominant color already. I did not go quite enough into this, I think this could be removed and managed differently.

For this, I have added an index to the `Product` entity's ID, to speed things up slightly.

#### Part III

The final API. The endpoint `/getProductsOfColor/{id}/{n}` retrieves an n-element list of Products,
that have the closest color proximity to the product provided in `id`.

For this implementation, I have used the findAll method in the JPARepository but this may be more memory-consuming
than necessary. `ProductService` then sorts this list of products, comparing the absolute value of two product's
dominant colors. I used absolute value for measuring the lowest distance. The sorted list is then limited to the value
of `n` and returned to the endpoint.

For the calculation of the color proximity, the RGB vectors (as received by the Vision API) 
have to be converted to CIE L * a * b vectors. As I found no better way of directly converting an RGB value
to a L * a * b one, I went by the basis of the color being of the sRGB profile and I used a natural luminosity,
to achieve the best, natural looking grays. 
The implementation for this is in `ColorProximity`.

In the current state, I think this has an O(n) Big O annotation but it could be improved by also implementing 
multi-threaded, more scalable options here. Because of the pay-per-use nature of Google Vision API,
I was not able to test this on very large sets of data, apart from the provided product catalog.

#### Other comments

Upon using the Google Vision API on all records in the database, I have come up with 133 skipped items
because of the lack of a working image path, and it took overall 5m 13s 128ms. This needs improving, but I think
at this point, the performance might be halted by the Vision API itself, as without the Google Vision algorithm,
I was able to achieve 500ms on average with processing the records here.

There are many smaller things that could use improving, or just a different approach. With little experience in such
projects, I cannot guarantee it will be a flawless processing of items.

## Instructions on running the application

The application runs in Docker, using *docker-compose*. 

The Docker configuration requires some environment variables set before you can run it.

Create an `.env` file in the root directory of the application, based on the `.env.example` file.

Certain resources required by the application are stored in the `docker/res` directory on the host,
which is mounted to/copied to the container to a certain path which can be changed in the environment variables.
This is the directory where you put the CSV file to be imported, or the Google credentials' JSON file, for example.
The environment variable for this path is `DOCKER_FILE_RES_DIR`.

### Docker environment variables

1. `DB_HOST` is the host for the database. In this Docker configuration, the host name is the same as the database
container's name: `db`.
2. `DB_PORT` is the port at which the application is running. The PostgreSQL database's port is `5432` here.
3. `DB_NAME` the name of the database used. In this case `productdb` is my choice.
4. `DB_USER` the username for the database. 
5. `DB_PASSWORD` the password for the database user.
6. `DOCKER_FILE_RES_DIR` the path for the resource directory used in the Docker container. 
Should not be changed by default.
7. `GOOGLE_APPLICATION_CREDENTIALS` the JSON file containing the Google service account's credentials.
Full path required on the Docker container, including the `.json` extension. Spring GCP automatically picks up on the
JSON-based authentication being added.
Example: `/usr/api-service/res/creds.json`.

**Normally, the PostgreSQL Docker image should automatically be configured with any username/password combination. 
It will be used as the default.*


After the configuration of the environment variables, you should be able to start the application.

Run the application by using the `docker-compose up --build` command. 
This will initialize the database and the server and start up the application.

After this, you can use the REST API to import a products list and perform actions on it. I have used Postman
to test the endpoints.
