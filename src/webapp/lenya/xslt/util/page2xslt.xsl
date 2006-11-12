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
    xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    >

<xsl:param name="contextprefix"/>

<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>


<xsl:template match="xsl:stylesheet">
  <xso:stylesheet version="1.0" xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
  </xso:stylesheet>
</xsl:template>


<xsl:template match="xsl:param[@name='contextprefix']">
  <xso:param name="contextprefix" select="'{$contextprefix}'"/>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
