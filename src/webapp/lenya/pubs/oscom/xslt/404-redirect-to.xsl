<?xml version="1.0"?>
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

<!-- $Id: 404-redirect-to.xsl,v 1.2 2004/03/13 12:42:18 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="requestURI" select="'No requestURI'"/>
<xsl:param name="contextPath" select="'No contextPath'"/>

<xsl:template match="/">
<html>
<body>
404
<br/><xsl:value-of select="$requestURI"/>
<br/><xsl:value-of select="$contextPath"/>
</body>
</html>
</xsl:template>
 
</xsl:stylesheet>  
