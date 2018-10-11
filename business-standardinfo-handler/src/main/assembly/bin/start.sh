#!/bin/bash
cd `dirname $0`
cd ../

# 服务根目录
DEPLOY_DIR=`pwd`
readonly DEPLOY_DIR
# conf目录
CONF_DIR=${DEPLOY_DIR}/conf
readonly CONF_DIR
# lib目录
LIB_DIR=${DEPLOY_DIR}/lib
readonly LIB_DIR
# 服务名称（服务根目录名称）
SERVER_NAME=${DEPLOY_DIR##*/}
readonly SERVER_NAME
# log目录
LOGS_DIR=${DEPLOY_DIR}/logs
readonly LOGS_DIR
if [ ! -d ${LOGS_DIR} ]; then
    mkdir ${LOGS_DIR}
fi
# log文件
STDOUT_FILE=${LOGS_DIR}/stdout.log
readonly STDOUT_FILE
# 启动类
START_CLASS="com.wanfang.datacleaning.handler.HandlerApplication"
readonly START_CLASS

# lib目录下的jar包
LIB_JARS=`ls ${LIB_DIR} | grep .jar | awk '{print "'${LIB_DIR}'/"$0}'| tr "\n" ":"`

echo -e "\033[36m ------------ Starting service start ------------ \033[0m"
# java基本配置
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
readonly JAVA_OPTS

# debug
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

# jmx
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

# java内存配置
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "${BITS}" ]; then
        JAVA_MEM_OPTS=" -server -Xms16g -Xmx16g -XX:+HeapDumpOnOutOfMemoryError -XX:MaxPermSize=128m -XX:-UseGCOverheadLimit -XX:+UseConcMarkSweepGC"
    else
        JAVA_MEM_OPTS=" -server -Xms2g -Xmx2g -XX:+HeapDumpOnOutOfMemoryError -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC"
fi

# 运行启动类
if [ -f "${STDOUT_FILE}" ];then
     # 系统当前时间
    currentTime=$(date "+%Y%m%d%H%M%S")
    mv ${STDOUT_FILE} ${LOGS_DIR}/stdout_${currentTime}_backup.log
fi
nohup java ${JAVA_OPTS} ${JAVA_MEM_OPTS} ${JAVA_DEBUG_OPTS} ${JAVA_JMX_OPTS} -classpath ${CONF_DIR}:${LIB_JARS} ${START_CLASS} > ${STDOUT_FILE} 2>&1 &

# 判断服务是否启动成功
PIDS=`ps -ef | grep java | grep "${DEPLOY_DIR}/" | awk '{print $2}'`
if [ -z "${PIDS}" ]; then
        echo -e "\033[31m ERROR: The service of ${SERVER_NAME} startup failed ! \033[0m"
    else
        echo -e "\033[32m The service of ${SERVER_NAME} start successfully ! \033[0m"
        echo -e "\033[32m PIDS: ${PIDS}  \033[0m"
        echo -e "\033[32m STDOUT: ${STDOUT_FILE} \033[0m"
fi

echo -e "\033[36m ------------ Starting service end ------------ \033[0m"