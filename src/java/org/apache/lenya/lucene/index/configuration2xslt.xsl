<?xml version="1.0" encoding="ISO-8859-1" ?>

<!--
    Document   : configuration2xslt.xsl
    Created on : 17. März 2003, 15:03
    Author     : hrt
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsl-out="http://www.wyona.org/2002/xslt"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:luc="http://www.wyona.org/2003/lucene"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    >

<xsl:namespace-alias stylesheet-prefix="xsl-out" result-prefix="xsl"/>    
<xsl:preserve-space elements="*"/>

<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" />
    
<xsl:template match="luc:document">
  <xsl-out:stylesheet version="1.0" xmlns:ex="http://www.eurexchange.com/2002/markup">
  
    <xsl-out:param name="filename"/>
    
    <xsl:apply-templates select="luc:variable"/>
  
    <xsl-out:template match="/">
      <luc:document>
        <xsl-out:attribute name="filename"><xsl-out:value-of select="$filename"/></xsl-out:attribute>
      
        <xsl:for-each select="luc:field">
          <luc:field name="{@name}" type="{@type}">
            <xsl-out:value-of select="{@xpath}"/>
          </luc:field>
        </xsl:for-each>
      
        <!--<xsl-out:apply-templates/>-->
      </luc:document>
    </xsl-out:template>
  
  </xsl-out:stylesheet>
</xsl:template>


<xsl:template match="luc:variable">
  <xsl-out:variable name="{@name}" select="{@value}"/>
</xsl:template>


<!--
<xsl:template match="luc:field">
  <xsl-out:template match="{@xpath}">
    <luc:field name="{@name}">
      <xsl-out:apply-templates/>
    </luc:field>
  </xsl-out:template>
</xsl:template>
-->

</xsl:stylesheet> 
