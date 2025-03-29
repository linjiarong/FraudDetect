FROM openjdk:17-ea-slim
ARG APP_VERSION
ENV APP_VERSION="${APP_VERSION}"
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC"

ADD trans-generator-*.jar /


ENTRYPOINT ["sh","-c","java -jar trans-generator-${APP_VERSION}.jar"]