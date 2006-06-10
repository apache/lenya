
  HOWTO CLONE THE DEFAULT PUBLICATION
  ===================================

  Clone Publication
  -----------------

    - Build Apache Lenya first by executing build.sh resp. build.bat

    - Change to the directory src/webapp/lenya/pubs/default/tools/clone

    - Copy build.properties to local.build.properties and modify the properties to your needs

    - Run ant in order to start the build process to clone the default publication

    - "Re-Build" Apache Lenya by executing build.sh resp. build.bat once more, whereas
      do not forget to add the source dir of the clone publication to the local.build.properties
      of the Apache Lenya build process

    - Startup Apache Lenya according to your settings



  Best Practice
  -------------

    It's very recommended to keep the modifications (e.g. XSLT and CSS) separate from the copy
    of the default publication. Otherwise one might have trouble in upgrading to a new version
    of Apache Lenya.


  A Note on Development
  ---------------------

    After cloning the default publication it's sufficient to run build.sh resp. build.bat.
    The clone process needs only to be replied if the default publication might have been updated by the 
    Apache Lenya committers.
