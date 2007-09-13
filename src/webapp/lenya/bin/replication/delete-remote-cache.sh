#!/bin/sh

REMOTE_USER=$1
REMOTE_HOST=$2
REMOTE_CACHE_DIR=$3

echo "[delete-remote-cache.sh]: Remote cache dir: $REMOTE_CACHE_DIR"

#ssh $REMOTE_USER@$REMOTE_HOST "ls $REMOTE_CACHE_DIR;exit"

ssh $REMOTE_USER@$REMOTE_HOST "\rm -rf $REMOTE_CACHE_DIR;exit"

echo "[delete-remote-cache.sh]: Remote cache dir has been removed"
