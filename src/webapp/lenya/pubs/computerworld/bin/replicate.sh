#!/bin/sh
#
##############################################################
#        GENERAL CONFIGURATION                               #
##############################################################
#

SCP=/usr/bin/scp

CONTEXT=/home/wyona/build/jakarta-tomcat-4.0.6/webapps/lenya
PUBLICATION_DIR=$CONTEXT/lenya/pubs


#
##############################################################
#        PUBLICATION CONFIGURATION                           #
##############################################################
#

PUBLICATION_ID_1=computerworld

# location of HTML docs for replication
EXPORT_DIR_1=$PUBLICATION_DIR/computerworld/resources/publication/export
PENDING_DIR_1=$EXPORT_DIR_1/pending/lenya/computerworld
REPLICATION_DIR_1=$EXPORT_DIR_1/replication

# User and Host
RU=wyona
RH=192.168.98.238

# HTML docs for Apache (static caching)
RDOCS_1=/usr/local/apache/htdocs

# location of XML docs for replication
REPLICATION_DIR_2=$PUBLICATION_DIR/computerworld/docs/publication/replication/pending

# XML docs for Cocoon
# copy to same location for now.
RDOCS_2=/home/wyona/build/jakarta-tomcat-4.0.6/webapps/lenya/lenya/pubs/computerworld/docs/publication/live

# pictures for Cocoon
# copy to same location for now.
RDOCS_3=/home/wyona/build/jakarta-tomcat-4.0.6/webapps/lenya/lenya/pubs/computerworld/resources/publication/images/live

###########################################
#                MAIN                     #
###########################################

echo "START"
date

echo ""
echo "=================================================="
echo "= PUBLICATION: $PUBLICATION_ID_1"
echo "=================================================="
echo ""

# copy HTML docs to Apache
#    if [ -d $TEMP_DIR ];then
#      $SCP -r $TEMP_DIR/* $RU_1_1@$RH_1_1:$RDOCS_1_1/.
#    fi

# copy XML docs to Cocoon
    if [ -d $REPLICATION_DIR_2 ];then
      $SCP -r $REPLICATION_DIR_2/* $RU@$RH:$RDOCS_2/.
      rm -rf $REPLICATION_DIR_2/*
    fi

# copy Images to Cocoon
    if [ -d $RDOCS_3 ];then
      $SCP -r $RDOCS_3/* $RU@$RH:$RDOCS_3/.
    fi

date
echo "STOP"
