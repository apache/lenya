#!/bin/sh

PARENT_DIR=`dirname $0`
echo $PARENT_DIR

cd $PARENT_DIR
cd ../resources/publication/html/live

wget -O tmp.html http://localhost:8080/wyona-cms/oscom/live/index.html
STATUS=`grep -l "status=\"200\"" tmp.html`
if [ $STATUS ];then
  echo "OK"
  mv tmp.html index.html
else
  echo "NOT OK"
  rm tmp.html
fi
