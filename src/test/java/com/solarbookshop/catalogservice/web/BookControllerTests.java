package com.solarbookshop.catalogservice.web;

import com.solarbookshop.catalogservice.config.SecurityConfig;
import com.solarbookshop.catalogservice.domain.Book;
import com.solarbookshop.catalogservice.domain.BookNotFoundException;
import com.solarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.BDDMockito.given;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookControllerTests {
  @Autowired
  MockMvc mockMvc;

  RestTestClient client;

  @MockitoBean
  BookService bookService;

  @MockitoBean
  JwtDecoder jwtDecoder;

  @BeforeEach
  void setUp() {
    client = RestTestClient.bindTo(mockMvc).build();
  }

  @Test
  void get_req_when_book_not_exists_returns_404() {
    var isbn = "123456789";
    given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);

    client.get().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void get_req_without_authentication_return_200() {
    client.get().uri("/books")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  /**
   * Why does @WithMockUser work in BookControllerTests?
   * Spring Security, it only checks if the current Authentication has the authority ROLE_employee.
   * Both @WithMockUser(roles = "employee") and a mock JWT with ROLE_employee satisfy this requirement.
   */
  @WithMockUser(roles = "employee")
  void delete_req_with_employee_role_returns_204() {
    var isbn = "123456789";
    client.delete().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @WithMockUser(roles = "customer")
  void delete_req_with_customer_role_returns_403() {
    var isbn = "123456789";
    client.delete().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void delete_req_without_authentication_returns_401() {
    var isbn = "123456789";
    client.delete().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @WithMockUser(roles = "customer")
  void post_req_with_customer_role_returns_403() {
    var book = Book.of("123456789", "The Hobbit", "J. R. R.", 10.99, "Solar Books");
    client.post().uri("/books")
        .body(book)
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void post_req_without_authentication_returns_401() {
    var book = Book.of("123456789", "The Hobbit", "J. R. R.", 10.99, "Solar Books");
    client.post().uri("/books")
        .body(book)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @WithMockUser(roles = "customer")
  void put_req_with_customer_role_returns_403() {
    var book = Book.of("123456789", "The Hobbit", "J. R. R.", 10.99, "Solar Books");
    client.put().uri("/books")
        .body(book)
        .exchange()
        .expectStatus().isForbidden();
  }
}