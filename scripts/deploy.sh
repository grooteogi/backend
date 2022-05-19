#!/bin/bash

echo "> copy build file"
REPOSITORY=/home/ubuntu/app/grooteogi
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> check running application pid"
CURRENT_PID=$(pgrep -f Grooteogi | grep jar | awk '{print$1}')

echo "$CURRENT_PID"
if [ -z $CURRENT_PID ]; then
  echo "> running application not found\!"
else
  kill -9 $CURRENT_PID
  sleep 5
fi

echo "> Deploy new application"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> Grant execute permission for $JAR_NAME"
chmod +x $JAR_NAME

echo "> Run $JAR_NAME"
nohup java -jar \
  -Dspring.profiles.active=dev \
  $JAR_NAME >$REPOSITORY/nohup.out 2>&1 &
