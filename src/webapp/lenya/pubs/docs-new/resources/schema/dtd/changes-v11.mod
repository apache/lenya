<!-- ===================================================================

     Apache Changes Module (Version 1.1)

PURPOSE:
  This DTD was developed to create a simple yet powerful document
  type for software development changes for use with the Apache projects.
  It is an XML-compliant DTD and it's maintained by the Apache XML
  project.

TYPICAL INVOCATION:

  <!ENTITY % changes PUBLIC
      "-//APACHE//ENTITIES Changes Vxy//EN"
      "changes-vxy.mod">
  %changes;

  where

    x := major version
    y := minor version

NOTES:
  It is important, expecially in open developped software projects, to keep
  track of software changes both to give users indications of bugs that might
  have been resolved, as well, and not less important, to provide credits
  for the support given to the project. It is considered vital to provide
  adequate payback using recognition and credits to let users and developers
  feel part of the community, thus increasing development power.

AUTHORS:
  Stefano Mazzocchi <stefano@apache.org>

FIXME:

CHANGE HISTORY:
[Version 1.0]
  19991129 Initial version. (SM)
  20000316 Added bugfixing attribute. (SM)
[Version 1.1]
  20011212 Used public identifiers for external entities (SM)

COPYRIGHT:
  Copyright (c) 2002 The Apache Software Foundation.

  Permission to copy in any form is granted provided this notice is
  included in all copies. Permission to redistribute is granted
  provided this file is distributed untouched in all its parts and
  included files.

==================================================================== -->

<!-- =============================================================== -->
<!-- Document Type Definition -->
<!-- =============================================================== -->

<!ELEMENT changes (title?, devs?, release+)>
<!ATTLIST changes %common.att;>

    <!ELEMENT release (action+)>
    <!ATTLIST release %common.att;
                      version  CDATA  #REQUIRED
                      date     CDATA  #REQUIRED>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
