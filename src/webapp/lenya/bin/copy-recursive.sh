#!/bin/sh

SOURCE_DIRECTORY=/home/michiii/tmp

DESTINATION_DIRECTORY=/home/michiii/backup

#########################################################
# FUNCTIONS
#########################################################


browseDirectory(){
  CURRENT_DIRECTORY=$1
  SOURCE_DIR=$2
  DEST_DIR=$3
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
      echo "INFO: .browseDirectory(): Directory: $CURRENT_DIRECTORY/$FILE_OR_DIR"

      createDirectory $SOURCE_DIR $DEST_DIR $CURRENT_DIRECTORY/$FILE_OR_DIR

      browseDirectory $CURRENT_DIRECTORY/$FILE_OR_DIR $SOURCE_DIR $DEST_DIR
      CURRENT_DIRECTORY=`dirname $CURRENT_DIRECTORY`
    else
      if [ -f $CURRENT_DIRECTORY/$FILE_OR_DIR ]; then
        echo "INFO: .browseDirectory(): File: $CURRENT_DIRECTORY/$FILE_OR_DIR"

        copyFile $SOURCE_DIR $DEST_DIR $CURRENT_DIRECTORY/$FILE_OR_DIR
      else
       echo "Exception: Neither File nor Directory: $CURRENT_DIRECTORY/$FILE_OR_DIR"
      fi
    fi
  done
  }

createDirectory(){
  SOURCE_DIR=$1
  DEST_DIR=$2
  LDIRECTORY=$3

  echo "INFO: .createDirectory(): $LDIRECTORY"
  #echo "INFO: .createDirectory(): $SOURCE_DIR"
  #echo "INFO: .createDirectory(): $DEST_DIR"

  LPARENT=`dirname $LDIRECTORY`
  NAME=`basename $LDIRECTORY`
  RELATIVE_DIR=`echo $LDIRECTORY | sed -e "s%$SOURCE_DIR%%g"`
  echo "DEBUG: .copyFile(): Relative Directory: $RELATIVE_DIR"
  DPARENT=`dirname $DEST_DIR$RELATIVE_DIR`


  echo "INFO: .createDirectory(): lcd $LPARENT"
  echo "INFO: .createDirectory(): cd $DPARENT"
  echo "INFO: .createDirectory(): mkdir $NAME"
  echo ""
  }

copyFile(){
  SOURCE_DIR=$1
  DEST_DIR=$2
  LFILE=$3

  echo "INFO: .copyFile(): $LFILE"
  #echo "INFO: .copyFile(): $SOURCE_DIR"
  #echo "INFO: .copyFile(): $DEST_DIR"

  RELATIVE_FILE=`echo $LFILE | sed -e "s%$SOURCE_DIR%%g"`
  echo "DEBUG: .copyFile(): Relative File: $RELATIVE_FILE"
  DFILE=$DEST_DIR$RELATIVE_FILE
  LPARENT=`dirname $LFILE`
  DPARENT=`dirname $DFILE`
  NAME=`basename $LFILE`

  echo "INFO: .copyFile(): lcd $LPARENT"
  echo "INFO: .copyFile(): cd $DPARENT"
  echo "INFO: .copyFile(): put $NAME"
  echo ""
  }


#########################################################
# MAIN
#########################################################


#BATCH_FILE=$1
#if ! [ $BATCH_FILE ];then
#  echo "Usage: copy-recursive.sh \"SourceDirectory DestinationDirectory\""
#  exit 0
#fi


CURRENT_DIRECTORY=$SOURCE_DIRECTORY
browseDirectory $CURRENT_DIRECTORY $SOURCE_DIRECTORY $DESTINATION_DIRECTORY
