#!/bin/ksh -p

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


BATCH_FILE=$1
if ! [ $BATCH_FILE ];then
  echo "Usage: sftp.sh \"batch-file.txt\""
  exit 0
fi

echo "INFO: Batch File: $BATCH_FILE"

sftp -b $1 user@host.com
