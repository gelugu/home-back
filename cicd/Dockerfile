FROM gradle:7.4.2-jdk17 as builder

WORKDIR /app

COPY --chown=gradle:gradle . /app

RUN gradle clean shadowJar --no-daemon

FROM openjdk:17

RUN mkdir /app

COPY --from=builder /app/build/libs/home-back.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
