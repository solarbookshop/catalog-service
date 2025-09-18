package com.solarbookshop.catalogservice.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookValidationTests {
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      validator = validatorFactory.getValidator();
    }
  }

  @Test
  void when_fields_correct_then_validation_succeeds() {
    var book = new Book("1234567890", "The Hobbit", "J. R. R.", 10.99);
    var violations = validator.validate(book);
    assertThat(violations).isEmpty();
  }

  @Test
  void when_isbn_incorrect_then_validation_fails() {
    var book = new Book("invalid-isbn", "The Hobbit", "J. R. R.", 10.99);
    var violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("The ISBN format must be valid.");
  }
}