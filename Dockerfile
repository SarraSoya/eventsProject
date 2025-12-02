# Image de base avec Java 17
FROM openjdk:17-jdk-slim

# Dossier de travail dans le conteneur
WORKDIR /app

# Argument : chemin du JAR généré par Maven
ARG JAR_FILE=target/eventsProject-1.0.1.jar

# Copier le JAR dans l'image
COPY ${JAR_FILE} app.jar

# Port exposé par l'application Spring Boot (souvent 8080)
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
