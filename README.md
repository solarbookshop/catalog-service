# Catalog Service

This application is part of the Solar Bookshop system and provides the functionality for managing
the books in the bookshop catalog.

## REST API

| Endpoint      | HTTP method | Request body | Status | Response body | Description                               |
|---------------|-------------|--------------|--------|---------------|-------------------------------------------|
| /books        | GET         |              | 200    | Book[]        | Get all the books in the catalog.         |
| /books        | POST        | Book         | 201    | Book          | Add a new book to the catalog.            |
|               |             |              | 422    |               | A book with the same ISBN already exists. |
| /books/{isbn} | GET         |              | 200    | Book          | Get the book with the given ISBN.         |
|               |             |              | 404    |               | No book with the given ISBN exists.       |
| /books/{isbn} | PUT         | Book         | 200    | Book          | Update the book with the given ISBN.      |
|               |             |              | 201    | Book          | Create a book with the given ISBN.        |
| /books/{isbn} | DELETE      |              | 204    |               | Delete the book with the given ISBN.      |

## Useful Commands

| Gradle Command             | Description                                   |
|:---------------------------|:----------------------------------------------|
| `./gradlew bootRun`        | Run the application.                          |
| `./gradlew build`          | Build the application.                        |
| `./gradlew test`           | Run tests.                                    |
| `./gradlew bootJar`        | Package the application as a JAR.             |
| `./gradlew bootBuildImage` | Package the application as a container image. |

After building the application, you can also run it from the Java CLI:

```bash
java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
```

## Running a PostgreSQL Database

Run PostgreSQL as a Docker container

```bash
docker container run -d --rm --name solar-postgres \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=solardb_catalog \
  -p 5433:5432 \
  postgres:18
```

### Container Commands

| Docker Command                 |    Description    |
|:-------------------------------|:-----------------:|
| `docker stop solar-postgres`   |  Stop container.  |
| `docker start solar-postgres`  | Start container.  |
| `docker remove solar-postgres` | Remove container. |

### Database Commands

Start an interactive PSQL console:

```bash
docker exec -it solar-postgres psql -U user -d solardb_catalog
```

Access PostgreSQL from the host machine:

```bash
PGPASSWORD=secret psql -h localhost -p 5433 -U user -d solardb_catalog
```

| PSQL Command	              | Description                                    |
|:---------------------------|:-----------------------------------------------|
| `\list`                    | List all databases.                            |
| `\connect solardb_catalog` | Connect to specific database.                  |
| `\dt`                      | List all tables.                               |
| `\d book`                  | Show the `book` table schema.                  |
| `\d flyway_schema_history` | Show the `flyway_schema_history` table schema. |
| `\quit`                    | Quit interactive psql console.                 |

From within the PSQL console, you can also fetch all the data stored in the `book` table.

```bash
select * from book;
```

The following query is to fetch all the data stored in the `flyway_schema_history` table.

```bash
select * from flyway_schema_history;
```