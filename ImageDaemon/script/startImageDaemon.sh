export IMAGE_HOME=/programs/app/edocDaemon
cd $EDS_HOME
echo 'Current Path = ' $PWD
. ./Config/EdocServer.env
#PID Check
pid=$(ps -ef |grep -v grep | grep "Dproject=ImageDaemon" |awk {'print $2'})


if [[ $pid = "" ]]; then
	echo "start edocDaemon"
        nohup java -d64 -Dproject=ImageDaemon com.mobileleader.image.ImageDaemonApp 1> /dev/null 2> error.info &
	
    waitingTime=0
	
	while [ $waitingTime -ne 10 ]
		do
			if [ $(ps -ef |grep -v grep | grep "Dproject=ImageDaemon" | wc -l) -eq 1 ]
			then
				echo "ImageDaemon is start!! Process ID=$(ps -ef |grep -v grep | grep "Dproject=ImageDaemon" |awk {'print $2'})"
				exit 0;
			else
				sleep 1
				echo "waiting $waitingTime seconds..."
				waitingTime=`expr $waitingTime + 1`
			fi
		done
	exit -1;
else
	echo "already ImageDaemon running"
	exit 0;
fi


