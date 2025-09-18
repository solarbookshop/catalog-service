package com.solarbookshop.catalogservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {

  @Autowired
  JacksonTester<Book> json;

  @Test
  void testSerialize() throws IOException {
    var book = new Book("1234567890", "The Hobbit", "J. R. R.", 10.99);
    var jsonContent = json.write(book);

    assertThat(jsonContent).extractingJsonPathStringValue("@.isbn").isEqualTo(book.isbn());
    assertThat(jsonContent).extractingJsonPathStringValue("@.title").isEqualTo(book.title());
    assertThat(jsonContent).extractingJsonPathStringValue("@.author").isEqualTo(book.author());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.price").isEqualTo(book.price());
  }

  @Test
  void testDeserialize() throws IOException {
    var jsonContent = """
            {
              "isbn":"1234567890",
              "title":"The Hobbit",
              "author":"",
              "price":10.99
            }
            """;
    var expectedBook = new Book("1234567890", "The Hobbit", "", 10.99);

    assertThat(json.parse(jsonContent))
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
  }
}
