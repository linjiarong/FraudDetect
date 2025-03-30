FROM openjdk:17-ea-slim
ARG APP_VERSION
ENV APP_VERSION="${APP_VERSION}"
RUN apt-get update && apt-get install -y \
    curl awscli \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*
ADD fraud-detection-service-*.jar /

ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar fraud-detection-service-$APP_VERSION.jar"]