package com.solarbookshop.catalogservice.domain;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookJsonTests {

  @Autowired
  JacksonTester<Book> json;

  @Test
  void test_serialize() throws IOException {
    var book = Book.of("1234567890", "The Hobbit", "J. R. R.", 10.99);
    var jsonContent = json.write(book);

    assertThat(jsonContent).extractingJsonPathStringValue("@.isbn").isEqualTo(book.isbn());
    assertThat(jsonContent).extractingJsonPathStringValue("@.title").isEqualTo(book.title());
    assertThat(jsonContent).extractingJsonPathStringValue("@.author").isEqualTo(book.author());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.price").isEqualTo(book.price());
  }

  @Test
  void test_deserialize() throws IOException {
    var jsonContent = """
            {
              "isbn":"1234567890",
              "title":"The Hobbit",
              "author":"",
              "price":10.99
            }
            """;
    var expectedBook = Book.of("1234567890", "The Hobbit", "", 10.99);

    assertThat(json.parse(jsonContent))
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
  }

  @Test
  void test_serialize_with_audit_fields() throws IOException {
    var created = Instant.parse("2020-01-01T00:00:00Z");
    var modified = Instant.parse("2020-01-02T01:02:03Z");

    var book = new Book(42L, "1234567890123", "Clean Code", "Robert C. Martin", 33.50, created, modified, 5);
    var jsonContent = json.write(book);

    // id and version should be present as numbers
    assertThat(jsonContent).extractingJsonPathNumberValue("@.id").isEqualTo(book.id().intValue());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.version").isEqualTo(book.version());

    // createdDate and lastModifiedDate should be serialized as ISO-8601 strings
    assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate").isEqualTo(created.toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate").isEqualTo(modified.toString());
  }

  @Test
  void test_deserialize_with_audit_fields() throws IOException {
    var jsonContent = """
            {
              "id":1,
              "isbn":"1234567890123",
              "title":"The Pragmatic Programmer",
              "author":"Andy Hunt",
              "price":42.0,
              "createdDate":"2000-01-01T12:00:00Z",
              "lastModifiedDate":"2000-01-02T13:14:15Z",
              "version":2
            }
            """;

    var expected = new Book(1L, "1234567890123", "The Pragmatic Programmer", "Andy Hunt", 42.0,
            Instant.parse("2000-01-01T12:00:00Z"), Instant.parse("2000-01-02T13:14:15Z"), 2);

    assertThat(json.parse(jsonContent))
            .usingRecursiveComparison()
            .isEqualTo(expected);
  }
}
