#!/bin/sh

REMOTE_USER=$1
REMOTE_HOST=$2
REMOTE_RESOURCE=$3

echo "[$0]: Remote resource: $REMOTE_RESOURCE"

#ssh $REMOTE_USER@$REMOTE_HOST "ls $REMOTE_RESOURCE;exit"

ssh $REMOTE_USER@$REMOTE_HOST "\rm -rf $REMOTE_RESOURCE;exit"

echo "[$0]: Remote resource has been removed"
