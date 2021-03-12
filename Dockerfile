# the first stage of our build will use a maven 3.6.1 parent image
FROM maven:3.3-jdk-8

# copy the pom and src code to the container
COPY ./ ./

# package our application code
RUN mvn clean install -f web/example/pom.xml

# set the startup command to execute the jar
CMD ["java", "-jar", "spring-data-examples/web/example/target/spring-data-web-example-2.0.0.BUILD-SNAPSHOT.jar"]

# package our application code
RUN mvn clean install -f web/projection/pom.xml

# set the startup command to execute the jar
CMD ["java", "-jar", "spring-data-examples/web/projection/target/spring-data-web-projection-2.0.0.BUILD-SNAPSHOT.jar"]

# package our application code
RUN mvn clean install -f web/querydsl/pom.xml

# set the startup command to execute the jar
CMD ["java", "-jar", "spring-data-examples/web/querydsl/target/spring-data-web-querydsl-2.0.0.BUILD-SNAPSHOT.jar"]