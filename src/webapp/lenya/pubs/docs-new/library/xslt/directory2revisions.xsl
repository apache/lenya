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

<!-- $Id: directory2revisions.xsl,v 1.2 2004/03/13 12:42:08 gregor Exp $ -->

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:dir="http://apache.org/cocoon/directory/2.0">
 
 
 <xsl:param name="page" />
 <xsl:variable name="revisionPage"><xsl:value-of select="concat('revision-',$page)" /></xsl:variable>

  <xsl:template match="dir:directory">
  <revisions>
      <xsl:apply-templates select="dir:file" />
  </revisions>
  </xsl:template>

  <xsl:template match="dir:file">
  
 <xsl:if test="starts-with(@name,$revisionPage)" >
  	<revision>
  	  <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
  	  <xsl:attribute name="date"><xsl:value-of select="@date"/></xsl:attribute>
	</revision>
 </xsl:if>
  </xsl:template>

</xsl:stylesheet>
