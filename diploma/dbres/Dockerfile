FROM postgres:17
COPY ./1-schema.sql /docker-entrypoint-initdb.d/1-schema.sql
COPY ./2-data.sql /docker-entrypoint-initdb.d/2-data.sql
