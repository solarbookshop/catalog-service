plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.solarbookshop"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // webclient is available from v4.0.0 - changing back to webflux for now
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
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
    imageName.set(project.name)
    environment.set(mapOf("BP_JVM_VERSION" to "25.*"))

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