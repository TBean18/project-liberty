#!/bin/sh
mvn clean package &&
java -jar '.\target\project-liberty-1.0-SNAPSHOT-jar-with-dependencies.jar'