<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: common-elems-v10.mod,v 1.2 2004/03/13 12:42:10 gregor Exp $ -->
    
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
