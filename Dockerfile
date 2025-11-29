FROM tomcat:11.0.14-jre21-temurin-jammy

RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

ENV JPDA_ADDRESS=*:8000
ENV JPDA_TRANSPORT=dt_socket

EXPOSE 8080 8000
CMD ["catalina.sh", "jpda", "run"]
