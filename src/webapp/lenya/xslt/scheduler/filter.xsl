<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://www.wyona.org/2002/sch">
  
<xsl:variable name="params" select="/sch:scheduler/sch:parameters"/>

<xsl:variable name="documentUri" select="$params/sch:parameter[@name='documentUri']/@value"/>
<xsl:variable name="publication-id" select="$params/sch:parameter[@name='publication-id']/@value"/>
    
<!-- only jobs for this document -->  
<xsl:template match="sch:jobs/sch:job">
  <xsl:if test="not($documentUri) or sch:parameter[@name='documentUri']/@value = $documentUri">
    <xsl:copy>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:if>
</xsl:template>
  
  
<!-- remove other publications -->
<xsl:template match="sch:publication[@name != $publication-id]">
</xsl:template>

    
<!-- Identity transformation -->
<xsl:template match="@*|*">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>  


</xsl:stylesheet>
