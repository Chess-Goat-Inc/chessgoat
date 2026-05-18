FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

CMD ["sh", "-c", "java -jar build/libs/*SNAPSHOT.jar"]