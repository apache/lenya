#!/bin/sh

PARENT_DIR=`dirname $0`
echo $PARENT_DIR

cd $PARENT_DIR
cd ../docs/publication/live/rss-rdf

rm slashdot.rdf
#wget http://www.slashdot.org/slashdot.rdf
wget http://localhost:8080/wyona-cms/oscom/http/slashdot.rdf
#sed -e 's/xmlns="http:\/\/my.netscape.com\/rdf\/simple\/0.9\/"//g' slashdot.rdf > tmp.rdf
#mv tmp.rdf slashdot.rdf

rm fm.rdf
#wget http://www.freshmeat.net/backend/fm.rdf
wget http://localhost:8080/wyona-cms/oscom/http/fm.rdf
#sed -e 's/xmlns="http:\/\/my.netscape.com\/rdf\/simple\/0.9\/"//g' fm.rdf > tmp.rdf
#mv tmp.rdf fm.rdf

rm cmswatch.xml
wget http://www.cmswatch.com/RSS/cmswatch.xml
