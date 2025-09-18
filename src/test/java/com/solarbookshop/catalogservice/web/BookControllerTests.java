package com.solarbookshop.catalogservice.web;

import com.solarbookshop.catalogservice.domain.BookNotFoundException;
import com.solarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;

@WebMvcTest(BookController.class)
class BookControllerTests {
  @Autowired
  WebApplicationContext webApplicationContext;

  WebTestClient webTestClient;

  @MockitoBean
  BookService bookService;

  @BeforeEach
  void setUp() {
    webTestClient = MockMvcWebTestClient.bindToApplicationContext(webApplicationContext).build();
  }

  @Test
  void when_book_not_exist_then_return_404() {
    var isbn = "123456789";
    given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);

    webTestClient.get()
            .uri("/books/" + isbn)
            .exchange()
            .expectStatus().isNotFound();
  }
}