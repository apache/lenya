<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : newsletter-mail.xsl
    Created on : November 19, 2002, 11:28 AM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:mail="http://www.wyona.org/2002/mail">

<xsl:output method="text"/>
    
<xsl:strip-space elements="footer"/>
    
<xsl:include href="articles.xsl"/>
    
<!-- template rule matching source root element -->
<xsl:template match="/">
  <mail:mail>
    <xsl:apply-templates select="newsletter/email/*"/>
    <mail:body>
      <xsl:value-of select="newsletter/title"/>
      <xsl:text>&#10;</xsl:text>
      <xsl:call-template name="underline">
        <xsl:with-param name="title" select="newsletter/title"/>
      </xsl:call-template>
      <xsl:text>&#10;</xsl:text>
      <xsl:text>&#10;</xsl:text>
      <xsl:apply-templates select="newsletter/abstract"/>
      <xsl:text>&#10;</xsl:text>
      <xsl:text>&#10;</xsl:text>
      <xsl:apply-templates select="newsletter/articles"/>
      <xsl:apply-templates select="newsletter/footer"/>
    </mail:body>
  </mail:mail>
</xsl:template>

<xsl:template match="text()">
  <xsl:value-of select="normalize-space(.)"/>
</xsl:template>

<xsl:template match="br">
   <xsl:text>&#10;</xsl:text>
</xsl:template>

<xsl:template match="email/to">
  <mail:to><xsl:apply-templates/></mail:to>
</xsl:template>

<xsl:template match="email/cc">
  <mail:cc><xsl:apply-templates/></mail:cc>
</xsl:template>

<xsl:template match="email/subject">
  <mail:subject><xsl:apply-templates/></mail:subject>
</xsl:template>

<xsl:template match="articles">
  <xsl:apply-templates/>
</xsl:template>                

<xsl:template match="articles/article">
    <xsl:value-of select="body.head/hedline/hl1"/>
    <xsl:text>&#10;</xsl:text>
    <xsl:call-template name="underline">
      <xsl:with-param name="title" select="body.head/hedline/hl1"/>
    </xsl:call-template>
    <xsl:text>&#10;</xsl:text>
    <xsl:choose>
      <xsl:when test="body.head/teasertext!=''">
        <xsl:value-of select="body.head/teasertext"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="body.head/abstract"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="body.head/dateline/story.date/@norm!=''">
        <xsl:text/>(<xsl:value-of select="body.head/dateline/story.date/@norm"/>)<xsl:text/>
      </xsl:when>
      <xsl:otherwise>
      <xsl:text>&#10;(Noch nie publiziert)</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>&#10;Mehr unter:&#10;</xsl:text>
    <xsl:value-of select="concat($server-uri, '/', @href, '/index.html')"/>
    <xsl:text>&#10;</xsl:text>
    <xsl:text>&#10;</xsl:text>
    <xsl:text>&#10;</xsl:text>
</xsl:template>

</xsl:stylesheet> 
