#!/bin/bash

set -e

echo "Building JAR file..."
mvn clean package -DskipTests

echo "Running JAR file..."
java -jar target/command-atm-0.0.1-SNAPSHOT.jar