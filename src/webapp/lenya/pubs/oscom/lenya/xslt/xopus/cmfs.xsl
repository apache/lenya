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

<!-- $Id: cmfs.xsl,v 1.2 2004/03/13 12:42:12 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="@*|node()"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template> 
  
<xsl:template match="/">
  <html>
    <head>
      <title><xsl:value-of select="/system/system_name"/></title>
    </head>
    <body bgcolor="white">
      <xsl:apply-templates select="system" />
    </body>
  </html>
</xsl:template>
  
<xsl:template match="system">
  <xsl:apply-templates select="system_name" />
  <xsl:apply-templates select="description" />
</xsl:template>
  
<xsl:template match="system_name">
  <h1><xsl:apply-templates/></h1>
</xsl:template>
  
<xsl:template match="description">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

</xsl:stylesheet>
