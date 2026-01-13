FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/*.jar app.jar
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

# docker build -t gcr.io/cnse-qr-code-generator/backend:latest ./backend

# docker tag backend:latest gcr.io/cnse-qr-code-generator/backend:latest
# docker push gcr.io/cnse-qr-code-generator/backend:latest

# gcloud run deploy backend-service --image gcr.io/cnse-qr-code-generator/backend:latest --platform managed --region europe-west1 --allow-unauthenticated --set-env-vars SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=cnse-qr-code-generator,GCS_BUCKET=cnse-qr-code-generator-bucket
# gcloud run services delete backend-service --region europe-west1
