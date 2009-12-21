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
  xmlns:i="http://apache.org/cocoon/include/1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml">
  
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  <xsl:param name="format"/>
  
  <xsl:template match="col:collection">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="not(@uuid)">
        <xsl:attribute name="uuid"><xsl:value-of select="$uuid"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="not(@language)">
        <xsl:attribute name="xml:lang"><xsl:value-of select="$language"/></xsl:attribute>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="@type = 'children'">
          <xsl:variable name="allLanguages">
            <xsl:choose>
              <xsl:when test="@allLanguages"><xsl:value-of select="@allLanguages"/></xsl:when>
              <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <i:include src="cocoon://modules/collection/collectionWithChildren/{$allLanguages}/{$pub}/{$area}/{$uuid}/{$language}.xml" strip-root="true"/>
        </xsl:when>
        <xsl:when test="@type = 'link'">
          <i:include src="{@href}" strip-root="true">
            <i:parameter name="format" value="{$format}"/>
          </i:include>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="col:document"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>