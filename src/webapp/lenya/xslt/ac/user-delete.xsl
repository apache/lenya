<?xml version="1.0"?>

 <xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://www.lenya.org/2003/cms-page"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Delete User</page:title>
      <page:body>
	<h1>Delete User</h1>
	
	<xsl:apply-templates select="body"/>
	<xsl:apply-templates select="user"/>
	
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="user">
    <form method="post">
      <xsl:attribute name="action"></xsl:attribute>
      <p>
	Really delete user <xsl:value-of select="id"/> (<xsl:value-of select="fullName"/>)?
      </p>
      <input type="submit" value="Delete"/>
      <input type="submit" value="Cancel"/>
    </form>
  </xsl:template>
  
</xsl:stylesheet>
