<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: root.xsl,v 1.6 2004/03/13 12:42:14 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="xopus.xsl"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:variable name="context_prefix" select="/lenya/menu/context_prefix"/>

<xsl:template match="lenya">
  <html>
    <xsl:call-template name="xopus_html_attribute"/>
    <xsl:call-template name="xopus_top"/>
    <head>
      <xsl:call-template name="xopus_head"/>
      <title>Authoring</title>
    <style type="text/css">
      <xsl:comment>
        .alenya {
            color: #0066FF;
            text-decoration: none;
        }

        .alenya:visited {
            <!--color: #669999;-->
            color: #0066FF;
            text-decoration: none;
        }
      </xsl:comment>
    </style>
    </head>
    <body bgcolor="#ffffff">
      <xsl:call-template name="xopus_body"/>

      <xsl:apply-templates select="cmsbody"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="xopus" mode="top">
  <xsl:call-template name="xopus_top"/>
</xsl:template>

<xsl:template match="xopus" mode="head">
  <xsl:call-template name="xopus_head"/>
</xsl:template>

<xsl:template match="xopus" mode="body">
  <xsl:call-template name="xopus_body"/>
</xsl:template>

<xsl:template match="xopus" mode="html_attribute">
  <xsl:call-template name="xopus_html_attribute"/>
</xsl:template>
 
</xsl:stylesheet>  
