package com.solarbookshop.catalogservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJdbcAuditing
public class DataConfig {
  /// Configures an [AuditorAware] bean to extract the current authenticated user's identity.
  ///
  /// When an `AuditorAware` bean is defined and auditing is enabled via [@EnableJdbcAuditing],
  /// Spring Data uses it to extract the current principal. In our case, the username of the
  /// currently authenticated user is represented as a `String`.
  ///
  /// This enables Spring Data to automatically populate entity fields annotated with
  /// `@CreatedBy` and `@LastModifiedBy` (`Book` record) whenever a create
  /// or update operation is performed on the entity.
  ///
  /// @return an [AuditorAware] providing the current authenticated user's name, or empty if unauthenticated
  @Bean
  AuditorAware<String> auditorAware() {
    return () -> Optional
        .of(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getName);
  }
}
