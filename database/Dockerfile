FROM postgres
ENV POSTGRES_PASSWORD admin
ENV POSTGRES_DB amusement_park
ADD init.sql /docker-entrypoint-initdb.d/init.sql
