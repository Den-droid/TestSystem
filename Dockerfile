FROM eclipse-temurin:8-jdk-jammy as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests
 
FROM eclipse-temurin:8-jre-jammy
WORKDIR /opt/app
COPY --from=builder /opt/app/target/TestSystemProject-0.0.1-SNAPSHOT.jar /opt/app/TestSystemProject-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/opt/app/TestSystemProject-0.0.1-SNAPSHOT.jar" ]