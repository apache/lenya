<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="board"/>
</xsl:template>

<xsl:template match="board">
 <font face="verdana">
 <h3>Board of Directors</h3>
<p>
The board of directors of OSCOM is responsible
for the management and oversight of the business and 
affairs of the organization in accordance with the bylaws.
</p>
 <p>
 The current board of directors includes:
 </p>
 <ul>
 <xsl:apply-templates select="member"/>
 </ul>
 </font>
</xsl:template>

<xsl:template match="member">
 <li>
 <xsl:choose>
  <xsl:when test="email">
    <a href="mailto:{email}"><xsl:value-of select="name"/></a>
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="name"/>
  </xsl:otherwise>
 </xsl:choose>
 </li>
</xsl:template>
 
</xsl:stylesheet>  
