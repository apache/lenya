<?xml version="1.0"?>

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp="http://apache.org/xsp"
  xmlns:xsp-wyona="http://www.wyona.org/xsp/wyona/1.0"
  version="1.0"
>

<xsl:template match="xsp-wyona:util">
  <xsp:logic>
    Date xsp_wyona_server_time=new Date();
    String xsp_wyona_context=request.getContextPath();
    <xsp:content><context><xsp:expr>xsp_wyona_context</xsp:expr></context></xsp:content>
    String xsp_wyona_request_uri=request.getRequestURI();
    <xsp:content><request_uri><xsp:expr>xsp_wyona_request_uri</xsp:expr></request_uri></xsp:content>
    String xsp_wyona_sitemap_uri=request.getSitemapURI();
    <xsp:content><sitemap_uri><xsp:expr>xsp_wyona_sitemap_uri</xsp:expr></sitemap_uri></xsp:content>
    String xsp_wyona_prefix="nwt";
    String xsp_wyona_context_prefix=xsp_wyona_context+"/"+xsp_wyona_prefix;
  </xsp:logic>

  <server_time><xsp:expr>xsp_wyona_server_time</xsp:expr></server_time>
  <xsp:content><prefix><xsp:expr>xsp_wyona_prefix</xsp:expr></prefix></xsp:content>
  <xsp:content><context_prefix><xsp:expr>xsp_wyona_context_prefix</xsp:expr></context_prefix></xsp:content>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
 <xsl:copy>
  <xsl:apply-templates select="@*|node()"/>
 </xsl:copy>
</xsl:template>

</xsl:stylesheet>
