package com.solarbookshop.catalogservice.demo;

import com.solarbookshop.catalogservice.domain.Book;
import com.solarbookshop.catalogservice.domain.BookRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("testdata")
public class BookDataLoader {
  private final BookRepository bookRepository;

  public BookDataLoader(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void loadBookTestData() {
    bookRepository.save(new Book("0134685997", "Effective Java", "Joshua Bloch", 45.0));
    bookRepository.save(new Book("0596009208", "Head First Java", "Kathy Sierra, Bert Bates", 37.5));
    bookRepository.save(new Book("9781492078005", "Kotlin in Action", "Dmitry Jemerov, Svetlana Isakova", 49.99));
  }
}
