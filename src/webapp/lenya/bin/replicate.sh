#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
##############################################################
#        GENERAL CONFIGURATION                               #
##############################################################
#

if [ "$TOMCAT_HOME" = "" ] ; then
  echo You must set TOMCAT_HOME to point at your Tomcat installation
  exit 1
fi

CONTEXT=$TOMCAT_HOME/webapps/lenya
PUBLICATION_DIR=$CONTEXT/lenya/pubs

SFTP_BATCH=$CONTEXT/lenya/bin/copy-recursive.sh


#
##############################################################
#        PUBLICATION CONFIGURATION                           #
##############################################################
#


PUBLICATION_ID_2=default
EXPORT_DIR_2=$PUBLICATION_DIR/default/resources/export
PENDING_DIR_2=$EXPORT_DIR_2/pending/lenya/default
REPLICATION_DIR_2=$EXPORT_DIR_2/replication
RU_2_1=username
RH_2_1=127.0.0.1
RDOCS_2_1=/usr/local/jakarta-tomcat-4.0.4-b3/webapps/ROOT
RU_2_2=username
RH_2_2=127.0.0.1
RDOCS_2_2=/usr/local/apache/htdocs_default



###########################################
#                MAIN                     #
###########################################

echo "START"
date

# Loop over all publications (BEGIN)



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

    if [ -d $TEMP_DIR ];then
      scp -r $TEMP_DIR/* $RU_2_1@$RH_2_1:$RDOCS_2_1/.
    fi
    if [ -d $TEMP_DIR ];then
      scp -r $TEMP_DIR/* $RU_2_2@$RH_2_2:$RDOCS_2_2/.
    fi


  else
    echo "WARN: No such directory: $PENDING_DIR_2"
  fi
  rm -r $TEMP_DIR
else
  echo "FATAL: No such directory: $REPLICATION_DIR_2"
  #exit 0
fi

# Loop over all publications (END)

date
echo "STOP"
