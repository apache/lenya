<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: directory2html.xsl,v 1.5 2004/03/13 12:42:18 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    >
    

<xsl:template match="/">
  <page:page>
    <page:title>Task Log History</page:title>
    <page:body>
      <xsl:apply-templates/>
    </page:body>
  </page:page>
</xsl:template>    

    
<xsl:template match="dir:directory">
  <ul>
     <xsl:apply-templates/>
  </ul>
</xsl:template>

    
<xsl:template match="dir:file">
  <xsl:variable name="formatted-date">
    <xsl:call-template name="format-date">
      <xsl:with-param name="date" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
  <li><a href="?lenya.usecase=view-logs&amp;lenya.step=log&amp;logfile={@name}"><xsl:value-of select="$formatted-date"/></a></li>
</xsl:template>


<xsl:template name="format-date">
  <xsl:param name="date"/>
  <xsl:value-of select="substring($date, 1, 2)"/>-<xsl:text/>
  <xsl:value-of select="substring($date, 6, 2)"/>-<xsl:text/>
  <xsl:value-of select="substring($date, 9, 2)"/>&#160;&#160;<xsl:text/>
  <xsl:value-of select="substring($date, 12, 2)"/>:<xsl:text/>
  <xsl:value-of select="substring($date, 15, 2)"/>:<xsl:text/>
  <xsl:value-of select="substring($date, 18, 2)"/>.<xsl:text/>
  <xsl:value-of select="substring($date, 21, 3)"/>
</xsl:template>
    
</xsl:stylesheet> 
