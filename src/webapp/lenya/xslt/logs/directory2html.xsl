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
    >
    
<xsl:param name="test"/>

<xsl:template match="/">
  <html>
    <head>
      <title>Task Log History</title>
    </head>
    <body>
      <h1>Task Log History: <xsl:value-of select="$test"/></h1>
      <xsl:apply-templates/>
    </body>
  </html>
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
  <li><a href="{substring-before(@name, '.xml')}.html"><xsl:value-of select="$formatted-date"/></a></li>
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
