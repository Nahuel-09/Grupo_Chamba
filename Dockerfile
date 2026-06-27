# Stage 1: Compilación
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Forzamos el perfil activo para que coincida con tus variables de entorno
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]