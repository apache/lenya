<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  <xsl:variable name="type" select="/page/type"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  
  <xsl:template match="page">
    <page:page>
      <page:title>Delete <xsl:value-of select="$type"/></page:title>
      <page:body>
      	
    <div class="lenya-box">
      <div class="lenya-box-title">Delete <xsl:value-of select="$type"/></div>
      <div class="lenya-box-body">
          <p>Really delete <xsl:value-of select="$type"/>&#160;<strong><xsl:value-of select="id"/></strong> (<xsl:value-of select="name"/>)? </p>
          <form method="GET" action="{/page/continuation}.continuation">
            <input type="submit" name="submit" value="Delete"/>
            <input type="submit" name="cancel" value="Cancel"/>
          </form>
      </div>
    </div>
    
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
  </xsl:template>
  
  
</xsl:stylesheet>
