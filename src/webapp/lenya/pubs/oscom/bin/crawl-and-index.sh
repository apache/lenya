#!/bin/sh

export JAVA_HOME=/usr/java/j2sdk1.4.1_01
export ANT_HOME=/home/michael/build/jakarta-ant-1.5.1
export PATH=/home/michael/build/jakarta-ant-1.5.1/bin:$PATH

LENYA_SOURCE_DIR=/home/michael/src/lenya
TOMCAT_HOME=/home/michael/build/jakarta-tomcat-4.1.21-LE-jdk14



###########



BUILD_FILE=$LENYA_SOURCE_DIR/src/webapp/lenya/bin/crawl_and_index.xml
CONF_PATH=$TOMCAT_HOME/webapps/lenya/lenya/pubs/oscom/config/search

INDICES="-cambridge -lists -conferences -blog"
#INDICES="-blog"


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

#ant -f $BUILD_FILE -projecthelp

cd $LENYA_SOURCE_DIR

for INDEX in $INDICES
do
    crawlAndIndex $INDEX
done

crawlAndIndex ""
