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
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:param name="path"/>
  <xsl:param name="uri"/>
  <xsl:param name="continuationId"/>
  
  
  <xsl:template match="/dir:directory">
    <p style="margin-bottom: 0">
      <xsl:value-of select="$path"/>
    </p>
    <xsl:call-template name="children"/>
  </xsl:template>
  
  
  <xsl:template match="dir:directory">
    <li>
      <a href="{$uri}?lenya.usecase=export.import&amp;lenya.continuation={$continuationId}&amp;path={$path}/{@name}"><xsl:value-of select="@name"/></a>
    </li>
    <xsl:if test="*">
      <xsl:call-template name="children"/>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="children">
    <ul style="margin-top: 0; margin-bottom: 0; list-style-type: none;">
      <xsl:apply-templates select="*">
        <xsl:sort/>
      </xsl:apply-templates>
    </ul>
  </xsl:template>
  
  
</xsl:stylesheet>
