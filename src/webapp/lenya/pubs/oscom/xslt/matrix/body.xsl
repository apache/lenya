
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="system"/>
</xsl:template>

<xsl:template match="system">
 <font face="verdana">

 <h3>Content Management
<xsl:choose>
  <xsl:when test="@type='cms'">
System
  </xsl:when>
  <xsl:when test="@type='framework'">
Framework
  </xsl:when>
  <xsl:otherwise>
<xsl:value-of select="."/>
  </xsl:otherwise>
</xsl:choose>
</h3>

 <h2><xsl:value-of select="system_name"/></h2>
 <xsl:apply-templates select="description"/>
 <ul>
   <li>Home: <a href="{main_url}" target="_blank"><xsl:apply-templates select="main_url"/></a></li>
   <li><xsl:apply-templates select="license"/></li>
 </ul>
 </font>
</xsl:template>

<xsl:template match="license">
 License: <a href="{license_url}"><xsl:apply-templates select="license_name"/></a>
</xsl:template>

<xsl:template match="description">
  <p><xsl:apply-templates/></p>
</xsl:template>
 
</xsl:stylesheet>  
