#!/bin/env bash

#Build
mvn clean package
cp ./target/JTeeProxy*.jar ./JTeeProxy.jar
