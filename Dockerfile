FROM ubuntu:18.04 as soapproducer
RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get upgrade -y \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y apt-utils tzdata locales bash-completion \
    && ln -snf /usr/share/zoneinfo/"Asia/Yekaterinburg" /etc/localtime \
    && echo "Asia/Yekaterinburg" > /etc/timezone \
    && dpkg-reconfigure -f noninteractive tzdata \
    && sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen \
    && sed -i -e 's/# ru_RU.UTF-8 UTF-8/ru_RU.UTF-8 UTF-8/' /etc/locale.gen \
    && echo 'LANG="ru_RU.UTF-8"'>/etc/default/locale \
    && dpkg-reconfigure --frontend=noninteractive locales \
    && update-locale LANG=ru_RU.UTF-8 \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-11-jdk

ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU:ru
ENV LC_ALL ru_RU.UTF-8
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ARG DEPENDENCY=target/dependency
COPY target/producing-soap-service-0.0.1-SNAPSHOT.jar /app/producing-soap-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Xmx256m","-jar","/app/producing-soap-service-0.0.1-SNAPSHOT.jarr","--spring.config.location=file:/var/local/application.properties"]
CMD echo "Done"
