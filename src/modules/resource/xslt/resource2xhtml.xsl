<?xml version="1.0" encoding="UTF-8" ?>

<!-- $Id: xhtml-standard.xsl,v 1.44 2004/12/14 11:00:41 josias Exp $ -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:meta="http://apache.org/cocoon/lenya/metadata/1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:media="http://apache.org/lenya/pubs/default/media/1.0"
  xmlns:mediameta="http://apache.org/lenya/metadata/media/1.0"
  xmlns:docmeta="http://apache.org/lenya/metadata/document/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:dcterms="http://purl.org/dc/terms/"
  exclude-result-prefixes="xhtml meta dc">
  
  <xsl:import href="fallback://lenya/modules/resource/xslt/common/mimetype.xsl"/>
  <xsl:include href="fallback://lenya/modules/xhtml/xslt/helper-object.xsl"/>
  <xsl:param name="root"/> <!-- the URL up to (including) the area -->
  <xsl:param name="documentId"/>
  <xsl:param name="context-prefix" select="''"/>
  <xsl:param name="language"/>
  <xsl:param name="document-type"/>
  <xsl:param name="documentParent"/>
  <xsl:param name="title"/>
  <xsl:param name="contentLength"/>
  <xsl:param name="mimeType"/>
  
  <xsl:variable name="imageprefix"
    select="concat($context-prefix,'/modules/resource')"/>
  <xsl:variable name="nodeid"
    select="concat($context-prefix,$root,$documentParent)"/>
  
  <xsl:template match="/">
    <xhtml:div id="body">
      <xsl:apply-templates select="//meta:metadata" mode="media"/>
    </xhtml:div>
  </xsl:template>
    
  <xsl:template match="meta:metadata" mode="media">
    
    <xsl:variable name="mediaName" select="concat($documentId, '.', mediameta:elements/mediameta:filename)"/>
    
    <xsl:variable name="mediaURI">
      <xsl:value-of select="$root"/>
      <xsl:value-of select="$mediaName"/>
    </xsl:variable>
    
    <xsl:variable name="size">
      <xsl:value-of select="format-number($contentLength div 1024, '#,###.##')"/>
    </xsl:variable>
    
    <table width="500" cellpadding="2" cellspacing="0" border="0" bgcolor="#FFEEEE"
      style="padding: 20px; border: solid 1px #BB9999;">
      <tr>
        <td> <a href="{$mediaURI}" target="_new">
          <xsl:call-template name="icon">
            <xsl:with-param name="mimetype" select="$mimeType"/>
            <xsl:with-param name="imageprefix" select="$imageprefix"/>
          </xsl:call-template> </a>
        </td>
        <td><h1><i18n:text>Resource Document</i18n:text></h1></td>
      </tr>
      <tr>
        <td><i><i18n:text>Title</i18n:text>:</i></td>
        <td><strong><xsl:value-of select="dc:elements/dc:title"/></strong></td>
      </tr>
      <tr>
        <td><i><i18n:text>Description</i18n:text>:</i></td>
        <td><xsl:value-of select="dc:elements/dc:description"/></td>
      </tr>
      <tr>
        <td><i><i18n:text>Content</i18n:text>:</i></td>
        <td><a href="{$mediaURI}" target="_new"><xsl:value-of select="$mediaName"/>
          </a></td>
      </tr>
      <tr>
        <td><i><i18n:text>Size</i18n:text>:</i></td>
        <td><xsl:value-of select="$size"/> KB</td>
      </tr>
      <tr>
        <td><i><i18n:text>MimeType</i18n:text>:</i></td>
        <td><xsl:value-of select="$mimeType"/></td>
      </tr>
      <xsl:if test="mediameta:elements/mediameta:width != ''">
        <tr>
          <td><i><i18n:text>Dimension</i18n:text>:</i></td>
          <td><xsl:value-of select="mediameta:elements/mediameta:width"/>x
            <xsl:value-of select="mediameta:elements/mediameta:height"/></td>
        </tr>
      </xsl:if>
      <tr>
        <td colspan="2"><i><i18n:text>Preview</i18n:text>:</i><br/><br/>
        <div style="text-align: center">
          <xsl:call-template name="preview">
            <xsl:with-param name="mimetype" select="$mimeType"/>
            <xsl:with-param name="mediaURI" select="$mediaURI"/>
          </xsl:call-template><br/><br/>
        </div>
        </td>
      </tr>
    </table>

  </xsl:template>
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
