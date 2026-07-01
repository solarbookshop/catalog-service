package com.solarbookshop.catalogservice;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {
  @Bean
  KeycloakContainer keycloakContainer() {
    return new KeycloakContainer("quay.io/keycloak/keycloak:26.6")
        .withRealmImportFile("test-realm-config.json");
  }

  @Bean
  DynamicPropertyRegistrar keycloakProperties(KeycloakContainer keycloak) {
    return registry -> registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
        () -> keycloak.getAuthServerUrl() + "/realms/SolarBookshop");
  }
}
