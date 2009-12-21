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

<!-- $Id: xhtml2xhtml.xsl 201776 2005-06-25 18:25:26Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:meta="http://apache.org/cocoon/lenya/metadata/1.0"
  xmlns:doc="http://apache.org/lenya/metadata/document/1.0"
  xmlns:i="http://apache.org/cocoon/include/1.0"
  >
  
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="format"/>
  
  <xsl:template match="col:document[@uuid]">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <i:include src="cocoon:/feedContent/{doc:resourceType}/{$format}/{$pub}/{$area}/{@uuid}/{@xml:lang}" strip-root="true"/>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="doc:resourceType"/>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet> 
