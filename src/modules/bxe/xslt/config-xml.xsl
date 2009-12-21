<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0" 
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="BX_xmlfile"/>
<xsl:param name="BX_xhtmlfile"/>
<xsl:param name="BX_xslfile"/>
<xsl:param name="BX_validationfile"/>
<xsl:param name="script"/>
<xsl:param name="callbackscript"/>
<xsl:param name="BX_exitdestination"/>
<xsl:param name="contextmenufile"/>
<xsl:param name="defaultlanguage"/>

<xsl:template match="/config">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>

  <cinclude:include src="{$contextmenufile}"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xmlfile']">
  <file name="BX_xmlfile"><xsl:value-of select="$BX_xmlfile"/></file>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xhtmlfile']">
  <xsl:if test="$BX_xhtmlfile">
    <file name="BX_xhtmlfile"><xsl:value-of select="$BX_xhtmlfile"/></file>
  </xsl:if>

</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xslfile']">
  <xsl:if test="$BX_xslfile">
    <file name="BX_xslfile"><xsl:value-of select="$BX_xslfile"/></file>
  </xsl:if>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_validationfile']">
  <xsl:if test="$BX_validationfile">
    <file name="BX_validationfile"><xsl:value-of select="$BX_validationfile"/></file>
  </xsl:if>

</xsl:template>

<xsl:template match="files/output/file[@name = 'BX_exitdestination']">
  <file name="BX_exitdestination"><xsl:value-of select="$BX_exitdestination"/></file>
</xsl:template>

<xsl:template match="files/css/file"/>

  <xsl:template match="files/scripts/file[position()=last()]">
    <file><xsl:value-of select="."/></file>
    <xsl:if test="$script">
      <file><xsl:value-of select="$script"/></file>
    </xsl:if>
    <xsl:if test="$callbackscript">
      <file><xsl:value-of select="$callbackscript"/></file>
    </xsl:if>
  </xsl:template>

<!-- pass default language to link screen for sitetree display -->  
<xsl:template match="callbacks/element[@name = 'a']/text()">
  <xsl:value-of select="."/>&amp;language=<xsl:value-of select="$defaultlanguage"/>
</xsl:template>
  

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>

  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet>