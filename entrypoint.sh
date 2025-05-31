#!/bin/bash

cd /app
./gradlew clean bootJar && \
./gradlew --stop && \
java -jar $(find build/libs/ -type f -iname "TukitaLearner*.jar")