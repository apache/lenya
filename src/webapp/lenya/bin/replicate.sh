#!/bin/sh
#
##############################################################
#        CONFIGURATION                                       #
##############################################################
#
SCP=/usr/bin/scp

PUBLICATION_DIR=/home/michi/build/jakarta-tomcat-4.0/webapps/wyona-cms/wyona/cms/pubs


EXPORT_DIR_1=$PUBLICATION_DIR/unipublic/resources/publication/export
PENDING_DIR_1=$EXPORT_DIR_1/pending/127.0.0.1/wyona-cms/unipublic
REPLICATION_DIR_1=$EXPORT_DIR_1/replication
RU_1_1=michi
RH_1_1=cvs.wyona.org
RDOCS_1_1=/usr/local/apache/htdocs_unipublic
RU_1_2=michi
RH_1_2=cvs.wyona.org
RDOCS_1_2=/usr/local/apache/htdocs_unipublic



EXPORT_DIR_2=$PUBLICATION_DIR/oscom/resources/publication/export
PENDING_DIR_2=$EXPORT_DIR_2/pending/127.0.0.1/wyona-cms/oscom
REPLICATION_DIR_2=$EXPORT_DIR_2/replication
RU_2_1=michi
RH_2_1=cvs.wyona.org
RDOCS_2_1=/usr/local/apache/htdocs_oscom
RU_2_2=michi
RH_2_2=cvs.wyona.org
RDOCS_2_2=/usr/local/apache/htdocs_oscom
#
#
#
##PARENT=`dirname $0`
##CLASSPATH=`grep CLASSPATH $PARENT/properties.conf | grep -v "#" | sed -e 's/CLASSPATH=//'`
##JAVA=`grep JAVA $PARENT/properties.conf | grep -v "#" | sed -e 's/JAVA=//'`
#


###########################################
#                MAIN                     #
###########################################

echo "START"
date

# Loop over all publications (BEGIN)


#### PUBLICATION 1

mkdir $REPLICATION_DIR_1

if [ -d $REPLICATION_DIR_1 ];then
  echo "DEBUG: Replication Directory: $REPLICATION_DIR_1"

  PROCESS_ID=$$
  DATUM=`date +%Y.%m.%d_%H.%M.%S`
  TEMP_ID=$DATUM\_$PROCESS_ID

  TEMP_DIR=$REPLICATION_DIR_1/temp\_$TEMP_ID
  mkdir -p $TEMP_DIR
  echo "DEBUG: Temporary Directory: $TEMP_DIR"

  if [ -d $PENDING_DIR_1 ];then
    echo "DEBUG: Pending Directory: $PENDING_DIR_1"

    if [ -d $PENDING_DIR_1 ];then
      mv $PENDING_DIR_1/* $TEMP_DIR/.
    fi


#    $JAVA -classpath $CLASSPATH org.wyona.xps.publish.Replicator $TEMP_DIR




    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_1_1/.
      $SCP -r $TEMP_DIR/* $RU_1_1@$RH_1_1:$RDOCS_1_1/.
    fi
    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_1_2/.
      $SCP -r $TEMP_DIR/* $RU_1_2@$RH_1_2:$RDOCS_1_2/.
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR_1"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_DIR_1"
  exit 0
fi



#### PUBLICATION 2

mkdir $REPLICATION_DIR_2

if [ -d $REPLICATION_DIR_2 ];then
  echo "DEBUG: Replication Directory: $REPLICATION_DIR_2"

  PROCESS_ID=$$
  DATUM=`date +%Y.%m.%d_%H.%M.%S`
  TEMP_ID=$DATUM\_$PROCESS_ID

  TEMP_DIR=$REPLICATION_DIR_2/temp\_$TEMP_ID
  mkdir -p $TEMP_DIR
  echo "DEBUG: Temporary Directory: $TEMP_DIR"

  if [ -d $PENDING_DIR_2 ];then
    echo "DEBUG: Pending Directory: $PENDING_DIR_2"

    if [ -d $PENDING_DIR_2 ];then
      mv $PENDING_DIR_2/* $TEMP_DIR/.
    fi


#    $JAVA -classpath $CLASSPATH org.wyona.xps.publish.Replicator $TEMP_DIR




    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_2_1/.
      $SCP -r $TEMP_DIR/* $RU_2_1@$RH_2_1:$RDOCS_2_1/.
    fi
    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_2_2/.
      $SCP -r $TEMP_DIR/* $RU_2_2@$RH_2_2:$RDOCS_2_2/.
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR_2"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_DIR_2"
  exit 0
fi

# Loop over all publications (END)

date
echo "STOP"
