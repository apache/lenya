<!-- ===================================================================

     Apache Common Elements (Version 1.0)

PURPOSE:
  Common elements across DTDs

TYPICAL INVOCATION:

  <!ENTITY % common PUBLIC
      "-//APACHE//ENTITIES Common elements Vx.y//EN"
      "common-elems-vxy.mod">
  %common;

  where

    x := major version
    y := minor version

AUTHORS:
  Steven Noels <stevenn@apache.org>

FIXME:

CHANGE HISTORY:
[Version 1.0]
  20020611 Initial version. (SN)

COPYRIGHT:
  Copyright (c) 2002 The Apache Software Foundation.

  Permission to copy in any form is granted provided this notice is
  included in all copies. Permission to redistribute is granted
  provided this file is distributed untouched in all its parts and
  included files.

==================================================================== -->

<!-- =============================================================== -->
<!-- Common entities -->
<!-- =============================================================== -->

<!ENTITY % types "add|remove|update|fix">
<!ENTITY % contexts "build|docs|code|admin|design">

<!-- =============================================================== -->
<!-- Common elements -->
<!-- =============================================================== -->

<!ELEMENT devs (person+)>
<!ATTLIST devs %common.att;>

<!ELEMENT action (%content.mix;)*>
<!ATTLIST action %common.att;
          dev  IDREF  #REQUIRED
          type (%types;)  #IMPLIED
          context (%contexts;) #IMPLIED
          due-to CDATA #IMPLIED
          due-to-email CDATA #IMPLIED
          fixes-bug CDATA #IMPLIED>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
