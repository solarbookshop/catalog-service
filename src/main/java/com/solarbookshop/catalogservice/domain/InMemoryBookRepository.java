package com.solarbookshop.catalogservice.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
public class InMemoryBookRepository implements BookRepository {
  private final Map<String, Book> books = new ConcurrentHashMap<>();

  @Override
  public Iterable<Book> findAll() {
    return books.values();
  }

  @Override
  public Optional<Book> findBookByIsbn(String isbn) {
    return Optional.ofNullable(books.get(isbn));
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return books.containsKey(isbn);
  }

  @Override
  public Book save(Book book) {
    books.put(book.isbn(), book);
    return book;
  }

  @Override
  public void deleteByIsbn(String isbn) {
    books.remove(isbn);
  }
}
