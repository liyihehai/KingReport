#!/bin/bash
KRBACKENDPATH=/var/local/kingreport
echo "start...kr_backend-1.0.1.jar"
nohup java -jar $KRBACKENDPATH/kr_backend-1.0.1.jar --spring.config.location=$KRBACKENDPATH/classes/application.properties > $KRBACKENDPATH/log.out &