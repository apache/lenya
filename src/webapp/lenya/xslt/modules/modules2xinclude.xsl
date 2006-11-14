<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: xinclude.xsl 123414 2004-12-27 14:52:24Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.0"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
    >

  <xsl:param name="area"/>

  <xsl:template match="publication">
    <menu:menu>
      <xsl:apply-templates select="module"/>
    </menu:menu>
  </xsl:template>

  <xsl:template match="module">
    <xsl:if test="not(preceding-sibling::module[@name = current()/@name])">
      <xi:include href="cocoon:/menu-xml/module/{$area}/{@name}.xml" xpointer="xpointer(/*/*)"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
