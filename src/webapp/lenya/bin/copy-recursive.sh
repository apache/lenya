#!/bin/sh

SOURCE_DIRECTORY=/home/michiii/tmp

DESTINATION_DIRECTORY=/home/michiii/backup

#########################################################
# FUNCTIONS
#########################################################


browseDirectory(){
  CURRENT_DIRECTORY=$1
  DEST_DIR=$2
  ##echo "DEBUG: Browsing Directory: $CURRENT_DIRECTORY"

  FILES_AND_DIRS=`ls $CURRENT_DIRECTORY`
  NUMBER_OF_FILES_AND_DIRS=`ls $CURRENT_DIRECTORY | wc -l`

  ##echo "DEBUG: Number of files and dirs: $NUMBER_OF_FILES_AND_DIRS"
  if [ $NUMBER_OF_FILES_AND_DIRS = 0 ];then
    ##echo "DEBUG: No Files or Dirs within directory"
    return
  fi

  for FILE_OR_DIR in $FILES_AND_DIRS; do
    ##echo "DEBUG: File or Dir: $FILE_OR_DIR"
    if [ -d $CURRENT_DIRECTORY/$FILE_OR_DIR ]; then
      echo "Directory: $CURRENT_DIRECTORY/$FILE_OR_DIR"
      browseDirectory $CURRENT_DIRECTORY/$FILE_OR_DIR $DEST_DIR
      CURRENT_DIRECTORY=`dirname $CURRENT_DIRECTORY`
    else
      if [ -f $CURRENT_DIRECTORY/$FILE_OR_DIR ]; then
        echo "File: $CURRENT_DIRECTORY/$FILE_OR_DIR"

        cp $CURRENT_DIRECTORY/$FILE_OR_DIR $DEST_DIR/.

        echo $DEST_DIR
      else
       echo "Exception: Neither File nor Directory: $CURRENT_DIRECTORY/$FILE_OR_DIR"
      fi
    fi
  done
  }


#########################################################
# MAIN
#########################################################

CURRENT_DIRECTORY=$SOURCE_DIRECTORY
browseDirectory $CURRENT_DIRECTORY $DESTINATION_DIRECTORY
