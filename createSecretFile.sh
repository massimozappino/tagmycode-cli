#!/bin/sh

if [ ! -f "src/main/java/com/tagmycode/cli/Secret.java" ]; then
    cat src/test/resources/Secret.java.txt > src/main/java/com/tagmycode/cli/Secret.java
else
    echo "Secret.java already exists"
fi