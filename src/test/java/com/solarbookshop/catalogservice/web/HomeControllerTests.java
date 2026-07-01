package com.solarbookshop.catalogservice.web;

import com.solarbookshop.catalogservice.config.SecurityConfig;
import com.solarbookshop.catalogservice.config.SolarProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HomeControllerTests {
  @Autowired
  MockMvc mockMvc;

  RestTestClient client;

  @Autowired
  SolarProperties solarProperties;

  @BeforeEach
  void setup() {
    client = RestTestClient.bindTo(mockMvc).build();
  }

  @Test
  void when_root_url_then_display_welcome_message() {
    client.get().uri("/")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(body -> assertThat(body).isEqualTo(solarProperties.getGreeting()));
  }
}
