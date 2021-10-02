# Color Match API

This is an API designed to match products of similar colors.

## API
The REST API has multiple endpoints for handling the products.

### Product API endpoints
1. `/importProducts` is a REST API endpoint for importing the products from a CSV file.
It takes a file path String, in simple plain text format from the Response Body.

### Tech
I used Java 11 with Spring Boot for creating a REST API for this application.
The application uses a PostgreSQL database as PostgreSQL is a great choice for dealing with CSV files.
It also uses Docker to connect the server to the database in the same container, and for easier management.

