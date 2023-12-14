#!/bin/bash

set -e

# Wait for the psql container to start and be ready  to accept connections
 until pg_isready -h localhost -U postgres -q; do
   >&2 echo "PSQL is unavailable! Sleeping..."
   sleep 1
 done

 >&2 echo "Postgres is up - executing initialization scripts"
sleep 5


# Define environment variables or set your password directly
DB_USER="mo"
DB_PASSWORD="Not1.2.3.aga!n"
DB_NAME="modb"

# Connect to the PSQL container
psql -U postgres -h localhost <<-EOSQL
    -- Create a new db
    CREATE DATABASE $DB_NAME;

    -- Create a new role with  password
    CREATE ROLE mo WITH LOGIN PASSWORD '$DB_PASSWORD';

    -- Grant necessary privileges to user
    ALTER ROLE mo WITH SUPERUSER;
    ALTER ROLE mo WITH CREATEROLE;
    ALTER ROLE mo WITH CREATEDB;
    ALTER ROLE mo WITH REPLICATION;
    ALTER ROLE mo WITH BYPASSRLS;

    -- Set the ownership of the db and schema
    ALTER DATABASE $DB_NAME OWNER TO mo;
    ALTER SCHEMA public OWNER TO mo;

    -- Grant all privileges on  modb to user
    GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO mo;
    \q
EOSQL








