<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/page-util.xsl"/>
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    
    <page:page>
      <page:title>Page not published yet</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">Page not published yet</div>
	  <div class="lenya-box-body">
	    <p>
	      An error occured. Most likely you are trying access a
	      page which has not been published yet.</p>
	      
	    <p>To publish this page click on the <b>File</b> menu
	      within the Authoring area and then click on the
	      <b>Publish</b> menu item.</p>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>
