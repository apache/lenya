<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:variable name="language"><xsl:value-of select="/missing-language/current-language"/></xsl:variable>

  <xsl:template match="/">
    
    <page:page>
      <page:title>Document not available for this language</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">The requested document is not available for language "<xsl:value-of select="$language"/>"</div>
	  <div class="lenya-box-body">
	    <p>
	      The requested document is not available for language "<xsl:value-of select="$language"/>". The following languages are available:
	    </p>
	    <ul>
	      <xsl:apply-templates select="missing-language/available-languages/available-language"/>
	    </ul>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="available-languages/available-language">
    <li>
      <a><xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute><xsl:value-of select="language"/></a>
    </li>
  </xsl:template>

</xsl:stylesheet>
