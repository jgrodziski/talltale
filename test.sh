#!/bin/sh

# JVM tests
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "==================================================="
echo "=  Running talltale tests on JVM $java_version          ="
echo "==================================================="
clojure -A:test:runner

if [ $? -eq 0 ]; then
    echo "\033[32m\"talltale\" tests run successfully on JVM\033[0m"
else
    echo "\033[31m\"talltale\" JVM tests failed!\033[0m"
    exit 1
fi

echo "==============================================================="
echo "=     Running talltale tests on Chromium JS Engine            ="
echo "==============================================================="
# JS Engine tests (because talltale should be isomorphic)
yarn install --silent \
    && clojure -A:dev:test-js \
    && npx karma start --single-run

if [ $? -eq 0 ]; then
    echo "\033[32m\"talltale\" tests run successfully on Chromium JS Engine\033[0m"
    echo "\033[32m\"talltale\" tests run successfully on both Clojure/JVM _AND_ Clojurescript/Chromium JS Engine\033[0m"
else
    echo "\033[31m\"talltale\" JS tests failed!\033[0m"
    exit 1
fi
