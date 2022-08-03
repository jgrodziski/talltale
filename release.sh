#!/usr/bin/env bash

RELEASE_LEVEL=$1
MODULE_NAME=${PWD##*/}
echo "Release \"$MODULE_NAME\" with level '$RELEASE_LEVEL'"
tag=$(clj -Mrelease $RELEASE_LEVEL --spit --output-dir src --namespace talltale.meta --formats clj,cljs)

if [ $? -eq 0 ]; then
    echo "Successfully released \"$MODULE_NAME\" to $tag"
else
    echo "Fail to release \"$MODULE_NAME\"!"
    exit 1
fi

####################################################
# build thin jar                                   #
####################################################
source ./build.sh

####################################################
#                                                  #
#     Clojars uploading stuff                      #
#                                                  #
####################################################

if [[ $tag =~ v(.+) ]]; then
    newversion=${BASH_REMATCH[1]}
else
    echo "unable to parse tag $tag"
    exit 1
fi
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$newversion  2>&1 > /dev/null

if [ $? -eq 0 ]; then
    echo "Successfully set new version of \"$MODULE_NAME\"'s pom to $newversion"
else
    echo "Fail to set new version of \"$MODULE_NAME\"'s pom!"
    exit 1
fi

ARTIFACT_NAME=$(clj -Ametav -m metav.display)
ARTIFACT_ID=$(echo "$ARTIFACT_NAME" | cut -f1)
ARTIFACT_VERSION=$(echo "$ARTIFACT_NAME" | cut -f2)

JAR_FILENAME="$ARTIFACT_ID-$ARTIFACT_VERSION.jar"

clj -X:deploy :artifact \"$(echo target/$JAR_FILENAME)\"

if [ $? -eq 0 ]; then
    echo "Successfully deployed \"$MODULE_NAME\" version $newversion to clojars"
else
    echo "Fail to deploy \"$MODULE_NAME\" to clojars!"
    exit 1
fi


