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

<!-- $Id: wiki2htmlpage.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:wiki="http://chaperon.sourceforge.net/grammar/wiki/1.0">

 <xsl:output indent="yes" method="xml"/>

 <xsl:template match="/">
  <html>
   <body>
    <xsl:apply-templates/>
   </body>
  </html>
 </xsl:template>


<!-- Identity transformation template -->			
<xsl:template match="@* | * | comment() | processing-instruction() | text()"> 
	<xsl:copy> 
		<xsl:apply-templates select="@* | * | comment() | processing-instruction() | text()"/> 
	</xsl:copy> 
</xsl:template> 
    

</xsl:stylesheet>
