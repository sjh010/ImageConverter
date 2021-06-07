#!/bin/sh

#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=ImageDaemon" |awk {'print $2'})

if [[ $pid = "" ]]
    then
	echo "there is no process [edocDaemon]"
	exit;
else
	kill -9 $pid
	echo "ImageDaemon : kill -9 $pid"
	while [ $(ps -ef |grep -v grep | grep Dproject=ImageDaemon | wc -l) -ne 0 ]
		do
			sleep 1
		done
	echo "stopped ImageDaemon"
	exit 0;
fi



