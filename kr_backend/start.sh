#!/bin/bash
echo "start...kr_backend-1.0.1.jar"
nohup java -jar /root/.jenkins/workspace/kingreport/kr_backend/target/kr_backend-1.0.1.jar --spring.config.location=/root/.jenkins/workspace/kingreport/kr_backend/target/classes/application.properties