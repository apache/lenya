#!/bin/sh

SOURCE_DIRECTORY=/home/michiii

DESTINATION_DIRECTORY=/home/michiii/tmp

#########################################################
# FUNCTIONS
#########################################################


browseDirectory(){
  CURRENT_DIRECTORY=$1
  echo "Browsing Directory: $CURRENT_DIRECTORY"

  FILES_AND_DIRS=`ls $CURRENT_DIRECTORY`
  NUMBER_OF_FILES_AND_DIRS=`ls $CURRENT_DIRECTORY | wc -l`

  echo "Number of files and dirs: $NUMBER_OF_FILES_AND_DIRS"
  if [ $NUMBER_OF_FILES_AND_DIRS = 0 ];then
    echo "No Files or Dirs within directory"
    return
  fi

  for FILE_OR_DIR in $FILES_AND_DIRS; do
    if [ -d $CURRENT_DIRECTORY/$FILE_OR_DIR ]; then
      echo "Directory: $CURRENT_DIRECTORY/$FILE_OR_DIR"
      browseDirectory $CURRENT_DIRECTORY/$FILE_OR_DIR
    fi
    if [ -f $CURRENT_DIRECTORY/$FILE_OR_DIR ]; then
      echo "File: $CURRENT_DIRECTORY/$FILE_OR_DIR"
    else
      echo "Exception: Neither File nor Directory: $CURRENT_DIRECTORY/$FILE_OR_DIR"
    fi
  done
  }


#########################################################
# MAIN
#########################################################


browseDirectory $SOURCE_DIRECTORY
