<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id: upper2lower_case.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml"/>

<xsl:template match="*"> <!-- "*" = all elements-->
   <xsl:element name="{translate(name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 
'abcdefghijklmnopqrstuvwxyz')}">
   <xsl:apply-templates select="node()|@*"/>
    </xsl:element>
</xsl:template>

<xsl:template match="@*"> <!-- "*" = all attributes of elements-->
   <xsl:attribute  name="{translate(name(), 
'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')}">
     <xsl:value-of select="."/>
   </xsl:attribute>
</xsl:template>

<xsl:template match="node()" priority="-1">
	<xsl:copy>
		<xsl:apply-templates select="node()|@*"/>
	</xsl:copy>
</xsl:template>


</xsl:stylesheet>
