#!/bin/ksh -p

BATCH_FILE=$1
if ! [ $BATCH_FILE ];then
  echo "Usage: sftp.sh \"batch-file.txt\""
  exit 0
fi

echo "INFO: Batch File: $BATCH_FILE"

sftp -b $1 user@host.com
