<?xml version="1.0"?>

<!--
 * @author Michael Wechner
 * @created 2002.4.12
 * @version 2002.4.12
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp="http://apache.org/xsp"
  xmlns:xsp-lenya="http://www.lenya.org/xsp/lenya/1.0"
>

  <xsl:template match="xsp:page">
    <xsp:page>
      <xsl:apply-templates select="@*"/>

      <xsp:structure>
        <xsp:include>org.apache.cocoon.environment.Session</xsp:include>
        <xsp:include>org.apache.cocoon.environment.Source</xsp:include>
        <xsp:include>org.apache.lenya.cms.ac.Identity</xsp:include>
        <xsp:include>org.apache.lenya.cms.publication.Publication</xsp:include>
      </xsp:structure>
      
      <xsl:apply-templates/>
    </xsp:page>
  </xsl:template>

<xsl:template match="xsp-lenya:util">
  <xsp:logic>
    Date xsp_lenya_server_time=new Date();
    <xsp:content><server_time><xsp:expr>xsp_lenya_server_time</xsp:expr></server_time></xsp:content>
    String xsp_lenya_context=request.getContextPath();
    <xsp:content><context><xsp:expr>xsp_lenya_context</xsp:expr></context></xsp:content>
    String xsp_lenya_request_uri=request.getRequestURI();
    <xsp:content><request_uri><xsp:expr>xsp_lenya_request_uri</xsp:expr></request_uri></xsp:content>

    String xsp_lenya_context_prefix = null;
    String xsp_lenya_pub_url = null;
    if(xsp_lenya_request_uri.indexOf("/authoring") >= 0) {
        xsp_lenya_context_prefix = xsp_lenya_request_uri.substring(0,xsp_lenya_request_uri.indexOf("/authoring"));
        xsp_lenya_pub_url = xsp_lenya_request_uri.substring(xsp_lenya_request_uri.indexOf("/authoring")+10);
    }
    <xsp:content><context_prefix><xsp:expr>xsp_lenya_context_prefix</xsp:expr></context_prefix></xsp:content>

    String xsp_lenya_sitemap_uri=request.getSitemapURI();
    <xsp:content><sitemap_uri><xsp:expr>xsp_lenya_sitemap_uri</xsp:expr></sitemap_uri></xsp:content>
    String xsp_lenya_prefix=xsp_lenya_request_uri.substring(0,xsp_lenya_request_uri.length()-xsp_lenya_sitemap_uri.length()-1);
    <xsp:content><prefix><xsp:expr>xsp_lenya_prefix</xsp:expr></prefix></xsp:content>

    Session xsp_lenya_session=request.getSession(false);
    Identity xsp_lenya_id=null;
    if(xsp_lenya_session != null){
      xsp_lenya_id=(Identity)xsp_lenya_session.getAttribute("org.apache.lenya.cms.ac.Identity");
      if(xsp_lenya_id != null){
        <xsp:content><current_username><xsp:expr>xsp_lenya_id.getUsername()</xsp:expr></current_username></xsp:content>
      }
      else{
        <xsp:content><no_username_yet/></xsp:content>
      }
    }
    else{
      <xsp:content><no_session_yet/></xsp:content>
    }
    String xsp_lenya_referer=request.getHeader("Referer");
    if(xsp_lenya_referer == null){
      <xsp:content><no_referer/></xsp:content>
    }    
    else {
       String xsp_lenya_status=request.getParameter("status");
       if(xsp_lenya_status == null){
          xsp_lenya_session.setAttribute("org.apache.lenya.cms.cocoon.acting.TaskAction.parent_uri",xsp_lenya_referer);
          <xsp:content><referer><xsp:expr>xsp_lenya_referer</xsp:expr></referer></xsp:content>
       }
       else{
         <xsp:content><referer><xsp:expr>xsp_lenya_session.getAttribute("org.apache.lenya.cms.cocoon.acting.TaskAction.parent_uri")</xsp:expr></referer></xsp:content>
       }
    }
    
        Publication xspUtilPublication = null;
        String xspUtilServletContextPath = null;
        String xspUtilPublicationPath = null;
        {
            Source inputSource = resolver.resolve("");
            String publicationPath = inputSource.getSystemId();
            String directories[] = publicationPath.split("/");
            String publicationId = directories[directories.length - 1];
            String path = publicationPath.substring(0, publicationPath.indexOf("/lenya/pubs/" + publicationId));
            // FIXME:
            path = path.replaceAll("file:", "");
            //path = path.replaceAll("file:/", "");
            path = path.replace('/', File.separatorChar);
            publicationPath = publicationPath.replaceAll("file:", "");
            publicationPath = publicationPath.replace('/', File.separatorChar);
            
            xspUtilServletContextPath = path;
            xspUtilPublicationPath = publicationPath;
            xspUtilPublication = new Publication(publicationId, path);
        }
    
  </xsp:logic>
</xsl:template>


<xsl:template match="xsp-lenya:publication-id">
  <xsp:expr>xspUtilPublication.getId()</xsp:expr>
</xsl:template>


<xsl:template match="xsp-lenya:servlet-context-path">
  <xsp:expr>xspUtilServletContextPath</xsp:expr>
</xsl:template>


<xsl:template match="xsp-lenya:publication">
  xspUtilPublication
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
