<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://www.wyona.org/2002/sch">
  
  <xsl:param name="documentUri" select="''"/>
  <xsl:param name="publication-id"/>
  
  <!-- only tasks for this document type -->
<!--  
  <xsl:template match="sch:tasks">
    <xsl:copy>
      <xsl:for-each select="sch:task">
	<xsl:if test="sch:parameter[@name='task-id']/@value = $documentUri">
	  <xsl:apply-templates select="."/>
	</xsl:if>
      </xsl:for-each>
    </xsl:copy> 
  </xsl:template>
-->
  <!-- only jobs for this document -->  
  <xsl:template match="sch:publication[@name = $publication-id]/sch:jobs">
    <xsl:copy>
      <xsl:for-each select="sch:job">
	<!-- Only keep the tasks that have the same docid as the one -->  
	<!-- that was given in the url. -->
	<xsl:if test="$documentUri = '' or sch:parameter[@name='documentUri']/@value = $documentUri">
	  <xsl:apply-templates select="."/>
	</xsl:if>
      </xsl:for-each>
    </xsl:copy> 
  </xsl:template>

  <xsl:template match="sch:publication[@name != $publication-id]/sch:jobs">
  </xsl:template>
  
  <xsl:template match="* | @*">
        <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
