package com.solarbookshop.catalogservice.web;

import com.solarbookshop.catalogservice.domain.BookNotFoundException;
import com.solarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.BDDMockito.given;

@WebMvcTest(BookController.class)
class BookControllerTests {
  @Autowired
  MockMvc mockMvc;

  RestTestClient client;

  @MockitoBean
  BookService bookService;

  @BeforeEach
  void setUp() {
    client = RestTestClient.bindTo(mockMvc).build();
  }

  @Test
  void when_book_not_exist_then_return_404() {
    var isbn = "123456789";
    given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);

    client.get()
        .uri("/books/" + isbn)
        .exchange()
        .expectStatus().isNotFound();
  }
}