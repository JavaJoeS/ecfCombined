#!/bin/sh

#the directory containing this script
scriptDir=`dirname "$0"`

ANT_HOME=/bluebird/teamswt/swt-builddir/build/apache-ant-1.7.1
extraArgs=""

while [ "$1" != "" ]; do
	if [ "$1" = "-java" ] && [ "$2" != "" ]; then
        JAVA_HOME=/bluebird/teamswt/swt-builddir/build/JRE/$2
        shift
    elif [ "$1" = "-javaHome" ] && [ "$2" != "" ]; then
		JAVA_HOME="$2"
		shift
    elif [ "$1" = "-antHome" ] && [ "$2" != "" ]; then
		ANT_HOME="$2"
		shift
    else
        extraArgs="$extraArgs $1"
    fi
    shift
done

OS=`uname -s`
case $OS in 
	"SunOS")
		if [ "$PROC" = "" ];  then
		    PROC=`uname -p`
		fi
		case ${PROC} in
			"i386")
				ANT_HOME=~/equinox/apache-ant-1.7.1
				;;
		esac
		;;
esac

PATH=$JAVA_HOME/jre/bin:$ANT_HOME/bin:$PATH

export JAVA_HOME ANT_HOME PATH
echo JAVA_HOME = $JAVA_HOME
echo ANT_HOME = $ANT_HOME
echo ant -f ${scriptDir}/build.xml buildConfig $extraArgs 
ant -f ${scriptDir}/build.xml buildConfig $extraArgs
