FROM --platform=linux/amd64 maven:3.9.12-eclipse-temurin-21
LABEL authors="nguyngc"

# Stage 1: Build
WORKDIR /app
COPY pom.xml .
COPY . /app
RUN mvn -q -DskipTests package

# Stage 2: Run JavaFX
FROM eclipse-temurin:21-jre

# Install dependencies for JavaFX (GTK, X11, OpenGL)
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgl1 \
    libgl1-mesa-dri \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK for Linux
RUN wget https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_linux-x64_bin-sdk.zip \
    && unzip openjfx-21.0.1_linux-x64_bin-sdk.zip \
    && mv javafx-sdk-21.0.1 /opt/javafx \
    && rm openjfx-21.0.1_linux-x64_bin-sdk.zip

# Copy JAR from build stage
COPY target/calculator.jar app.jar

ENV DISPLAY=host.docker.internal:0
ENV DB_HOST=host.docker.internal

# Run JavaFX with module-path
CMD ["java", "--module-path", "/opt/javafx/lib", "--add-modules", "javafx.controls,javafx.fxml", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "app.jar"]


