<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:variable name="separator" select="','"/>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title>Sax parsing problem</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">Cannot parse the xml</div>
	  <div class="lenya-box-body">
	    <p>
	      An error occured while parsing the xml you entered. Most
              likely you entered non-validating xml.</p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
