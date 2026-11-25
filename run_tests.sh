#!/usr/bin/env bash
# Script penjalan seluruh JUnit 5 Test Suite EcoTrash v4
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
M2="/Users/mac/.m2/repository"
JUNIT_CP="$M2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar:$M2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar:$M2/org/junit/platform/junit-platform-commons/1.10.0/junit-platform-commons-1.10.0.jar:$M2/org/junit/platform/junit-platform-engine/1.10.0/junit-platform-engine-1.10.0.jar:$M2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar:$M2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar"

java -cp "$DIR/ecotrash/bin:$DIR/ecotrash/target/test-classes:$JUNIT_CP" id.ac.kampus.ecotrash.JUnit5ConsoleRunner
