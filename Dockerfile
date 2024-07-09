# Use a Maven image as base
FROM maven:3.8.1-jdk-11-slim AS build

# Set working directory
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Fetch dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src


# Expose the port your app runs on
EXPOSE 8080

# Command to run the application using spring-boot:run
CMD ["mvn", "spring-boot:run"]
