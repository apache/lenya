#!/bin/sh

PARENT_DIR=`dirname $0`
echo $PARENT_DIR
PORT=8080



###########
#
# FUNCTIONS
#
###########

downloadRSS(){
  RSS_FILE=$1

  wget -t 1 -O $RSS_FILE.tmp http://localhost:$PORT/wyona-cms/oscom/http/$RSS_FILE
  ERROR=`grep -l "xmlns:error" $RSS_FILE.tmp`
  if [ $ERROR ];then
    echo "ERROR: http://localhost:$PORT/wyona-cms/oscom/http/$RSS_FILE"
    rm $RSS_FILE.tmp
  else
    echo "NO ERROR"
    mv $RSS_FILE.tmp $RSS_FILE
  fi
  }

###########
#
# MAIN
#
###########

cd $PARENT_DIR
cd ../docs/publication/live/rss-rdf



downloadRSS slashdot.rdf
downloadRSS fm.rdf
downloadRSS cmsinfo.rdf
downloadRSS cmswatch.xml
