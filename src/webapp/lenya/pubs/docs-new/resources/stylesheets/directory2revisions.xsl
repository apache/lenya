<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:dir="http://apache.org/cocoon/directory/2.0">
 
 
 <xsl:param name="page" />
 <xsl:variable name="revisionPage"><xsl:value-of select="concat('revision-',$page)" /></xsl:variable>

  <xsl:template match="dir:directory">
  <revisions>
      <xsl:apply-templates select="dir:file" />
  </revisions>
  </xsl:template>

  <xsl:template match="dir:file">
  
 <xsl:if test="starts-with(@name,$revisionPage)" >
  	<revision>
  	  <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
  	  <xsl:attribute name="date"><xsl:value-of select="@date"/></xsl:attribute>
	</revision>
 </xsl:if>
  </xsl:template>

</xsl:stylesheet>
