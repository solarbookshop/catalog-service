package com.solarbookshop.catalogservice.web;

import com.solarbookshop.catalogservice.config.SolarProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  private final SolarProperties solarProperties;

  public HomeController(SolarProperties solarProperties) {
    this.solarProperties = solarProperties;
  }

  @GetMapping
  public String home() {
    return solarProperties.getGreeting();
  }
}
