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

# -----------------------------------------------------------------------------
# Lenya Unix Shell Script
#
# $Id$
# -----------------------------------------------------------------------------

# Configuration variables
#
# LENYA_HOME
#   The root of the Lenya distribution
#
# LENYA_WEBAPP_HOME
#   The root of the Lenya web application
#
# LENYA_LIB
#   Folder containing all the library files needed by the Lenya CLI
#
# JAVA_HOME
#   Home of Java installation.
#
# JAVA_OPTIONS
#   Extra options to pass to the JVM
#
# JAVA_DEBUG_ARGS
#   The command line arguments for the internal JVM debugger
#
# JAVA_PROFILE_ARGS
#   The command line arguments for the internal JVM profiler
#
# JETTY_PORT
#   Override the default port for Jetty
#
# JETTY_ADMIN_PORT
#   The port where the jetty web administration should bind
#


usage()
{
    echo "Usage: $0 (action)"
    echo "actions:"
    echo "  cli               Run Lenya from the command line"
    echo "  servlet           Run Lenya in a servlet container"
    echo "  servlet-admin     Run Lenya in a servlet container and turn on container web administration"
    echo "  servlet-debug     Run Lenya in a servlet container and turn on JVM remote debug"
    echo "  servlet-profile   Run Lenya in a servlet container and turn on JVM profiling"
    exit 1
}

[ $# -gt 0 ] || usage

ACTION=$1
shift
ARGS="$*"

# ----- Verify and Set Required Environment Variables -------------------------

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

if [ "$JAVA_OPTIONS" = "" ] ; then
  JAVA_OPTIONS='-Xms32M -Xmx512M'
fi

if [ "$LENYA_HOME" = "" ] ; then
  LENYA_HOME='.'
fi

if [ "$LENYA_WEBAPP_HOME" = "" ] ; then
  STANDALONE_WEBAPP=../webapp
  if [ -d $STANDALONE_WEBAPP ] ; then
    # for standalone-webapp setup
    LENYA_WEBAPP_HOME=$STANDALONE_WEBAPP
  else
    # when in the build environment
    LENYA_WEBAPP_HOME="$LENYA_HOME/build/lenya/webapp"
  fi
fi
echo "$0: using $LENYA_WEBAPP_HOME as the webapp directory"

if [ "$LENYA_LIB" = "" ] ; then
  LENYA_LIB="$LENYA_WEBAPP_HOME/WEB-INF/lib"
fi

if [ "$JETTY_PORT" = "" ] ; then
  JETTY_PORT='8888'
fi

if [ "$JETTY_ADMIN_PORT" = "" ] ; then
  JETTY_ADMIN_PORT='8889'
fi

if [ "$JAVA_DEBUG_ARGS" = "" ] ; then
  JAVA_DEBUG_ARGS='-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n'
fi

if [ "$JAVA_PROFILE_ARGS" = "" ] ; then
  JAVA_PROFILE_ARGS='-Xrunhprof:heap=all,cpu=samples,thread=y,depth=3'
fi


# ----- Set platform specific variables

PATHSEP=":";
case "`uname`" in
   CYGWIN*) PATHSEP=";" ;;
esac

# ----- Set Local Variables ( used to minimize cut/paste) ---------------------

JAVA="$JAVA_HOME/bin/java"
ENDORSED_LIBS="$LENYA_LIB/endorsed"
ENDORSED="-Djava.endorsed.dirs=$ENDORSED_LIBS"
PARSER=-Dorg.xml.sax.parser=org.apache.xerces.parsers.SAXParser
LOADER=Loader
LOADER_LIB="${LENYA_HOME}/tools/loader"

CLI=-Dloader.main.class=org.apache.lenya.Main
CLI_LIBRARIES="-Dloader.jar.repositories=$LENYA_LIB"

JETTY=-Dloader.main.class=org.mortbay.jetty.plus.Server
JETTY_CONF="$LENYA_HOME/tools/jetty/conf"
JETTY_MAIN="$JETTY_CONF/main.xml"
JETTY_ADMIN="$JETTY_CONF/admin.xml"
JETTY_WEBAPP="-Dwebapp=$LENYA_WEBAPP_HOME"
JETTY_HOME="-Dhome=$LENYA_HOME"
JETTY_PORT_ARGS="-Djetty.port=$JETTY_PORT"
JETTY_ADMIN_ARGS="-Djetty.admin.port=$JETTY_ADMIN_PORT"
JETTY_LIBRARIES="-Dloader.jar.repositories=$LENYA_HOME/tools/jetty/lib${PATHSEP}$LENYA_HOME/tools/jetty/extra/lib${PATHSEP}$LENYA_HOME/tools/jetty/extra/ext${PATHSEP}$LENYA_HOME/tools/jetty/ext${PATHSEP}$LENYA_HOME/tools/jetty/extra/resources${PATHSEP}${ENDORSED_LIBS}"

# ----- Do the action ----------------------------------------------------------

if [ -d build ]; then
    echo "INFO: build/lenya/webapp directory already exists."
else
    if [ -f lenya.war ]; then
        echo "INFO: lenya.war will be unpacked ..."
        mkdir -p build/lenya/webapp
        cp lenya.war build/lenya/webapp/.
        cd build/lenya/webapp
        $JAVA_HOME/bin/jar -xf lenya.war
        cd ../../..
        mkdir -p build/lenya/webapp/WEB-INF/logs
        rm build/lenya/webapp/lenya.war
    else
        echo "INFO: No such war file lenya.war or build directory!"
        exit 1
    fi
fi

case "$ACTION" in
  cli)
        $JAVA $JAVA_OPTIONS -cp $LOADER_LIB $ENDORSED $CLI_LIBRARIES $CLI $LOADER $ARGS
        ;;

  servlet)
        $JAVA $JAVA_OPTIONS -cp $LOADER_LIB $ENDORSED $PARSER $JETTY_PORT_ARGS $JETTY_LIBRARIES $JETTY_WEBAPP $JETTY_HOME $JETTY $LOADER $JETTY_MAIN
        ;;

  servlet-admin)
        $JAVA $JAVA_OPTIONS -cp $LOADER_LIB $ENDORSED $PARSER $JETTY_PORT_ARGS $JETTY_ADMIN_ARGS $JETTY_LIBRARIES $JETTY_WEBAPP $JETTY_HOME $JETTY $LOADER $JETTY_MAIN $JETTY_ADMIN
        ;;

  servlet-debug)
        $JAVA $JAVA_OPTIONS $JAVA_DEBUG_ARGS -cp $LOADER_LIB $ENDORSED $PARSER $JETTY_PORT_ARGS $JETTY_LIBRARIES $JETTY_WEBAPP $JETTY_HOME $JETTY $LOADER $JETTY_MAIN
        ;;

  servlet-profile)
        $JAVA $JAVA_OPTIONS $JAVA_PROFILE_ARGS -cp $LOADER_LIB $ENDORSED $PARSER $JETTY_ARGS $JETTY_LIBRARIES $JETTY_WEBAPP $JETTY_HOME $JETTY $LOADER $JETTY_MAIN
        ;;

  *)
        usage
        ;;
esac

exit 0
