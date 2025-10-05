package com.solarbookshop.catalogservice.domain;

import com.solarbookshop.catalogservice.config.DataConfig;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookRepositoryJdbcTests {
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private JdbcAggregateTemplate jdbcAggregateTemplate;

  @Test
  void find_all_books() {
    var effectiveJava = Book.of("0134685997", "Effective Java", "Joshua Bloch", 45.0);
    var headFirstJava = Book.of("0596009208", "Head First Java", "Kathy Sierra, Bert Bates", 37.5);
    jdbcAggregateTemplate.saveAll(List.of(effectiveJava, headFirstJava));

    var actualBooks = bookRepository.findAll();

    var foundBooks = StreamSupport.stream(actualBooks.spliterator(), false)
            .filter(b -> b.isbn().equals(effectiveJava.isbn()) || b.isbn().equals(headFirstJava.isbn()))
            .toList();
    assertThat(foundBooks).hasSize(2);
  }

  @Test
  void find_book_by_isbn_when_existing() {
    var isbn = "0134685997";
    var effectiveJava = Book.of(isbn, "Effective Java", "Joshua Bloch", 45.0);
    jdbcAggregateTemplate.save(effectiveJava);

    var actualBook = bookRepository.findByIsbn(isbn);

    assertThat(actualBook).isPresent();
    assertThat(actualBook.get().isbn()).isEqualTo(isbn);
  }

  @Test
  void exists_by_isbn_when_existing() {
    var isbn = "0134685997";
    var effectiveJava = Book.of(isbn, "Effective Java", "Joshua Bloch", 45.0);
    jdbcAggregateTemplate.save(effectiveJava);

    var exists = bookRepository.existsByIsbn(isbn);

    assertThat(exists).isTrue();
  }

  @Test
  void exists_by_isbn_when_not_existing() {
    var isbn = "0134685997";
    var exists = bookRepository.existsByIsbn(isbn);
    assertThat(exists).isFalse();
  }

  @Test
  void delete_by_isbn() {
    var isbn = "0134685997";
    var effectiveJava = Book.of(isbn, "Effective Java", "Joshua Bloch", 45.0);
    var savedBook = jdbcAggregateTemplate.save(effectiveJava);

    bookRepository.deleteByIsbn(isbn);

    var exists = jdbcAggregateTemplate.existsById(savedBook.id(), Book.class);
    assertThat(exists).isFalse();
  }
}