<?xml version="1.0" encoding="UTF-8"?>

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
  <xsl:param name="insertBefore"/>

  <xsl:template match="/">
    <xso:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:unizh="http://unizh.ch/doctypes/common/1.0">

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
	<xsl:when test="$insertBefore = 'true'">
	  <xsl:copy-of select="*"/>
	  <xso:copy-of select="."/>
	</xsl:when>
	<xsl:otherwise>
	  <xso:copy-of select="."/>
	  <xsl:copy-of select="*"/>
	</xsl:otherwise>
      </xsl:choose>
    </xso:template>
  </xsl:template>	
  
  <xsl:template match="param">
    <xso:param>
      <xsl:copy-of select="@*"/>
    </xso:param>
  </xsl:template>	
  
</xsl:stylesheet>
