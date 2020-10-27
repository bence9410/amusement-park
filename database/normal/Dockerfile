FROM postgres
ENV POSTGRES_PASSWORD admin
ENV POSTGRES_DB amusement_park
ADD 0-schema.sql /docker-entrypoint-initdb.d/0-schema.sql
ADD 1-data.sql /docker-entrypoint-initdb.d/1-data.sql
