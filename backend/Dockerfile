# ---- Étape 1 : build Maven ----
    FROM eclipse-temurin:21-jdk AS build
    WORKDIR /app
    
    # Cache des dépendances Maven
    COPY .mvn/ .mvn/
    COPY mvnw pom.xml ./
    RUN chmod +x mvnw && ./mvnw -B dependency:go-offline
    
    COPY src ./src
    RUN ./mvnw -B clean package -DskipTests
    
    # ---- Étape 2 : image d'exécution ----
    FROM eclipse-temurin:21-jre
    WORKDIR /app
    
    RUN useradd --system --create-home spring
    USER spring
    
    COPY --from=build /app/target/*.jar app.jar
    
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]