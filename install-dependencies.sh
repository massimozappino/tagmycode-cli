#!/bin/sh

./createSecretFile.sh

cd /tmp
git clone https://github.com/massimozappino/tagmycode-java-sdk.git
cd tagmycode-java-sdk
mvn clean install
