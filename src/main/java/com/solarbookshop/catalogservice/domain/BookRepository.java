package com.solarbookshop.catalogservice.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {
  Optional<Book> findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);

  @Transactional
  @Modifying
  @Query("DELETE FROM Book WHERE isbn = :isbn")
  void deleteByIsbn(String isbn);
}
