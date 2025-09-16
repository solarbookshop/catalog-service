package com.solarbookshop.catalogservice.domain;

import java.util.Optional;

public interface BookRepository {
  Iterable<Book> findAll();
  Optional<Book> findBookByIsbn(String isbn);
  boolean existsByIsbn(String isbn);
  Book save(Book book);
  void deleteByIsbn(String isbn);
}
