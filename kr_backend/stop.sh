#!/bin/bash
PID=$(ps -ef|grep kr_backend-1.0.1.jar | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo Application is already stopped
else
    echo kill $PID
    kill $PID
    echo "service stop success"
fi