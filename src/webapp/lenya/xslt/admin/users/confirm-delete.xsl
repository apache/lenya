<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  
  <xsl:template match="page">
    <page:page>
      <page:title>Delete User</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    <div class="lenya-box">
      <div class="lenya-box-title">Delete User</div>
      <div class="lenya-box-body">
          <p> Really delete user <strong><xsl:value-of select="id"/></strong> (<xsl:value-of select="fullname"/>)? </p>
          <form method="GET" action="{/page/continuation}.continuation">
            <input type="submit" name="submit" value="Delete"/>
            <input type="submit" name="cancel" value="Cancel"/>
          </form>
      </div>
    </div>
  </xsl:template>
  
  
</xsl:stylesheet>
