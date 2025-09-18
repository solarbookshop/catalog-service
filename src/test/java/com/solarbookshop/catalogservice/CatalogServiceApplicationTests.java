package com.solarbookshop.catalogservice;

import com.solarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogServiceApplicationTests {
  @Autowired
  WebTestClient webTestClient;

  @Test
  void when_post_request_then_book_created() {
    var expectedBook = new Book("1234567890", "Expected Java", "Joshua Bloch", 45.00);
    webTestClient.post()
            .uri("/books")
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class)
            .isEqualTo(expectedBook);
  }
}
