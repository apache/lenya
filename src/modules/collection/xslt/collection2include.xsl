<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:ci="http://apache.org/cocoon/include/1.0">
  
  
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  <xsl:param name="format"/>
  
  
  <xsl:template match="/col:collection">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="*"/>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="*/col:collection">
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  
  <xsl:template match="col:document">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="not(*)">
        <ci:include src="lenya-document:{@uuid},pub={$pub},area={$area},lang={@xml:lang}?format={$format}"/>
      </xsl:if>
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>