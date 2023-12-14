# My Spring Boot Application

## Overview
This repository contains a Spring Boot application with integrated Docker container hosting a PostgresSQL database.

## Running the Application

### Prerequisites
Make sure you have Docker installed on your machine. If it is not the case, please change the database settings
in the application properties {resources/application.properties} to the following:
[
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
]
I left notes in the properties file. 

### Running the Application
1. Clone this repository to your local machine:
    ```
    git clone <repository_url>
    cd my-springboot-app
    ```

2. To start the application using Docker Compose:
You can use the intellijIDEA to run the docker compose file or use the command line:
    ```
    docker-compose up
    ```
   This command will build the necessary images and start the application along with the PostgreSQL database.

3. Access the application at `http://localhost:8080`.

## Database Initialization
In case the changes made to the PostgreSQL database are lost or to initialize the database, follow these steps:

### Running SQL Initialization Script
1. Ensure that Docker Compose is running (`docker-compose up`).
2. Open a new terminal window.
use this to get the necessary info about the running container:
```bash
# get the running containers
docker ps 
# use the 3 first chars from the returned docker container name: for instance[s7e]
# run a command to get a shell inside the conatiner
docker exec -it s7e /bin/bash

# now you are in: start creating the needed values: first connect to db
psql -U postgres -h localhost
# Now that you are connected to the db, start creating the role and database and configure them to match the env variables in the docker compose file.
# I list them below for you.

# DB_USER="mo"
# DB_PASSWORD="Not1.2.3.aga!n"
# DB_NAME="modb"

#create db
CREATE DATABASE $DB_NAME;

#Create a new role with  password
CREATE ROLE mo WITH LOGIN PASSWORD '$DB_PASSWORD';

## Grant necessary privileges to user
ALTER ROLE mo WITH SUPERUSER;
ALTER ROLE mo WITH CREATEROLE;
ALTER ROLE mo WITH CREATEDB;
ALTER ROLE mo WITH REPLICATION;
ALTER ROLE mo WITH BYPASSRLS;

# Set the ownership of the db and schema
ALTER DATABASE $DB_NAME OWNER TO mo;
ALTER SCHEMA public OWNER TO mo;

# Grant all privileges on  modb to user
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO mo;
# all done, quit.
\q
```

### Important Notes
- Make sure to replace any placeholder values or variables in the provided commands with your actual values.
- Ensure that you have the necessary permissions to execute Docker and Docker Compose commands.


## License
No license. 