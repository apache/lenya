<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="glossary"/>
</xsl:template>

<xsl:template match="glossary">
 <font face="verdana">
 <h3>Glossary</h3>
<p>
Please submit a new glossary term or a modification of an existing glossary term
by using the <a href="http://www.oscom.org/mailman/listinfo/general">general mailing list</a>.
Thanks for your contribution.
</p>
 <xsl:apply-templates select="term"/>
 </font>
</xsl:template>

<xsl:template match="term">
<p>
<font size="-1">
<b><xsl:value-of select="title"/></b>
<xsl:apply-templates select="description"/>
</font>
</p>
</xsl:template>

<xsl:template match="description">
<br />
<xsl:apply-templates/>
</xsl:template>
 
</xsl:stylesheet>  
