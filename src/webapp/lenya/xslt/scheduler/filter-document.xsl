<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0">
  
<xsl:param name="documenturl"/>
    
<!-- remove other jobs -->
<xsl:template match="sch:job[@url != $documenturl]"/>
    
<!-- Identity transformation -->
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>  


</xsl:stylesheet>
