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

<!-- $Id: changesv10tochangesv11.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:preserve-space elements="*" />
  <!-- changes-v10.dtd to changes-v11.dtd transformation -->
  
  <xsl:output doctype-public="-//APACHE//DTD Changes V1.1//EN" doctype-system="changes-v11.dtd"/>
  
  <xsl:template match="/">
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="changes/@title">
    <title><xsl:value-of select="."/></title>
  </xsl:template>

  <xsl:template match="link/@type | link/@actuate | link/@show |
                       jump/@type | jump/@actuate | jump/@show |
                       fork/@type | fork/@actuate | fork/@show"/>

  <!-- the obligatory copy-everything -->
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
