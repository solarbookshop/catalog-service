package com.solarbookshop.catalogservice.demo;

import com.solarbookshop.catalogservice.domain.Book;
import com.solarbookshop.catalogservice.domain.BookRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("testdata")
public class BookDataLoader {
  private final BookRepository bookRepository;

  public BookDataLoader(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void loadBookTestData() {
    bookRepository.deleteAll();
    var effectiveJava = Book.of("0134685997", "Effective Java", "Joshua Bloch", 45.0);
    var headFirstJava = Book.of("0596009208", "Head First Java", "Kathy Sierra, Bert Bates", 37.5);
    var kotlinInAction = Book.of("9781492078005", "Kotlin in Action", "Dmitry Jemerov, Svetlana Isakova", 49.99);
    bookRepository.saveAll(List.of(effectiveJava, headFirstJava, kotlinInAction));
  }
}
