FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /workspace/app
COPY . .
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /workspace/app/build/libs/*.jar app.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "/app.jar"]