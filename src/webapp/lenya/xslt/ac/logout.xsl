<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    >

<xsl:output version="1.0" indent="yes"/>

<xsl:param name="publication_name"/>
<xsl:variable name="copyright">copyright &#169; 2003-2004 Apache Software Foundation</xsl:variable>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="page">
  <page:page>
    <page:title><xsl:call-template name="html-title"/></page:title>
    <page:body>
     <xsl:apply-templates select="body"/>
     <p style="font-size: small">
       <xsl:value-of select="$copyright"/>
     </p>
    </page:body>
  </page:page>
</xsl:template>

<xsl:template match="body">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template name="html-title">
LOGOUT from the <xsl:call-template name="pubname" /> Publication
</xsl:template>

<xsl:template name="pubname">
   <xsl:value-of select="translate($publication_name, 'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
 </xsl:template>

<xsl:template match="logout">
  
<xsl:apply-templates select="referer"/>
<xsl:apply-templates select="no_referer"/>

  <div class="lenya-box">
    <div class="lenya-box-title">Your history</div>
    <div class="lenya-box-body">
      <ul>
        <xsl:apply-templates select="uri"/>
      </ul>
    </div>
  </div>

</xsl:template>

<xsl:template match="uri">
<li><a href="{.}"><xsl:value-of select="."/></a></li>
</xsl:template>

<xsl:template match="referer">
<p>Referer: <a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></p>
</xsl:template>

<xsl:template match="no_referer">
<p>
<font color="red">EXCEPTION:</font> No referer
</p>
</xsl:template>

</xsl:stylesheet>
