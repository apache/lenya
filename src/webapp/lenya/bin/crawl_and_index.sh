#!/bin/sh

DIRNAME=`dirname $0`
echo "INFO: dirname = $DIRNAME"

HOME=`grep home.dir $DIRNAME/search.properties | grep -v "#" | sed -e 's/home.dir=//'`
echo "INFO: HOME = $HOME"
JAVA=`grep java.run $DIRNAME/search.properties | grep -v "#" | sed -e 's/java.run=//'`
echo "INFO: JAVA = $JAVA"
WEBAPP_DIR=$HOME/src/cocoon-lenya/build/lenya/webapp
LIB_DIR=$WEBAPP_DIR/WEB-INF/lib
#symlink the xpdf directory to the version you have
XPDF=$HOME/build/xpdf-2.03-linux/pdftotext

CLASSPATH=$WEBAPP_DIR/WEB-INF/classes:$LIB_DIR/log4j-1.2.7.jar:$LIB_DIR/xercesImpl-2.6.1.jar:$LIB_DIR/xml-apis.jar:$LIB_DIR/excalibur-io-1.1.jar:$LIB_DIR/xml-commons-resolver-1.1.jar:$LIB_DIR/websphinx.jar

echo "INFO: classpath = $CLASSPATH"


case "$1" in
    crawl)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""

        CRAWLER_CONF=$2

        echo "INFO: crawler.xconf = $CRAWLER_CONF"
        $JAVA -cp $CLASSPATH org.apache.lenya.search.crawler.CrawlerConfiguration $CRAWLER_CONF
        echo ""
        $JAVA -cp $CLASSPATH org.apache.lenya.search.crawler.IterativeHTMLCrawler $CRAWLER_CONF
	;;
    index)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""
        CLASSPATH=$CLASSPATH:$LIB_DIR/lucene-1.3.jar
        echo "INFO: classpath = $CLASSPATH"
        echo ""

        LUCENE_CONF=$2

        echo "INFO: lucene.xconf = $LUCENE_CONF"
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.IndexConfiguration $LUCENE_CONF
        echo ""
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.index.Index $LUCENE_CONF

        ###$JAVA -cp $CLASSPATH org.apache.lenya.lucene.IndexHTML $LUCENE_CONF
	;;
    xpdf)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""

        HTDOCS_DUMP_DIR=$2

        echo "INFO: HTDOCS_DUMP_DIR = $HTDOCS_DUMP_DIR"
        if [ -f $XPDF ]; then
            find $HTDOCS_DUMP_DIR -name "*.pdf" -print -exec $XPDF -htmlmeta {} {}.txt \;
        else
            echo "WARNING: Xpdf not installed: $XPDF"
        fi
	;;
    search)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""
        CLASSPATH=$CLASSPATH:$LIB_DIR/lucene-1.3.jar

        INDEX_DIR=$2
	WORD=$3

        echo "INFO: Index Directory = $INDEX_DIR"
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.SearchFiles $INDEX_DIR $WORD
	;;
    *)
        echo "Usage: $0 {index|xpdf|search}"
        exit 1
        ;;
esac

exit 0
