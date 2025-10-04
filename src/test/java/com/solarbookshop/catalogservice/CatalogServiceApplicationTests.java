package com.solarbookshop.catalogservice;

import com.solarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CatalogServiceApplicationTests {
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:17.3");

  @Autowired
  WebTestClient webTestClient;

  @Test
  void whenContainerStarted_thenConnectionIsSuccessful() {
    assertThat(postgresqlContainer.isRunning()).isTrue();
  }

  @Test
  void when_post_request_then_book_created() {
    var expectedBook = Book.of("1234567890", "Expected Java", "Joshua Bloch", 45.00);
    webTestClient.post()
            .uri("/books")
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class)
            .isEqualTo(expectedBook);
  }
}
