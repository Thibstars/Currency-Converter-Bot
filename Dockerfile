FROM maven:3.6.1-jdk-12 AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/

RUN mvn clean package -f pom.xml

FROM adoptopenjdk/openjdk12
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/*.jar app.jar
# Make sure to provide an environment variable `BOT_TOKEN`
ENTRYPOINT exec java -jar /app.jar $BOT_TOKEN $DBL_TOKEN