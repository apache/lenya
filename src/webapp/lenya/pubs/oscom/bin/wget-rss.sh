#!/bin/sh

PARENT_DIR=`dirname $0`
echo $PARENT_DIR

cd $PARENT_DIR
cd ../docs/publication/live/rss-rdf

wget -O slashdot.rdf.tmp http://localhost:8080/wyona-cms/oscom/http/slashdot.rdf
ERROR=`grep -l "xmlns:error" slashdot.rdf.tmp`
if [ $ERROR ];then
  echo "ERROR: http://localhost:8080/wyona-cms/oscom/http/slashdot.rdf"
  rm slashdot.rdf.tmp
else
  echo "NO ERROR"
  mv slashdot.rdf.tmp slashdot.rdf
fi

wget -O fm.rdf.tmp http://localhost:8080/wyona-cms/oscom/http/fm.rdf
ERROR=`grep -l "xmlns:error" fm.rdf.tmp`
if [ $ERROR ];then
  echo "ERROR: http://localhost:8080/wyona-cms/oscom/http/fm.rdf"
  rm fm.rdf.tmp
else
  echo "NO ERROR"
  mv fm.rdf.tmp fm.rdf
fi

wget -O cmswatch.xml.tmp http://localhost:8080/wyona-cms/oscom/http/cmswatch.xml
ERROR=`grep -l "xmlns:error" cmswatch.xml.tmp`
if [ $ERROR ];then
  echo "ERROR: http://localhost:8080/wyona-cms/oscom/http/cmswatch.xml"
  rm cmswatch.xml.tmp
else
  echo "NO ERROR"
  mv cmswatch.xml.tmp cmswatch.xml
fi
