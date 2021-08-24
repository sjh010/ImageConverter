#! /bin/bash
export IMAGE_DAEMON_HOME=/home/sylim/dev/jhso
export WATCH_HOME=/home/sylim/dev/jhso/watchDog
cd $IMAGE_DAEMON_HOME

while [ 1 ]
do
  daemon_pid=`ps -ef | grep -v grep | grep "Dproject=ImageDaemon" | grep -v vi | awk '{print $2}'`
  DATE=$(date "+%Y-%m-%d %H:%M:%S")

  if [ "${daemon_pid}" == "" ]
  then
    echo "[$DATE] ImageDaemon start" >> $WATCH_HOME/restart.log
    watchdog_pid=`ps -ef | grep -v grep | grep "/bin/sh /home/sylim/dev/jhso/startImageDaemon.sh" | grep -v vi | awk '{print $2}'`
    /bin/sh startImageDaemon.sh &
    echo "[$DATE] ImageDaemon start complete" >> $WATCH_HOME/restart.log
    
    kill -9 ${watchdog_pid}

    #sleep 5
    #continue
  fi
  
  sleep 10 
done

