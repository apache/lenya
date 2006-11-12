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
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:template match="/">
<xsl:apply-templates select="bxe"/>
</xsl:template>

<xsl:template match="bxe">
<html>
<xsl:apply-templates select="xhtml:html/xhtml:head"/>
<xsl:copy-of select="xhtml:html/xhtml:body"/>
</html>
</xsl:template>

<xsl:template match="xhtml:head">
<xhtml:head>
<xsl:for-each select="/bxe/namespaces/xmlns">
  <meta name="bxeNS" content="{.}"/>
</xsl:for-each>
<xsl:copy-of select="@*|node()"/>
</xhtml:head>
</xsl:template>
 
</xsl:stylesheet>  
