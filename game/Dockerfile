FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build

CMD ["java", "-jar", "build/libs/game-service.jar"]