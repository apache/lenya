<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0">
  
<xsl:param name="document-url"/>
    
<!-- remove other jobs -->
<xsl:template match="sch:job[@url != $document-url]"/>

    
<!-- only jobs for this document -->  
<!--
<xsl:template match="sch:job[($document-url != '') and (@url != $document-url)]"/>
-->
  
<!-- Identity transformation -->
<xsl:template match="@*|*">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>  


</xsl:stylesheet>
