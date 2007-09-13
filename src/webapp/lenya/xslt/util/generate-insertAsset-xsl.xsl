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

<!-- $Id: generate-insertAsset-xsl.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

<!-- This is a meta xsl which generates another xsl, based on two -->
<!-- params and an xml. The generated xsl is used to insert asset tags -->
<!-- in a document. These asset tags can be very different, i.e. for -->
<!-- images or for pdfs. Hence the generated xsl takes an -->
<!-- configuration xml into account where the inserted tag can be -->
<!-- defined. --> 

<!-- See also O'Reilly's XSLT Cookbook  page 442, "Generating XSLT -->
<!-- from XSLT" --> 

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0" exclude-result-prefixes="xso">
  
  <!-- Let the processor do the formatting via indent = yes -->
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="xsl:text"/>
  
  <!--We use xso as a alias when we need to output literal xslt elements -->
  <xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>
  
  <xsl:param name="assetXPath"/>
  <xsl:param name="insertWhere"/>
  <xsl:param name="insertReplace"/>

  <xsl:template match="/">
    <xso:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:unizh="http://unizh.ch/doctypes/common/1.0" exclude-result-prefixes="unizh">

      <xsl:apply-templates select="//param"/>
      <xsl:apply-templates select="//template"/>
	
	<!-- Identity transformation -->
	<xso:template match="@*|*">
	  <xso:copy>
	    <xso:apply-templates select="@*|node()"/>
	  </xso:copy>
	</xso:template>  
	
    </xso:stylesheet>
  </xsl:template>	

  <xsl:template match="template">
    <!-- Create a template that matches the assetXPath -->
    <xso:template match="{$assetXPath}">
      <xsl:choose>
	<xsl:when test="$insertWhere = 'before'">
	  <xsl:copy-of select="*"/>
	  <xsl:if test="$insertReplace != 'true'">
	    <xso:copy-of select="."/>
	  </xsl:if>
	</xsl:when>
	<xsl:when test="$insertWhere = 'after'">
	  <xsl:if test="$insertReplace != 'true'">
	    <xso:copy-of select="."/>
	  </xsl:if>
	  <xsl:copy-of select="*"/>
	</xsl:when>
	<xsl:when test="$insertWhere = 'inside'">
	  <xso:copy>
	    <xsl:if test="$insertReplace != 'true'">
	      <xso:copy-of select="@*|node()"/>
	    </xsl:if>
	    <xsl:copy-of select="*"/>
	  </xso:copy>
	</xsl:when>
      </xsl:choose>
    </xso:template>
  </xsl:template>	
  
  <xsl:template match="param">
    <xso:param>
      <xsl:copy-of select="@*"/>
    </xso:param>
  </xsl:template>	
  
</xsl:stylesheet>
