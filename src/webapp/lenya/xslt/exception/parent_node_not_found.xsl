<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  
  <xsl:variable name="separator" select="','"/>

  
  <xsl:template match="/">
    
    <page:page>
      <page:title>Parent document not found</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">Error while publishing</div>
	  <div class="lenya-box-body">
	    <p>
	      An error occured while publishing. Most likely you are trying
	      to publish a document whose parent document hasn't been
	      published yet.</p>
	    <p>Try to publish the parent document first.</p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
