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


PARENT_DIR=`dirname $0`
echo $PARENT_DIR

cd $PARENT_DIR
cd ../resources/publication/html/live

wget -O tmp.html http://localhost:8080/lenya/oscom/index.html

STATUS=`grep -l "org.apache.cocoon" tmp.html`
if [ $STATUS ];then
  echo "NOT OK"
  rm tmp.html
else
  echo "OK"
  mv tmp.html index.html
  chmod 755 index.html
fi

exit 0

STATUS=`grep -l "status=\"200\"" tmp.html`
if [ $STATUS ];then
  echo "OK"
  mv tmp.html index.html
else
  echo "NOT OK"
  rm tmp.html
fi
