FROM maven:3.6.0-jdk-11-slim AS build

COPY src /home/j-joules/src
COPY pom.xml /home/j-joules

RUN mvn -f /home/j-joules/pom.xml clean package


FROM openjdk:11-jre-slim
COPY --from=build /home/j-joules/target/J-Joules-1.0-SNAPSHOT.jar /usr/local/lib/j-joules.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/j-joules.jar"]
