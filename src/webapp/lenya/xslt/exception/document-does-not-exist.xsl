<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:param name="documentid"/>
  <xsl:param name="documenturl"/>

  <xsl:template match="/">
    
    <page:page>
      <page:title>404: Page not found</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">The requested document does not exist</div>
	  <div class="lenya-box-body">
	    <p>
	      The requested document '<xsl:value-of select="$documenturl"/>' with
	      document-id '<xsl:value-of select="$documentid"/>' does not exist.
	    </p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
