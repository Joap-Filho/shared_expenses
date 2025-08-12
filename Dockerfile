# Etapa de build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de runtime
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
