# Catalog Service
This application is part of the Solar Bookshop system and provides the functionality for managing
the books in the bookshop catalog.

## REST API
| Endpoint      | HTTP method | Request body | Status | Response body | Description                               |
|---------------|-------------|--------------|--------|---------------|-------------------------------------------|
| /books        | GET         |              | 200    | Book[]        | Get all the books in the catalog.         |
| /books        | POST        | Book         | 201    | Book          | Add a new book to the catalog.            |
|               |             |              | 409    |               | A book with the same ISBN already exists. |
| /books/{isbn} | GET         |              | 200    | Book          | Get the book with the given ISBN.         |
|               |             |              | 404    |               | No book with the given ISBN exists.       |
| /books/{isbn} | PUT         | Book         | 200    | Book          | Update the book with the given ISBN.      |
|               |             |              | 201    | Book          | Create a book with the given ISBN.        |
| /books/{isbn} | DELETE      |              | 204    |               | Delete the book with the given ISBN.      |