KRBACKENDPATH=/var/local/kingreport
PID=`ps -ef |grep $KRBACKENDPATH/kr_backend-1.0.1.jar |grep -vw grep | awk '{print $2}'`
if [ ! "$PID" ];then # 这里判断kr_backend进程是否存在
    echo "kr_backend进程不存在"
else
    echo "kr_backend进程存在 杀死进程PID=$PID"
	kill -9 $PID
fi