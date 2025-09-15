# -------- 1) Сборка (stage build) --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

ARG JWT_SECRET
ARG ADMIN_EMAIL
ARG ADMIN_PASSWORD

ENV JWT_SECRET=${JWT_SECRET}
ENV ADMIN_EMAIL=${ADMIN_EMAIL}
ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}

COPY demo/gradlew ./gradlew
COPY demo/gradle ./gradle
COPY demo/build.gradle demo/settings.gradle ./

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN ./demo/gradlew dependencies --no-daemon || true

COPY demo/src ./src
RUN ./gradlew build --no-daemon

# -------- 2) Рантайм (stage run) --------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
