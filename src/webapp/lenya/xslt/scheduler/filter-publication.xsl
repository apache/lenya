<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0">
  
<xsl:param name="publication-id"/>

<!-- insert requested job group element if it does not exist -->
<xsl:template match="sch:scheduler">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		<xsl:if test="not(sch:job-group[@name = $publication-id])">
			<sch:job-group name="{$publication-id}"/>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>


<!-- remove other publications -->
<xsl:template match="sch:job-group[@name != $publication-id]"/>

    
<!-- Identity transformation -->
<xsl:template match="@*|*">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>  


</xsl:stylesheet>
