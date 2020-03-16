# FROM hub.c.163.com/library/java:8-alpine
FROM 192.168.100.170/library/java:8

MAINTAINER XXX@imooc.com

ADD init.sh /target/init.sh
ADD eureka.jar /target/eureka.jar

EXPOSE 8761

RUN rm /bin/sh && ln -s /bin/bash /bin/sh
ENTRYPOINT ["/bin/bash","-c","source /target/init.sh"]

# ENTRYPOINT ["java", "-jar", "/target/eureka.jar"]