<?xml version="1.0" encoding="UTF-8"?>
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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
  xmlns:D="DAV:"
  >
  
<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>
  
<xsl:template match="/">
  
  <xso:stylesheet version="1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xhtml">
    
  <xso:output method="xml" indent="yes"/>
    
  <xso:template match="node()|@*">
    <xso:copy>
      <xso:apply-templates select="node()|@*"/>
    </xso:copy>
  </xso:template>
  
  <xso:template match="D:displayname">
    <xsl:if test="//D:prop/D:displayname"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  <xso:template match="D:getlastmodified">
    <xsl:if test="//D:prop/D:getlastmodified"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  <xso:template match="D:creationdate">
    <xsl:if test="//D:prop/D:creationdate"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  <xso:template match="D:resourcetype">
    <xsl:if test="//D:prop/D:resourcetype"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  <xso:template match="D:getcontenttype">
    <xsl:if test="//D:prop/D:getcontenttype"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  <xso:template match="D:contentlength">
    <xsl:if test="//D:prop/D:contentlength"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  <xso:template match="D:lockdiscovery">
    <xsl:if test="//D:prop/D:lockdiscovery"><xso:copy-of select="."/></xsl:if>
  </xso:template>
  
  </xso:stylesheet>
  
  </xsl:template>
  
</xsl:stylesheet>
