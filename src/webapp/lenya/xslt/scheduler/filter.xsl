<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://www.wyona.org/2002/sch">
  
  <xsl:param name="documentID"/>

  <xsl:template match="sch:tasks">
    <xsl:copy>
      <xsl:for-each select="sch:task">
	<!-- Only keep the tasks that have the same docid as the one -->  
	<!-- that was given in the url. -->
	<xsl:if test="sch:parameter[@name='docid']/@value = $documentID">
	  <xsl:apply-templates select="."/>
	</xsl:if>
      </xsl:for-each>
    </xsl:copy> 
  </xsl:template>

  <xsl:template match="* | @*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
