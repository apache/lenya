#!/bin/sh

JAVA=/usr/local/jdk1.3.1/bin/java
CATALINA_HOME=/home/michi/build/jakarta-tomcat-4.0
CLASSPATH=$CATALINA_HOME/webapps/wyona-cms/WEB-INF/classes
LIBRARIES=`ls $CATALINA_HOME/webapps/wyona-cms/WEB-INF/lib/`
for LIBRARY in $LIBRARIES
do
  CLASSPATH=$CLASSPATH:$CATALINA_HOME/webapps/wyona-cms/WEB-INF/lib/$LIBRARY
done
#echo $CLASSPATH

SPREFIX=ethz-mat
SNAME='Materials Science'

#NUMBERS='567'
NUMBERS='1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 999 1000'

NUMBER=0
while [ $NUMBER -le 1000 ]
do
  ##echo $NUMBER
  NUMBER=`bc -l << END
         $NUMBER+1 
END`
  echo $NUMBER
##done


##for NUMBER in $NUMBERS
##do
  DPREFIX="$SPREFIX-$NUMBER"
  DNAME="$SNAME $NUMBER"
  echo "Clone Publication: $SPREFIX > $DPREFIX"

  cd $CATALINA_HOME/webapps/wyona-cms/wyona/cms/pubs

  if [ -d $DPREFIX ]
  then
    rm -r $DPREFIX
  fi
  cp -r $SPREFIX $DPREFIX

  cd $DPREFIX

  sed -e "s/$SPREFIX/$DPREFIX/g" sitemap.xmap > sitemap.xmap.tmp
  mv sitemap.xmap.tmp sitemap.xmap

  sed -e "s/$SPREFIX/$DPREFIX/g" stylesheets/Configuration/read.xsl > stylesheets/Configuration/read.xsl.tmp
  mv stylesheets/Configuration/read.xsl.tmp stylesheets/Configuration/read.xsl

  sed -e "s/$SNAME/$DNAME/" stylesheets/wyona/cms/conf/conf.xsl > stylesheets/wyona/cms/conf/conf.xsl.tmp
  mv stylesheets/wyona/cms/conf/conf.xsl.tmp stylesheets/wyona/cms/conf/conf.xsl

  $JAVA -classpath $CLASSPATH org.wyona.cms.cocoon.sitemap.Sitemap $CATALINA_HOME/webapps/wyona-cms/sitemap.xmap "$DPREFIX/**" "$DPREFIX" "wyona/cms/pubs/$DPREFIX/sitemap.xmap"

done
