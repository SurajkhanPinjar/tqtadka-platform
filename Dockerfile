# ===============================
# BUILD STAGE
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# RUNTIME STAGE
# ===============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy jar
COPY --from=build /app/target/*.jar app.jar

# JVM optimizations for small VPS
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]