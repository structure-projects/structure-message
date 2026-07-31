FROM eclipse-temurin:21-jdk-alpine

RUN apk update && apk add --no-cache curl tzdata && rm -rf /var/cache/apk/*

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS=""
ENV PARAMS=""
ENV APP_PATH=/app/boot/message-service.jar

RUN mkdir -p /app/logs /app/boot

COPY ./liveness.sh /app/
COPY ./target/message-service.jar /app/boot/message-service.jar

RUN chmod +x /app/liveness.sh

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=60s \
  CMD /app/liveness.sh || exit 1

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar $APP_PATH $PARAMS"]
