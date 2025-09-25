# ---- Build stage ----
FROM gradle:8.8-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon -x test


# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
CMD ["--spring.profiles.active=prod"]