#!/bin/sh

DIRNAME=`dirname $0`
echo "dirname: $DIRNAME"

PREFIX=/home/michiii/src/wyonacms
JAVA=/usr/local/jdk1.3.1/bin/java
PDFBOX=/home/michiii/src/PDFBox-0.5.5
XPDF=/home/michiii/bin/xpdf-2.01-linux/pdftotext

CLASSPATH=$PREFIX/build/wyona-cms/classes:$PREFIX/src/cocoon/WEB-INF/lib/avalon-framework-4.1.2.jar:$PREFIX/src/cocoon/WEB-INF/lib/avalon-excalibur-20020402.jar:$PREFIX/src/cocoon/WEB-INF/lib/xml-apis.jar:$PREFIX/src/cocoon/WEB-INF/lib/xercesImpl-2.0.0.jar:$PREFIX/lib/log4j-1.2.3.jar:$PREFIX/src/java

#echo $CLASSPATH

CRAWLER_CONF=$1
LUCENE_CONF=$2

if ! ([ $CRAWLER_CONF ] && [ $LUCENE_CONF ]);then
  echo ""
  echo "Usage: crawl_and_index.sh crawler.xconf lucene.xconf"
  exit 0
fi

echo ""
echo "=========================================================="
echo "Target: crawl"
echo "=========================================================="
echo ""
##$JAVA -cp $CLASSPATH org.wyona.search.crawler.CrawlerEnvironment $CRAWLER_CONF
##$JAVA -cp $CLASSPATH org.wyona.search.crawler.IterativeHTMLCrawler $CRAWLER_CONF


echo ""
echo "=========================================================="
echo "Target: extract_text_from_pdf"
echo "=========================================================="
echo ""
HTDOCS_DUMP_DIR=`$JAVA -cp $CLASSPATH org.wyona.search.crawler.CrawlerEnvironment $CRAWLER_CONF -name htdocs-dump-dir`
##find $HTDOCS_DUMP_DIR -name "*.pdf" -print -exec $XPDF -htmlmeta {} {}.txt \;
find $HTDOCS_DUMP_DIR -name "*.pdf.txt" -print

##$XPDF -htmlmeta $FILE_PDF $FILE_PDF.txt

CLASSPATH=$CLASSPATH:$PDFBOX/classes
##$JAVA -cp $CLASSPATH org.pdfbox.Main $FILE_PDF $FILE_PDF.txt


echo ""
echo "=========================================================="
echo "Target: index"
echo "=========================================================="
echo ""
CLASSPATH=$CLASSPATH:$PREFIX/src/webapp/WEB-INF/lib/lucene-1.3-dev1.jar
##$JAVA -cp $CLASSPATH org.wyona.lucene.IndexEnvironment $LUCENE_CONF
$JAVA -cp $CLASSPATH org.wyona.lucene.IndexHTML $LUCENE_CONF


echo ""
echo "=========================================================="
echo "Target: Regression Test"
echo "=========================================================="
echo ""




echo ""
echo "=========================================================="
echo "Target: Move index and htdocs_dump"
echo "=========================================================="
echo ""
