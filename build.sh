#!/usr/bin/env bash

MODULE_NAME=${PWD##*/}
ARTIFACT_NAME=$(clj -M:artifact-name)
ARTIFACT_ID=$(echo "$ARTIFACT_NAME" | cut -f1)
ARTIFACT_VERSION=$(echo "$ARTIFACT_NAME" | cut -f2)
JAR_FILENAME="$ARTIFACT_ID-$ARTIFACT_VERSION.jar"

echo -e "Build \"$MODULE_NAME\"'s jar: target/$JAR_FILENAME"

clj -X:thin-jar :group-id $MODULE_NAME :artifact-id $ARTIFACT_ID :version \"$(echo $ARTIFACT_VERSION)\" :jar target/$JAR_FILENAME 2>&1

if [ $? -eq 0 ]; then
    echo "Successfully built \"$MODULE_NAME\"'s artifact: target/$JAR_FILENAME"
else
    echo "Fail to built \"$MODULE_NAME\"'s artifact!"
    exit 1
fi
