x
  W Y O N A    C O N T E N T - M A N A G E M E N T - S Y S T E M
  ==============================================================


  Prerequisite
  ------------

  You need to have an installed Apache Ant 1.4. 
  
  Point your favorite browser to http://jakarta.apache.org/ant and get a 
  version 1.4 or greater distribution and installation instructions there.


  Installation
  ------------

  1) Get, install, start, try and stop Tomcat

     [unix]  cd /home/wyona/build
             wget http://jakarta.apache.org/builds/jakarta-tomcat-4.0/nightly/jakarta-tomcat-4.0-20020110.tar.gz
             tar -xzf jakarta-tomcat-4.0-20020110.tar.gz
             export JAVA_HOME=/usr/local/IBMJava2-13
             export CATALINA_HOME=/home/wyona/build/jakarta-tomcat-4.0
             /home/wyona/build/jakarta-tomcat-4.0/bin/startup.sh
             lynx http://localhost:8080
             /home/wyona/build/jakarta-tomcat-4.0/bin/shutdown.sh


  2) Get and install Ant

     [unix]  cd /home/wyona/build
             wget http://jakarta.apache.org/builds/jakarta-ant/release/v1.4.1/bin/jakarta-ant-1.4.1-bin.tar.gz
             tar -xzf jakarta-ant-1.4.1-bin.tar.gz


  3) Get Wyona

     [unix]  cd /home/wyona/src
             wget http://www.wyona.org/builds/wyona-cms-2.0/nightly/src/wyona-cms-2.0-src-200201102354.tar.gz
             tar -xzf wyona-cms-2.0-src-200201102354.tar.gz
             cd wyona-cms-2.0-src
             ls


  4) Get, build and copy Cocoon 
     (OPTIONAL: Just in case you want the most recent Cocoon. If not, then go to 5)

     [unix]  cd /home/wyona/src
             wget http://cvs.apache.org/snapshots/xml-cocoon2/xml-cocoon2_20020110171938.tar.gz
             tar -xzf xml-cocoon2_20020110171938.tar.gz
             cd xml-cocoon2
             export JAVA_HOME=/usr/local/IBMJava2-13
             ./build.sh -Dinclude.webapp.libs=yes webapp
             cp build/cocoon/cocoon.war ../wyona-cms-2.0-src/.


  5) Build Wyona

     [unix]  cd /home/wyona/src/wyona-cms-2.0-src
             export JAVA_HOME=/usr/local/IBMJava2-13
             export ANT_HOME=/home/wyona/build/jakarta-ant-1.4.1
             export PATH=/home/wyona/build/jakarta-ant-1.4.1/bin:$PATH
             ant
             ls build/webapp


  6) Install Wyona for Development

     [unix]  cd /home/wyona/build/jakarta-tomcat-4.0/webapps
             ln -s /home/wyona/src/wyona-cms-2.0-src/build/webapp cocoon
             #ln -s /home/wyona/src/wyona-cms-2.0-src/build/webapp wyona-cms
             vi /home/wyona/build/jakarta-tomcat-4.0/conf/server.xml
             To allow class reloading, insert the following line:
             <Context path="/cocoon" docBase="cocoon" debug="0" reloadable="true" crossContext="true"/>
             #<Context path="/wyona-cms" docBase="wyona-cms" debug="0" reloadable="true" crossContext="true"/>


  7) Start Wyona

     [unix]  (/home/wyona/build/jakarta-tomcat-4.0/bin/shutdown.sh)
             (rm -r /home/wyona/build/jakarta-tomcat-4.0/work/*)
             /home/wyona/build/jakarta-tomcat-4.0/bin/startup.sh
             lynx http://localhost:8080/cocoon/
             #lynx http://localhost:8080/wyona-cms/


  8) Start Hacking Wyona

     [unix]  cd /home/wyona/src/wyona-cms-2.0-src
             ls src/main
             ls src/webapp
             vi src/webapp/WEB-INF/logkit.xconf   (DEBUG, ERROR)
             vi src/webapp/cocoon.xconf   (asynchron, synchron)
             ant
             tail -f build/webapp/WEB-INF/logs/*
             lynx http://localhost:8080/cocoon/
             #lynx http://localhost:8080/wyona-cms/
