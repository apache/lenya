#!/bin/sh
#
##############################################################
#        CONFIGURATION                                       #
##############################################################
#
EXPORT_DIR=/home/michi/build/jakarta-tomcat-4.0/webapps/wyona-cms/wyona/cms/pubs/unipublic/resources/publication/export
PENDING_DIR=$EXPORT_DIR/pending/127.0.0.1/wyona-cms/unipublic
REPLICATION_TEMP=$EXPORT_DIR/replication

RU_1=michi
RH_1=cvs.wyona.org
RDOCS_1=/usr/local/apache/htdocs_unipublic

RU_2=michi
RH_2=cvs.wyona.org
RDOCS_2=/usr/local/apache/htdocs_oscom
#
#
#
PARENT=`dirname $0`
CLASSPATH=`grep CLASSPATH $PARENT/properties.conf | grep -v "#" | sed -e 's/CLASSPATH=//'`
JAVA=`grep JAVA $PARENT/properties.conf | grep -v "#" | sed -e 's/JAVA=//'`
SCP=/usr/bin/scp
#


###########################################
#                MAIN                     #
###########################################

echo "START"
date

mkdir $REPLICATION_TEMP

if [ -d $REPLICATION_TEMP ];then
##  echo "DEBUG: $REPLICATION_TEMP"

  PROCESS_ID=$$
  DATUM=`date +%Y.%m.%d_%H.%M.%S`
  TEMP_ID=$DATUM\_$PROCESS_ID

  TEMP_DIR=$REPLICATION_TEMP/temp\_$TEMP_ID
  mkdir -p $TEMP_DIR
##  echo "DEBUG: $TEMP_DIR"

  if [ -d $PENDING_DIR ];then
##    echo "DEBUG: $PENDING_DIR"

    if [ -d $PENDING_DIR ];then
      mv $PENDING_DIR/* $TEMP_DIR/.
    fi


#    $JAVA -classpath $CLASSPATH org.wyona.xps.publish.Replicator $TEMP_DIR




    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_1/.
      $SCP -r $TEMP_DIR/* $RU_1@$RH_1:$RDOCS_1/.
    fi
    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_2/.
      $SCP -r $TEMP_DIR/* $RU_2@$RH_2:$RDOCS_2/.
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_TEMP"
  exit 0
fi

date
echo "STOP"
