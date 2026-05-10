FROM eclipse-temurin:8-jdk-alpine
ADD ./structure-user-boot/target/user-center.jar /app/boot/app.jar
ADD ./structure-user-cloud/target/user-center.jar /app/cloud/app.jar
ADD liveness.sh /app/liveness.sh
ENV PARAMS=""
ENV JAVA_OPTS=""
ENV APP_PATH=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS $PARAMS -jar $APP_PATH","-ea","&"]
EXPOSE 8080
