#!/bin/sh


#########################################################
# DEFAULT CONFIGURATION
#########################################################

SOURCE_DIRECTORY=$HOME/tmp

DESTINATION_DIRECTORY=$HOME/backup

BATCH_FILE_DIR=`pwd`
PUT_BATCH_FILE=$BATCH_FILE_DIR/sftp-put-batch.txt
MKDIR_BATCH_FILE=$BATCH_FILE_DIR/sftp-mkdir-batch.txt

USERNAME=$USER

HOSTNAME=127.0.0.1

#########################################################
# FUNCTIONS
#########################################################


toString(){
  echo ""
  echo "INFO: .toString(): SourceDirectory: $SOURCE_DIRECTORY"
  echo "INFO: .toString(): DestinationDirectory: $DESTINATION_DIRECTORY"
  echo "INFO: .toString(): BatchFileDirectory: $BATCH_FILE_DIR"
  echo "INFO: .toString(): BatchFileMkdir: $MKDIR_BATCH_FILE"
  echo "INFO: .toString(): BatchFilePut: $PUT_BATCH_FILE"
  echo "INFO: .toString(): RemoteUsername: $USERNAME"
  echo "INFO: .toString(): RemoteHostname: $HOSTNAME"
  echo ""
  echo ""
  echo ""
  echo ""
  }

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


  #echo "INFO: .createDirectory(): lcd $LPARENT"
  #echo "INFO: .createDirectory(): cd $DPARENT"
  #echo "INFO: .createDirectory(): mkdir $NAME"
  echo ""

  echo "pwd" > $MKDIR_BATCH_FILE
  echo "lcd $LPARENT" >> $MKDIR_BATCH_FILE
  echo "cd $DPARENT" >> $MKDIR_BATCH_FILE
  echo "mkdir $NAME" >> $MKDIR_BATCH_FILE
  echo "quit" >> $MKDIR_BATCH_FILE

  sftp -b $MKDIR_BATCH_FILE $USERNAME@$HOSTNAME
  ##rm $MKDIR_BATCH_FILE
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

  #echo "INFO: .copyFile(): lcd $LPARENT"
  #echo "INFO: .copyFile(): cd $DPARENT"
  #echo "INFO: .copyFile(): put $NAME"
  echo ""

  echo "lcd $LPARENT" >> $PUT_BATCH_FILE
  echo "cd $DPARENT" >> $PUT_BATCH_FILE
  echo "put $NAME" >> $PUT_BATCH_FILE
  }


#########################################################
# MAIN
#########################################################

toString

SOURCE_DIRECTORY=$1
DESTINATION_DIRECTORY=$2
BATCH_FILE_DIR=$3
USERNAME=$4
HOSTNAME=$5
if ! ([ $SOURCE_DIRECTORY ] && [ $DESTINATION_DIRECTORY ] && [ $BATCH_FILE_DIR ] && [ $USERNAME ] && [ $HOSTNAME ]);then
  echo "Usage: copy-recursive.sh \"SourceDirectory DestinationDirectory BatchFileDirectory RemoteUsername RemoteHostname\""
  exit 0
fi

PUT_BATCH_FILE=$BATCH_FILE_DIR/sftp-batch-put.txt
MKDIR_BATCH_FILE=$BATCH_FILE_DIR/sftp-mkdir-batch.txt
PUT_BATCH_FILE=$BATCH_FILE_DIR/sftp-put-batch.txt

toString

#exit 0

echo "pwd" > $PUT_BATCH_FILE
CURRENT_DIRECTORY=$SOURCE_DIRECTORY
browseDirectory $CURRENT_DIRECTORY $SOURCE_DIRECTORY $DESTINATION_DIRECTORY
echo "quit" >> $PUT_BATCH_FILE

sftp -b $PUT_BATCH_FILE $USERNAME@$HOSTNAME
##rm $PUT_BATCH_FILE
