<?xml version="1.0"?>

<!--
 $Id: add-child.xsl,v 1.4 2003/06/14 18:53:37 gregor Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Add Child</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Add Child</div>
      <div class="lenya-box-body">
    <form method="post">
      <xsl:attribute name="action"></xsl:attribute>
      <p>
	Do you really want to add a child element to <xsl:value-of select="document-id"/>?
      </p>
      <input type="submit" class="lenya-form-element" value="Add"/>
      <input type="submit" class="lenya-form-element" value="Cancel"/>
    </form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
