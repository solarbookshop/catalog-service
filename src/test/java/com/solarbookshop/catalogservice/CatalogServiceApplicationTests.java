package com.solarbookshop.catalogservice;

import com.solarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureRestTestClient
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CatalogServiceApplicationTests {
  @Autowired
  RestTestClient testClient;

  @Test
  void post_request_creates_the_book() {
    var expectedBook = Book.of("1234567890", "Expected Java", "Joshua Bloch", 45.00, "Solar Books");
    testClient.post().uri("/books")
        .body(expectedBook)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Book.class)
        .value(actual -> {
          assertThat(actual).isNotNull();
          assertThat(actual.isbn()).isEqualTo(expectedBook.isbn());
        });
  }

  @Test
  void put_request_updates_the_book() {
    var bookIsbn = "1231231232";
    var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Solar Books");
    Book createdBook = testClient.post().uri("/books")
        .body(bookToCreate)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
        .returnResult().getResponseBody();
    assert createdBook != null;
    var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), createdBook.title(), createdBook.author(), 7.95,
        "Solar Books", createdBook.createdDate(), createdBook.lastModifiedDate(), createdBook.version());

    testClient.put().uri("/books/" + bookIsbn)
        .body(bookToUpdate)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Book.class).value(actualBook -> {
          assertThat(actualBook).isNotNull();
          assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
        });
  }

  @Test
  void delete_request_deletes_the_book() {
    var bookIsbn = "1231231233";
    var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Solar Books");
    testClient.post().uri("/books")
        .body(bookToCreate)
        .exchange()
        .expectStatus().isCreated();

    testClient.delete().uri("/books/" + bookIsbn)
        .exchange()
        .expectStatus().isNoContent();

    testClient.get().uri("/books/" + bookIsbn)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).value(errorMessage ->
            assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
        );
  }
}
