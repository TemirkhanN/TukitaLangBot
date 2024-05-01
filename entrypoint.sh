#!/bin/bash

cd /app
# TODO research more adequate ways to do this
./gradlew clean jar && \
./gradlew update && \
./gradlew --stop && \
java -jar $(find build/libs/ -type f -iname "TukitaLearner*.jar")