#!/bin/sh

DIRNAME=`dirname $0`
echo $DIRNAME

PREFIX=/home/michiii/src/wyonacms

CLASSPATH=$PREFIX/build/wyona-cms/classes:$PREFIX/src/cocoon/WEB-INF/lib/avalon-framework-4.1.2.jar:$PREFIX/src/cocoon/WEB-INF/lib/avalon-excalibur-20020402.jar:$PREFIX/src/cocoon/WEB-INF/lib/xml-apis.jar:$PREFIX/src/cocoon/WEB-INF/lib/xercesImpl-2.0.0.jar:$PREFIX/lib/log4j-1.2.3.jar:$PREFIX/src/java

#echo $CLASSPATH

CRAWLER_CONF=$1

if ! [  $CRAWLER_CONF ];then
  echo ""
  echo "Usage: crawl_and_index.sh crawler.xconf"
  exit 0
fi

echo ""
echo "Target: crawl"
##/usr/local/jdk1.3.1/bin/java -cp $CLASSPATH org.wyona.search.crawler.CrawlerEnvironment $CRAWLER_CONF
##/usr/local/jdk1.3.1/bin/java -cp $CLASSPATH org.wyona.search.crawler.IterativeHTMLCrawler $CRAWLER_CONF


echo ""
echo "Target: extract_text_from_pdf"


echo ""
echo "Target: index"
PUBLICATION_ID=oscom
##PUBLICATION_ID=unipublic
LUCENE_DIR=/home/michiii/build/jakarta-tomcat-4.0.4-b3/webapps/wyona-cms/wyona/cms/pubs/$PUBLICATION_ID/resources/publication/search/lucene
HTDOCS_DUMP_DIR=/home/michiii/build/jakarta-tomcat-4.0.4-b3/webapps/wyona-cms/wyona/cms/pubs/$PUBLICATION_ID/resources/publication/export/pending
##HTDOCS_DUMP_DIR=$LUCENE_DIR/htdocs_dump
INDEX_DIR=$LUCENE_DIR/index
CLASSPATH=$CLASSPATH:$PREFIX/src/webapp/WEB-INF/lib/lucene-1.3-dev1.jar
##/usr/local/jdk1.3.1/bin/java -cp $CLASSPATH org.wyona.lucene.IndexHTML -create -index $INDEX_DIR $HTDOCS_DUMP_DIR
/usr/local/jdk1.3.1/bin/java -cp $CLASSPATH org.wyona.lucene.IndexHTML -index $INDEX_DIR $HTDOCS_DUMP_DIR
