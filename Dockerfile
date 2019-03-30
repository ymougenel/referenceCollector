FROM java:8
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ADD docker-wait-for-it.sh docker-wait-for-it.sh
RUN bash -c 'chmod +x /docker-wait-for-it.sh'
RUN ls -la
ENTRYPOINT ["/bin/bash", "/docker-wait-for-it.sh"]

