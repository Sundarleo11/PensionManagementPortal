# For Java 11, try this

# FROM adoptopenjdk/openjdk11:alpine-jre

FROM openjdk:8

EXPOSE 8000

ADD target/PensionManagementPortal.jar PensionManagementPortal.jar

ENTRYPOINT ["java","-jar","/PensionManagementPortal.jar"]