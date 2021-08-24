#1 /bin/sh
pid=`ps -ef | grep -v grep | grep "watchdog.sh" | awk '{print $2}'`
date=$(date "+%Y-%m-%d %H:%M:%S")

if [ "$pid" == "" ]
then
  echo "[$date] WatchDog start"
  nohup ./watchdog.sh 1> /dev/null 2> /dev/null &
#  tail -f ./watchdog.log
  exit 0
else
  echo "[$date] Already start WatchDog..."
fi
