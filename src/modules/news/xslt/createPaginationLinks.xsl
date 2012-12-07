<?xml version="1.0" encoding="UTF-8" ?>
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

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:meta="http://apache.org/lenya/meta/1.0/"
    xmlns:doc="http://apache.org/lenya/metadata/document/1.0"
    xmlns:page="http://apache.org/cocoon/paginate/1.0" 
    xmlns:col="http://apache.org/cocoon/lenya/collection/1.0" 
    exclude-result-prefixes="page"
    >
    
  <xsl:template match="page:page">
  
    <col:page id="pagination">
      <!-- create 'previous' link -->
      <xsl:if test="@current &gt; 1">
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="@current-uri" />
          <xsl:text>?page=</xsl:text>
          <xsl:value-of select="number(@current)-1" />
        </xsl:attribute>
        <xsl:text>&lt;</xsl:text>
      </a>
      </xsl:if>
      <!-- show current page of total pages -->
      <span id="current"><xsl:value-of select="@current"/></span>
      <span><xsl:text> of </xsl:text></span>
      <span id="total"><xsl:value-of select="@total"/></span>
      <!-- create 'next' link -->
      <xsl:if test="@current &lt; @total">
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="@current-uri" />
            <xsl:text>?page=</xsl:text>
            <xsl:value-of select="number(@current)+1" />
          </xsl:attribute>
          <xsl:text>&gt;</xsl:text>
        </a>
      </xsl:if>
    </col:page>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  

</xsl:stylesheet> 
