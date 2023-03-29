FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar statistic-server.jar
ENTRYPOINT ["java","-jar","/statistic-server.jar"]
