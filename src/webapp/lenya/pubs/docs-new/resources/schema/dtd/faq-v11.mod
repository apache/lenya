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

<!-- $Id: faq-v11.mod,v 1.2 2004/03/13 12:42:10 gregor Exp $ -->
    
<!-- ===================================================================

     Apache Faq module (Version 1.1)

TYPICAL INVOCATION:

  <!ENTITY % faq PUBLIC
      "-//APACHE//ENTITIES FAQ Vxy//EN"
      "faq-vxy.mod">
  %faq;

  where

    x := major version
    y := minor version

NOTES:

AUTHORS:
  Steven Noels <stevenn@apache.org>

FIXME:

CHANGE HISTORY:
[Version 1.0]
  20020608 Initial version. (SN)

COPYRIGHT:
  Copyright (c) 2002 The Apache Software Foundation.

  Permission to copy in any form is granted provided this notice is
  included in all copies. Permission to redistribute is granted
  provided this file is distributed untouched in all its parts and
  included files.

==================================================================== -->

<!-- =============================================================== -->
<!-- Element declarations -->
<!-- =============================================================== -->

<!ELEMENT faqs (authors?, (faq|part)+)>
<!ATTLIST faqs %common.att;
               %title.att;>

    <!ELEMENT part (title, (faq | part)+) >
    <!ATTLIST part %common.att;>

    <!ELEMENT faq (question, answer)>
    <!ATTLIST faq %common.att;>

        <!ELEMENT question (%content.mix;)*>
        <!ATTLIST question %common.att;>

        <!ELEMENT answer (%blocks;)*>
        <!ATTLIST answer author IDREF #IMPLIED>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
