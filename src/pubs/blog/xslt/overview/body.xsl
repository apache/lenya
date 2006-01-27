<?xml version="1.0" encoding="iso-8859-1"?>
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
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:blog="http://apache.org/cocoon/blog/1.0" version="1.0">

  <xsl:template match="blog:overview" mode="overview">
    <h2>Archive</h2>	  
    <div class="overview">
      <xsl:apply-templates mode="overview"/>
    </div>	
  </xsl:template>

  <xsl:template match="blog:year" mode="overview">
    <div class="overview-year">
      <xsl:value-of select="@id"/>		  
    </div>   
     <xsl:apply-templates mode="overview"/> 
  </xsl:template>

  <xsl:template match="blog:month" mode="overview">
    <div class="overview-month">
      <xsl:value-of select="@id"/>
    </div>
     <xsl:apply-templates mode="overview"/> 
  </xsl:template>

  <xsl:template match="blog:day" mode="overview">
    <div class="overview-day">
      <xsl:value-of select="@id"/>
    </div>
     <xsl:apply-templates mode="overview"/> 
  </xsl:template>

  <xsl:template match="blog:entry" mode="overview">
    <div class="overview-entry">
      <a href="{@url}"><xsl:value-of select="@title"/></a>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
