FROM maven:3.6.3-jdk-14

ADD . /usr/src/alixus-crawler
WORKDIR /usr/src/alixus-crawler
EXPOSE 4567

ENV BASE_URL <<enter_your_http_website_here>>
ENV MAX_RESULTS 100
ENV PRETTY_JAY 1

ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]