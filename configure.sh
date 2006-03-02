#!/bin/sh

#  Copyright 1999-2004 The Apache Software Foundation
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
                                                                                                                                                             
# ----- Verify and Set Required Environment Variables -------------------------
                                                                                                                                                             
if [ "$TERM" = "cygwin" ] ; then
  S=';'
else
  S=':'
fi
                                                                                                                                                             
# ----- Ignore system CLASSPATH variable
OLD_CLASSPATH="$CLASSPATH"
unset CLASSPATH
CLASSPATH="`echo tools/configure/build | tr ' ' $S`"
export CLASSPATH
#echo "$CLASSPATH"

DEFAULT_UI_TYPE=cmd
UI_TYPE=$1
if [ "$UI_TYPE" = "" ];then
  UI_TYPE=$DEFAULT_UI_TYPE
fi
#echo $UI_TYPE

echo "WARNING: This shell script has not been finished yet! Use at own risk ;-)"

if [ "$UI_TYPE" = "cmd" ];then
  java org.apache.lenya.config.ConfigureCommandLine
elif [ "$UI_TYPE" = "gui" ]; then
  java org.apache.lenya.config.ConfigureGUI
else
  echo "No such User Interface: $UI_TYPE"
  exit 1
fi
ERR=$?

# ----- Restore CLASSPATH
CLASSPATH="$OLD_CLASSPATH"
export CLASSPATH
unset OLD_CLASSPATH

# Build status return
# Usage: e.g. bash: ./build.sh; if [ $? -ne 0 ]; then echo "Build FAILED"; fi
exit $ERR
