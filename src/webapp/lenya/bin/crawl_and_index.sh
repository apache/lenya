#!/bin/sh

# Copyright 1999-2004 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


DIRNAME=`dirname $0`
echo "INFO: dirname = $DIRNAME"

WEBAPP_DIR=`grep webapp.dir $DIRNAME/search.properties | grep -v "#" | sed -e 's/webapp.dir=//'`
echo "INFO: WEBAPP_DIR = $WEBAPP_DIR"
JAVA=`grep java.run $DIRNAME/search.properties | grep -v "#" | sed -e 's/java.run=//'`
echo "INFO: JAVA = $JAVA"
LIB_DIR=$WEBAPP_DIR/WEB-INF/lib
XPDF=`grep xpdf.bin $DIRNAME/search.properties | grep -v "#" | sed -e 's/xpdf.bin=//'`

JAVA_XMX=64M

CLASSPATH=$WEBAPP_DIR/WEB-INF/classes:$LIB_DIR/log4j-1.2.7.jar:$LIB_DIR/xercesImpl-2.6.1.jar:$LIB_DIR/xml-apis.jar:$LIB_DIR/excalibur-io-1.1.jar:$LIB_DIR/xml-commons-resolver-1.1.jar:$LIB_DIR/websphinx.jar

echo "INFO: classpath = $CLASSPATH"


case "$1" in
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
            LOG_MESSAGE="Xpdf not installed: $XPDF"
            echo "WARNING: $LOG_MESSAGE"
            java -cp $CLASSPATH org.apache.lenya.util.Log4Echo warn "$LOG_MESSAGE"
        fi
	;;
    search)
        echo ""
        echo "=========================================================="
        echo "Target: $1"
        echo "=========================================================="
        echo ""
        CLASSPATH=$CLASSPATH:$LIB_DIR/lucene-1.4-rc3.jar

        INDEX_DIR=$2
	WORD=$3

        echo "INFO: Index Directory = $INDEX_DIR"
        $JAVA -cp $CLASSPATH org.apache.lenya.lucene.SearchFiles $INDEX_DIR $WORD
	;;
    *)
        echo "Usage: $0 {xpdf|search}"
        exit 1
        ;;
esac

exit 0
