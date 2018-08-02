FROM openjdk:8-jdk-alpine
EXPOSE 8080
ADD /target/producerDocker.jar producerDocker.jar
ENTRYPOINT ["java","-jar","producerDocker.jar"]
