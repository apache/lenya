#!/bin/sh

DIRNAME=`dirname $0`
echo "dirname: $DIRNAME"

LENYA_PREFIX=/home/michiii/src/lenya
COCOON_PREFIX=/home/michiii/src/cocoon-2.1/build/webapp/WEB-INF/lib
JAVA=/usr/local/j2sdk1.4.1_01/bin/java
PDFBOX=/home/michiii/src/PDFBox-0.5.5
XPDF=/home/michiii/bin/xpdf-2.01-linux/pdftotext

CLASSPATH=$LENYA_PREFIX/build/lenya/classes:$COCOON_PREFIX/avalon-framework-4.1.3.jar:$COCOON_PREFIX/excalibur-io-1.1.jar:$COCOON_PREFIX/xml-apis.jar:$COCOON_PREFIX/xercesImpl-2.1.0.jar:$COCOON_PREFIX/jtidy-04aug2000r7-dev.jar:$LENYA_PREFIX/lib/log4j-1.2.3.jar:$LENYA_PREFIX/lib/websphinx.jar:$LENYA_PREFIX/build/lenya/src

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
$JAVA -cp $CLASSPATH org.lenya.search.crawler.CrawlerEnvironment $CRAWLER_CONF
#$JAVA -cp $CLASSPATH org.lenya.search.crawler.IterativeHTMLCrawler $CRAWLER_CONF



echo ""
echo "=========================================================="
echo "Target: extract_text_from_pdf"
echo "=========================================================="
echo ""
HTDOCS_DUMP_DIR=`$JAVA -cp $CLASSPATH org.lenya.search.crawler.CrawlerEnvironment $CRAWLER_CONF -name htdocs-dump-dir`
##find $HTDOCS_DUMP_DIR -name "*.pdf" -print -exec $XPDF -htmlmeta {} {}.txt \;
find $HTDOCS_DUMP_DIR -name "*.pdf.txt" -print

##$XPDF -htmlmeta $FILE_PDF $FILE_PDF.txt

CLASSPATH=$CLASSPATH:$PDFBOX/classes
##$JAVA -cp $CLASSPATH org.pdfbox.Main $FILE_PDF $FILE_PDF.txt

##http://www.adobe.com/products/acrobat/access_simple_form.html


echo ""
echo "=========================================================="
echo "Target: index"
echo "=========================================================="
echo ""
CLASSPATH=$CLASSPATH:$LENYA_PREFIX/src/webapp/WEB-INF/lib/lucene-1.3-dev1.jar
echo $CLASSPATH
echo $LUCENE_CONF
$JAVA -cp $CLASSPATH org.lenya.lucene.IndexEnvironment $LUCENE_CONF
#$JAVA -cp $CLASSPATH org.lenya.lucene.IndexHTML $LUCENE_CONF
$JAVA -cp $CLASSPATH org.lenya.lucene.index.Index $LUCENE_CONF


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
