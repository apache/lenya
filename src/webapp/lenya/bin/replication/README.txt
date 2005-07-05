
   R E A D M E
   ===========

   Requirements
   ------------

   1) Ant 1.6.X or higher

   2) Download the additonal libraries

        - ant-contrib-1.0b1.jar (http://ant-contrib.sourceforge.net/)
        - jsch-20040329.jar (http://www.jcraft.com/jsch/index.html, http://www.ibiblio.org/maven2/ant/ant-jsch/)

      and copy them to the lib dir of ant (e.g. /usr/local/apache-ant-1.6.2/lib)

   3) Create an SSH key

        ssk-keygen -t dsa (or -t rsa)

      and copy to remote server 

        scp .ssh/id_dsa.pub REMOTE_SERVER:.ssh/authorized_keys


   Howto Replicate
   ---------------

   1) Copy build.properties to local.build.properties

   2) Set the following parameters within local.build.properties

      - pub.dir
      - local.keyfile
      - default.username
      - default.remote.dir
      - remote1.host (uncomment and set the other remote servers if necessary)

   3) Replicate the content by running ant -f build.xml
