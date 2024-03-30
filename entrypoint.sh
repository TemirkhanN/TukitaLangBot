#!/bin/bash

cd /app
# TODO research more adequate ways to do this
./gradlew clean jar && \
./gradlew update && \
./gradlew --stop && \
java -jar build/libs/TukitaLearner-1.0.jar