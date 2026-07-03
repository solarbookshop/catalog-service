package com.solarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig.class)
@ActiveProfiles("integration")
@AutoConfigureRestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Allows non-static @BeforeAll
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CatalogServiceApplicationTests {
  // Customer
  KeycloakToken ramTokens;
  // Customer and employee
  KeycloakToken shayamTokens;

  @Autowired
  KeycloakContainer keycloakContainer;

  @Autowired
  RestTestClient testClient;

  @BeforeAll
  void generateAccessTokens() {
    var restClient = RestClient.builder()
        .baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/SolarBookshop/protocol/openid-connect/token")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build();

    shayamTokens = authenticateWith("shayam", "password", restClient);
    ramTokens = authenticateWith("ram", "password", restClient);
  }

  @Test
  void post_request_with_authorization_gives_201() {
    var expectedBook = Book.of("1234567890", "Expected Java", "Joshua Bloch", 45.00, "Solar Books");
    testClient.post().uri("/books")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + shayamTokens.accessToken())
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
  void post_req_without_authorization_gives_403() {
    var expectedBook = Book.of("1234567890", "Expected Java", "Joshua Bloch", 45.00, "Solar Books");
    testClient.post().uri("/books")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ramTokens.accessToken())
        .body(expectedBook)
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void post_req_without_authentication_gives_401() {
    var expectedBook = Book.of("1234567890", "Expected Java", "Joshua Bloch", 45.00, "Solar Books");
    testClient.post().uri("/books")
        .body(expectedBook)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void put_request_updates_the_book() {
    var bookIsbn = "1231231232";
    var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Solar Books");
    Book createdBook = testClient.post().uri("/books")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + shayamTokens.accessToken())
        .body(bookToCreate)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
        .returnResult().getResponseBody();
    assert createdBook != null;
    var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), createdBook.title(),
        createdBook.author(), 7.95, "Solar Books", createdBook.createdDate(), createdBook.lastModifiedDate(),
        createdBook.createdBy(), createdBook.lastModifiedBy(), createdBook.version());

    testClient.put().uri("/books/" + bookIsbn)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + shayamTokens.accessToken())
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
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + shayamTokens.accessToken())
        .body(bookToCreate)
        .exchange()
        .expectStatus().isCreated();

    testClient.delete().uri("/books/" + bookIsbn)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + shayamTokens.accessToken())
        .exchange()
        .expectStatus().isNoContent();

    testClient.get().uri("/books/" + bookIsbn)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + shayamTokens.accessToken())
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).value(errorMessage ->
            assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
        );
  }

  private KeycloakToken authenticateWith(String username, String password, RestClient restClient) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "password");
    formData.add("client_id", "solar-test");
    formData.add("username", username);
    formData.add("password", password);

    return restClient
        .post()
        .body(formData)
        .retrieve()
        .body(KeycloakToken.class);
  }

  private record KeycloakToken(String accessToken) {
    @JsonCreator
    private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
      this.accessToken = accessToken;
    }
  }
}

