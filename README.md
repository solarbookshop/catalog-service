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

## Postgres Database

### start postgresql

```bash
docker run -d --rm --name solar-postgres \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=solardb_catalog \
  -p 5433:5432 \
  postgres:18
```

### stop postgresql

```bash
docker stop solar-postgres
```

### access postgresql container

```bash
docker exec -it solar-postgres psql -U user -d solardb_catalog
```

### access postgresql from host

```bash
PGPASSWORD=secret psql -h localhost -p 5433 -U user -d solardb_catalog
```