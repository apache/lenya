#!/bin/sh

# Copyright 1999-2004 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# ----- Verify and Set Required Environment Variables -------------------------

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

if [ "$ANT_HOME" = "" ] ; then
  echo You must set ANT_HOME to point at your Ant installation
  exit 1
fi

if [ "$TOMCAT_HOME" = "" ] ; then
  echo You must set TOMCAT_HOME to point at your Tomcat installation
  exit 1
fi

if [ "$LENYA_SOURCE_DIR" = "" ] ; then
  echo You must set LENYA_SOURCE_DIR to point at your Apache Lenya source directory
  exit 1
fi

###########

export PATH=$ANT_HOME/bin:$PATH

BUILD_FILE=$LENYA_SOURCE_DIR/src/webapp/lenya/bin/crawl_and_index.xml
CONF_PATH=$TOMCAT_HOME/webapps/lenya/lenya/pubs/oscom/config/search

INDICES="-cambridge -lists -conferences -blog"

###########
#
# FUNCTIONS
#
###########

crawlAndIndex(){
    INDEX=$1

    echo ""
    echo "-------------------------------------------------------"
    echo ""
    echo "Build index: $INDEX"
    echo ""
    echo "-------------------------------------------------------"
    echo ""
    ant -f $BUILD_FILE -Dcrawler.xconf=$CONF_PATH/crawler$INDEX.xconf crawl
    ant -f $BUILD_FILE -Dlucene.xconf=$CONF_PATH/lucene$INDEX.xconf index
}

###########
#
# MAIN
#
###########

echo $JAVA_HOME
echo $ANT_HOME
echo $PATH

cd $LENYA_SOURCE_DIR

for INDEX in $INDICES
do
    crawlAndIndex $INDEX
done

crawlAndIndex ""
