#!/bin/sh
#
##############################################################
#        GENERAL CONFIGURATION                               #
##############################################################
#

SCP=/usr/bin/scp

CONTEXT=/home/michiii/build/jakarta-tomcat-4.0.4-b3/webapps/lenya
PUBLICATION_DIR=$CONTEXT/lenya/pubs

SFTP_BATCH=$CONTEXT/lenya/bin/copy-recursive.sh


#
##############################################################
#        PUBLICATION CONFIGURATION                           #
##############################################################
#

PUBLICATION_ID_1=unipublic
EXPORT_DIR_1=$PUBLICATION_DIR/unipublic/resources/export
PENDING_DIR_1=$EXPORT_DIR_1/pending/lenya/unipublic
REPLICATION_DIR_1=$EXPORT_DIR_1/replication
RU_1_1=michiii
RH_1_1=127.0.0.1
RDOCS_1_1=/home/michiii/build/jakarta-tomcat-4.0.4-b3/webapps/ROOT
RU_1_2=michiii
RH_1_2=127.0.0.1
RDOCS_1_2=/usr/local/apache/htdocs_unipublic



PUBLICATION_ID_2=oscom
EXPORT_DIR_2=$PUBLICATION_DIR/oscom/resources/export
PENDING_DIR_2=$EXPORT_DIR_2/pending/lenya/oscom
REPLICATION_DIR_2=$EXPORT_DIR_2/replication
RU_2_1=michiii
RH_2_1=127.0.0.1
RDOCS_2_1=/home/michiii/build/jakarta-tomcat-4.0.4-b3/webapps/ROOT
RU_2_2=michiii
RH_2_2=127.0.0.1
RDOCS_2_2=/usr/local/apache/htdocs_oscom



PUBLICATION_ID_3=forum
EXPORT_DIR_3=$PUBLICATION_DIR/forum/resources/export
PENDING_DIR_3=$EXPORT_DIR_3/pending/lenya/forum
REPLICATION_DIR_3=$EXPORT_DIR_3/replication
RU_3_1=michi
RH_3_1=cvs.lenya.org
RDOCS_3_1=/usr/local/apache/htdocs_oscom/news
RU_3_2=michi
RH_3_2=cvs.lenya.org
RDOCS_3_2=/usr/local/apache/htdocs_oscom/news



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

echo ""
echo "=================================================="
echo "= PUBLICATION: $PUBLICATION_ID_1"
echo "=================================================="
echo ""

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


#    $JAVA -classpath $CLASSPATH org.lenya.xps.publish.Replicator $TEMP_DIR




    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_1_1/.
##      $SCP -r $TEMP_DIR/* $RU_1_1@$RH_1_1:$RDOCS_1_1/.
      sh $SFTP_BATCH $TEMP_DIR $RDOCS_1_1 $REPLICATION_DIR_1 $RU_1_1 $RH_1_1
    fi
    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_1_2/.
##      $SCP -r $TEMP_DIR/* $RU_1_2@$RH_1_2:$RDOCS_1_2/.
      sh $SFTP_BATCH $TEMP_DIR $RDOCS_1_2 $REPLICATION_DIR_1 $RU_1_2 $RH_1_2
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR_1"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_DIR_1"
  #exit 0
fi



#### PUBLICATION 2

echo ""
echo "=================================================="
echo "= PUBLICATION: $PUBLICATION_ID_2"
echo "=================================================="
echo ""

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


#    $JAVA -classpath $CLASSPATH org.lenya.xps.publish.Replicator $TEMP_DIR




    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_2_1/.
      $SCP -r $TEMP_DIR/* $RU_2_1@$RH_2_1:$RDOCS_2_1/.
##      sh $SFTP_BATCH $TEMP_DIR $RDOCS_2_1 $REPLICATION_DIR_2 $RU_2_1 $RH_2_1
    fi
    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_2_2/.
      $SCP -r $TEMP_DIR/* $RU_2_2@$RH_2_2:$RDOCS_2_2/.
##      sh $SFTP_BATCH $TEMP_DIR $RDOCS_2_2 $REPLICATION_DIR_2 $RU_2_2 $RH_2_2
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR_2"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_DIR_2"
  #exit 0
fi



#### PUBLICATION 3

echo ""
echo "=================================================="
echo "= PUBLICATION: $PUBLICATION_ID_3"
echo "=================================================="
echo ""

mkdir $REPLICATION_DIR_3

if [ -d $REPLICATION_DIR_3 ];then
  echo "DEBUG: Replication Directory: $REPLICATION_DIR_3"

  PROCESS_ID=$$
  DATUM=`date +%Y.%m.%d_%H.%M.%S`
  TEMP_ID=$DATUM\_$PROCESS_ID

  TEMP_DIR=$REPLICATION_DIR_3/temp\_$TEMP_ID
  mkdir -p $TEMP_DIR
  echo "DEBUG: Temporary Directory: $TEMP_DIR"

  if [ -d $PENDING_DIR_3 ];then
    echo "DEBUG: Pending Directory: $PENDING_DIR_3"

    if [ -d $PENDING_DIR_3 ];then
      mv $PENDING_DIR_3/* $TEMP_DIR/.
    fi


#    $JAVA -classpath $CLASSPATH org.lenya.xps.publish.Replicator $TEMP_DIR




    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_3_1/.
      $SCP -r $TEMP_DIR/* $RU_3_1@$RH_3_1:$RDOCS_3_1/.
    fi
    if [ -d $TEMP_DIR ];then
##      cp -r $TEMP_DIR/* $RDOCS_3_2/.
      $SCP -r $TEMP_DIR/* $RU_3_2@$RH_3_2:$RDOCS_3_2/.
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR_3"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_DIR_3"
  #exit 0
fi

# Loop over all publications (END)

date
echo "STOP"
