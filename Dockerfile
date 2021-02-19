FROM java:8
COPY target/referenceCollector-1.2.0.jar app.jar
ADD docker-wait-for-it.sh docker-wait-for-it.sh
RUN bash -c 'chmod +x /docker-wait-for-it.sh'
RUN ls -la
ENTRYPOINT ["/bin/bash", "/docker-wait-for-it.sh"]

