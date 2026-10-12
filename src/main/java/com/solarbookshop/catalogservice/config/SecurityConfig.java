package com.solarbookshop.catalogservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) {
    return http
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/", "/books/**").permitAll()
            .anyRequest().hasRole("employee")
        )
        // Enables OAuth2 Resource Server support using the default configuration based on JWT (JWT authentication)
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
        // Each request must include an Access Token, so there's no need to keep a session alive between requests,
        // so we want it to be stateless.
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Since the authentication strategy is stateless and doesn't involve a browser-based client,
        // we can safely disable the CSRF protection.
        .csrf(AbstractHttpConfigurer::disable)
        .build();
  }

  /// Defines a custom strategy to extract GrantedAuthority objects from a JWT access token.
  ///
  /// Configures a [JwtGrantedAuthoritiesConverter] to extract role names from the `roles` claim
  /// and prefix them with `ROLE_`. Spring Security associates these granted authorities with each
  /// authenticated user to evaluate authorization policies (e.g., `hasRole("employee")`).
  ///
  /// @return the configured [JwtAuthenticationConverter]
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}
