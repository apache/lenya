<?xml version="1.0"?>
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

<!-- $Id: howto2document.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

 <xsl:import href="copyover.xsl"/>

  <!-- Processing a raw howto without revisions -->
  <xsl:template match="/howto">
    <document>
      <xsl:copy-of select="header"/>
      <body>
        <xsl:apply-templates/>
      </body>
    </document>
  </xsl:template>

  <!-- Processing a howto combined with revisions -->
  <xsl:template match="/all">
   <document>
    <xsl:copy-of select="howto/header"/>
     <body>
        <xsl:apply-templates select="howto"/>
        <xsl:apply-templates select="revisions"/>
     </body>
   </document>
  </xsl:template>
  
  <xsl:template match="howto">
    <xsl:if test="normalize-space(header/abstract)!=''">
      <xsl:apply-templates select="header/abstract"/>
    </xsl:if>
     <xsl:apply-templates select="*[not(name()='header')]"/>
  </xsl:template>
  
  <xsl:template match="howto/header/abstract">
    <section id="Overview">
     <title>Overview</title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="purpose | prerequisites | audience | steps | extension  | faqs | tips | references | feedback ">
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="normalize-space(@title)!=''">
          <xsl:value-of select="@title"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <section id="{$title}">
        <title><xsl:value-of select="$title"/></title>
     <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="faq">
    <section>
     <title>
      <xsl:value-of select="question"/>
     </title>
      <xsl:apply-templates select="answer" />
    </section>
  </xsl:template>
  
   <xsl:template match="answer">
      <xsl:copy-of select="."/>
    </xsl:template>
    
   <xsl:template match="question">
    </xsl:template>
  
  <xsl:template match="revisions">
    <section id="revisions">
     <title>Revisions</title>
    <p>Find a problem with this document? Consider contacting the author or submitting your own revision. For instructions, read the How To Submit a Revision.</p>
      <ul>
       <xsl:apply-templates select="revision"/>
      </ul>
    </section>
  </xsl:template>
  
  <xsl:template match="revision">
  <xsl:variable name="href"><xsl:value-of select="concat(substring-before(@name,'.xml'),'.html')" /></xsl:variable>
   <li>Revision, <a href="{ $href}"><xsl:value-of select="@date"/></a></li>
  </xsl:template>
  
</xsl:stylesheet>
