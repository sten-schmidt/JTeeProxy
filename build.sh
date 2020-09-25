#!/bin/env bash

#Build
mvn clean package
cp ./target/JTeeProxy*-jar-with-dependencies.jar ./JTeeProxy.jar
