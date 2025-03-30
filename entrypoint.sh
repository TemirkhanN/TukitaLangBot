#!/bin/bash

cd /app
# TODO research more adequate ways to do this. Probably, just build jar in dockerfile
./gradlew clean bootJar && \
./gradlew --stop && \
java -jar $(find build/libs/ -type f -iname "TukitaLearner*.jar")