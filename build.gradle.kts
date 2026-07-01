plugins {
  java
  id("org.springframework.boot") version "4.1.0"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.solarbookshop"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(25)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

extra["springCloudVersion"] = "2025.1.2"
extra["testKeyCloakVersion"] = "4.2.1"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-webmvc")
  implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
  implementation("org.flywaydb:flyway-database-postgresql")
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("org.postgresql:postgresql")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:testcontainers-junit-jupiter")
  testImplementation("org.testcontainers:testcontainers-postgresql")
  testImplementation("com.github.dasniko:testcontainers-keycloak:${property("testKeyCloakVersion")}")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.bootRun {
  systemProperty("spring.profiles.active", "testdata")
}

tasks.bootBuildImage {
  builder.set("paketobuildpacks/builder-noble-java-tiny:latest")
  imageName.set(project.name)
  environment.set(mapOf("BP_JVM_VERSION" to "25"))

  docker {
    publishRegistry {
      val publishRequested = (project.findProperty("publishImage"))?.toString()?.toBoolean() ?: false

      if (!publishRequested) {
        return@publishRegistry
      }

      val registryUrl = (project.findProperty("registryUrl") ?: System.getenv("REGISTRY_URL")) as String?
      val user = (project.findProperty("registryUsername") ?: System.getenv("REGISTRY_USERNAME")) as String?
      val token = (project.findProperty("registryToken") ?: System.getenv("REGISTRY_TOKEN")) as String?

      if (user.isNullOrBlank() || token.isNullOrBlank()) {
        throw GradleException("Registry credentials missing. Set registryUsername/registryToken or REGISTRY_USERNAME/REGISTRY_TOKEN.")
      }

      url.set(registryUrl)
      username.set(user)
      password.set(token)
    }
  }
}