<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="publish-all">
<publish>
  <referer>/wyona-cms/oscom/authoring/matrix/index.html</referer>
  <publication-id>oscom</publication-id>
  <current_username/>
  <context>/wyona-cms</context>
  <prefix>/wyona-cms/oscom</prefix>

<uris>
/wyona-cms/oscom/matrix/index.html<xsl:for-each select="oscom/system">,/wyona-cms/oscom/matrix/<xsl:value-of select="id"/>.html</xsl:for-each>
</uris>

<sources>
<xsl:for-each select="oscom/system">/matrix/<xsl:value-of select="id"/>.xml<xsl:if test="position() != last()">,</xsl:if></xsl:for-each>
</sources>
</publish>
</xsl:template>
 
</xsl:stylesheet>  
