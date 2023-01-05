FROM maven:3.6.3-jdk-14

ADD . /usr/src/alixus-crawler
WORKDIR /usr/src/alixus-crawler
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]