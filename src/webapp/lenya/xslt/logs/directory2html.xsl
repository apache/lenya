<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : directory2html.xsl
    Created on : 7. April 2003, 19:04
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    >
    

<xsl:template match="/">
  <page:page>
    <page:title>Task Log History</page:title>
    <page:body>
      <xsl:apply-templates/>
    </page:body>
  </page:page>
</xsl:template>    

    
<xsl:template match="dir:directory">
  <ul>
     <xsl:apply-templates/>
  </ul>
</xsl:template>

    
<xsl:template match="dir:file">
  <xsl:variable name="formatted-date">
    <xsl:call-template name="format-date">
      <xsl:with-param name="date" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
  <li><a href="?lenya.usecase=view-logs&amp;lenya.step=log&amp;logfile={@name}"><xsl:value-of select="$formatted-date"/></a></li>
</xsl:template>


<xsl:template name="format-date">
  <xsl:param name="date"/>
  <xsl:value-of select="substring($date, 1, 2)"/>-<xsl:text/>
  <xsl:value-of select="substring($date, 6, 2)"/>-<xsl:text/>
  <xsl:value-of select="substring($date, 9, 2)"/>&#160;&#160;<xsl:text/>
  <xsl:value-of select="substring($date, 12, 2)"/>:<xsl:text/>
  <xsl:value-of select="substring($date, 15, 2)"/>:<xsl:text/>
  <xsl:value-of select="substring($date, 18, 2)"/>.<xsl:text/>
  <xsl:value-of select="substring($date, 21, 3)"/>
</xsl:template>
    
</xsl:stylesheet> 
