package com.solarbookshop.catalogservice.domain;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookServiceTests {
  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private BookService bookService;

  @Test
  void when_book_to_create_already_exists_then_throws() {
    var bookIsbn = "1234561232";
    var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Publisher");
    when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
    assertThatThrownBy(() -> bookService.addBookToCatalog(bookToCreate))
        .isInstanceOf(BookAlreadyExistsException.class)
        .hasMessage("A book with ISBN " + bookIsbn + " already exists.");
  }

  @Test
  void when_book_to_read_does_not_exist_then_throws() {
    var bookIsbn = "1234561232";
    when(bookRepository.findByIsbn(bookIsbn)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> bookService.viewBookDetails(bookIsbn))
        .isInstanceOf(BookNotFoundException.class)
        .hasMessage("The book with ISBN " + bookIsbn + " was not found.");
  }
}