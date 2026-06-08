# Multi-stage build
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Primero los archivos de build (cambian poco)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución al gradlew
RUN chmod +x gradlew

# Descargar dependencias : Esta capa se cachea mientras no cambien los .gradle
RUN ./gradlew dependencies --no-daemon || true

# Copiar código fuente
COPY src src

# Compilar la aplicación
RUN ./gradlew clean bootJar -x test --no-daemon


# Etapa final
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Crear usuario sin privilegios y usarlo
RUN groupadd -r app && useradd -r -g app app
USER app

# Exponer puerto
EXPOSE 8081

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]