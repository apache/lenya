#!/bin/sh

export JAVA_HOME=/usr/local/j2sdk1.4.1_02
export ANT_HOME=/home/michi/build/jakarta-ant-1.5.1
export PATH=/home/michi/build/jakarta-ant-1.5.1/bin:$PATH

LENYA_SOURCE_DIR=/home/michi/src/lenya
TOMCAT_HOME=/home/michi/build/jakarta-tomcat-4.1.21-LE-jdk14



###########



BUILD_FILE=$LENYA_SOURCE_DIR/src/webapp/lenya/bin/crawl_and_index.xml
CONF_PATH=$TOMCAT_HOME/webapps/lenya/lenya/pubs/oscom/content

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
