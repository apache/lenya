<?xml version="1.0"?>

<!--
 * @author Michael Wechner
 * @created 2002.4.12
 * @version 2002.4.12
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp="http://apache.org/xsp"
  xmlns:xsp-wyona="http://www.wyona.org/xsp/wyona/1.0"
>

  <xsl:template match="xsp:page">
    <xsp:page>
      <xsl:apply-templates select="@*"/>

      <xsp:structure>
        <xsp:include>org.lenya.cms.ac.Identity</xsp:include>
        <xsp:include>org.apache.cocoon.environment.Session</xsp:include>
      </xsp:structure>

      <xsl:apply-templates/>
    </xsp:page>
  </xsl:template>

<xsl:template match="xsp-wyona:util">
  <xsp:logic>
    Date xsp_wyona_server_time=new Date();
    String xsp_wyona_context=request.getContextPath();
    <xsp:content><context><xsp:expr>xsp_wyona_context</xsp:expr></context></xsp:content>
    String xsp_wyona_request_uri=request.getRequestURI();
    <xsp:content><request_uri><xsp:expr>xsp_wyona_request_uri</xsp:expr></request_uri></xsp:content>
    String xsp_wyona_context_prefix=xsp_wyona_request_uri.substring(0,xsp_wyona_request_uri.indexOf("/authoring"));
    String xsp_wyona_sitemap_uri=request.getSitemapURI();
    <xsp:content><sitemap_uri><xsp:expr>xsp_wyona_sitemap_uri</xsp:expr></sitemap_uri></xsp:content>

    //String xsp_wyona_prefix="oscom";
    //String xsp_wyona_prefix="nwt";
    //String xsp_wyona_context_prefix=xsp_wyona_context+"/"+xsp_wyona_prefix;

    Session xsp_wyona_session=request.getSession(false);
    Identity xsp_wyona_id=null;
    if(xsp_wyona_session != null){
      xsp_wyona_id=(Identity)xsp_wyona_session.getAttribute("org.lenya.cms.ac.Identity");
      if(xsp_wyona_id != null){
        <xsp:content><current_username><xsp:expr>xsp_wyona_id.getUsername()</xsp:expr></current_username></xsp:content>
        }
      else{
        <xsp:content><no_username_yet/></xsp:content>
        }
      }
    else{
      <xsp:content><no_session_yet/></xsp:content>
      }
  </xsp:logic>

  <server_time><xsp:expr>xsp_wyona_server_time</xsp:expr></server_time>
  <!--<xsp:content><prefix><xsp:expr>xsp_wyona_prefix</xsp:expr></prefix></xsp:content>-->
  <xsp:content><context_prefix><xsp:expr>xsp_wyona_context_prefix</xsp:expr></context_prefix></xsp:content>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
 <xsl:copy>
  <xsl:apply-templates select="@*|node()"/>
 </xsl:copy>
</xsl:template>

<!-- Standard Templates -->
<!--
  <xsl:template name="get-nested-content">
    <xsl:param name="content"/>
    <xsl:choose>
      <xsl:when test="$content/xsp:text">"<xsl:value-of select="$content"/>"</xsl:when>
      <xsl:when test="$content/*">
        <xsl:apply-templates select="$content/*"/>
      </xsl:when>
      <xsl:otherwise>"<xsl:value-of select="$content"/>"</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
-->

  <xsl:template match="@*|*|text()|processing-instruction()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
