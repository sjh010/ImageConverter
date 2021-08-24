#! /bin/sh

pid=`ps -ef | grep -v grep | grep watchdog.sh | awk '{print $2}'`
DATE=$(date "+%Y-%m-%d %H:%M:%S")
if [ "${pid}" == "" ]
then
  echo "[$DATE] WatchDog not exist.."
else
  echo "[$DATE] Stop WatchDog. PID : ${pid}"
  kill -9 ${pid}
fi
