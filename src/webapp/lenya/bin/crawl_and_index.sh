#!/bin/sh

DIRNAME=`dirname $0`
echo "INFO: dirname = $DIRNAME"

WEBAPP_DIR=$HOME/src/cocoon-lenya/build/lenya/webapp
LIB_DIR=$WEBAPP_DIR/WEB-INF/lib
JAVA=/usr/lib/j2sdk1.4/bin/java
PDFBOX=/home/username/src/PDFBox-0.5.5
XPDF=/home/username/bin/xpdf-2.01-linux/pdftotext

CLASSPATH=$WEBAPP_DIR/WEB-INF/classes:$LIB_DIR/log4j-1.2.7.jar:$LIB_DIR/xercesImpl-2.5.0.jar:$LIB_DIR/xml-apis.jar:$LIB_DIR/excalibur-io-1.1.jar

echo "INFO: classpath = $CLASSPATH"


case "$1" in
    index)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""
        CLASSPATH=$CLASSPATH:$LIB_DIR/lucene-1.3-dev1.jar
        echo "INFO: classpath = $CLASSPATH"
        LUCENE_CONF=src/webapp/lenya/pubs/oscom/config/search/lucene-cmfsMatrix.xconf
        echo "INFO: lucene.xconf = $LUCENE_CONF"
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.IndexConfiguration $LUCENE_CONF
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.index.Index $LUCENE_CONF true

        ###$JAVA -cp $CLASSPATH org.apache.lenya.lucene.IndexHTML $LUCENE_CONF
	;;
    crawl)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""
	;;
    *)
        echo "Usage: $0 {crawl|index}"
        exit 1
        ;;
esac

exit 0

























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
$JAVA -cp $CLASSPATH org.apache.lenya.search.crawler.CrawlerEnvironment $CRAWLER_CONF
#$JAVA -cp $CLASSPATH org.apache.lenya.search.crawler.IterativeHTMLCrawler $CRAWLER_CONF



echo ""
echo "=========================================================="
echo "Target: extract_text_from_pdf"
echo "=========================================================="
echo ""
HTDOCS_DUMP_DIR=`$JAVA -cp $CLASSPATH org.apache.lenya.search.crawler.CrawlerEnvironment $CRAWLER_CONF -name htdocs-dump-dir`
##find $HTDOCS_DUMP_DIR -name "*.pdf" -print -exec $XPDF -htmlmeta {} {}.txt \;
find $HTDOCS_DUMP_DIR -name "*.pdf.txt" -print

##$XPDF -htmlmeta $FILE_PDF $FILE_PDF.txt

CLASSPATH=$CLASSPATH:$PDFBOX/classes
##$JAVA -cp $CLASSPATH org.pdfbox.Main $FILE_PDF $FILE_PDF.txt

##http://www.adobe.com/products/acrobat/access_simple_form.html



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
