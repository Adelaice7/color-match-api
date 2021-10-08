# Color Match API

This is an API designed to match products of similar colors.

## API
The REST API has multiple endpoints for handling the products.

### Product API endpoints
1. `/importProducts` is a REST API endpoint for importing the products from a CSV file.
It takes a file path String, in simple plain text format from the Response Body.
The format *must* include the extension of the file. The API accepts paths existing on the Docker container. Example:
`/usr/api-service/res/products_test.csv`. 
The path is case-sensitive and has to be exact, otherwise the Batch Job cannot run properly.
2. `/getColor/{id}` retreives the dominant color stored in the database for a given product. A product id 
is needed in the path.
3. `/loadColor/{id}` this endpoint uses the Google Vision API to load the image from the database for 
a given product, and find the first dominant color and save it for the product. A product id is needed in the path.
It also checks whether a product already has a dominant color, and only loads a new one if it doesn't 
(the value is null).
4. `/loadColorForAllProducts` is the endpoint that loads the dominant colors for all products in the database.
This will run the Google Vision API for the entire database. It checks all product elements and if they
do not have an existing dominant color, it will load all of them.

### Tech
The application uses Java 11 with Spring Boot, to create a REST API.
It uses a PostgreSQL database as PostgreSQL is a great choice for dealing with CSV files.
It also uses Docker to connect the server to the database in the same container, and for easier management.

The application uses multi-threading for faster and efficient processing of data.

The Google Cloud Platform is accessed using the Spring GCP library. Spring GCP contains the Cloud Vision API
which simplifies the usage of the Google Cloud Vision API.

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
Full path required on the Docker container, including the `.json` extension.
Example: `/usr/api-service/res/creds.json`

**Normally, the PostgreSQL Docker image should automatically be configured with any username/password combination. 
It will be used as the default.*


After the configuration of the environment variables, you should be able to start the application.

Run the application by using the `docker-compose up --build` command. 
This will initialize the database and the server and start up the application.

After this, you can use the REST API to import a products list and perform actions on it.
