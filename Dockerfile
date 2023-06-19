FROM gradle:8.1.1-jdk11

WORKDIR /usr/app
USER root

COPY . .
RUN gradle build

ENTRYPOINT exec gradle run
