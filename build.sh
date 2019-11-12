#!/usr/bin/env sh

./gradlew clean bootJar
docker build -t eu.wojciechzurek/mattermost-nbp:0.0.1 -t eu.wojciechzurek/mattermost-nbp:latest -f Dockerfile .