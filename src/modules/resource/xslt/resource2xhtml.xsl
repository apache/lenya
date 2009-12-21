<?xml version="1.0" encoding="UTF-8" ?>
<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

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
  <xsl:param name="path"/>
  <xsl:param name="pubid"/>
  <xsl:param name="language"/>
  <xsl:param name="document-type"/>
  <xsl:param name="contentLength"/>
  <xsl:param name="mimeType"/>
  <xsl:param name="documentUrl"/>
  <xsl:param name="sourceExtension"/>
  <xsl:param name="imageprefix"/>
  <xsl:param name="revision"/>
  
  <xsl:variable name="revisionSuffix">
    <xsl:if test="$revision != ''">
      <xsl:text>?lenya.revision=</xsl:text><xsl:value-of select="$revision"/>
    </xsl:if>
  </xsl:variable>
  
  <xsl:variable name="mediaUrl" select="concat(substring-before($documentUrl, '.html'), '.', $sourceExtension, $revisionSuffix)"/>
  
  <xsl:template match="/">
    <html>
      <body>
        <xsl:apply-templates select="//meta:metadata" mode="media"/>
      </body>
    </html>
  </xsl:template>
    
  <xsl:template match="meta:metadata" mode="media">
    
    <xsl:variable name="mediaURI">
      <xsl:value-of select="$root"/>
      <xsl:value-of select="$mediaUrl"/>
    </xsl:variable>
    
    <xsl:variable name="size">
      <xsl:value-of select="format-number($contentLength div 1024, '#,###.##')"/>
    </xsl:variable>
    
    <table cellpadding="3" cellspacing="0" border="0" style="padding: 20px;">
      <tr>
        <th class="vertical"><a href="{$mediaURI}" target="_new">
          <xsl:call-template name="icon">
            <xsl:with-param name="mimetype" select="$mimeType"/>
            <xsl:with-param name="imageprefix" select="$imageprefix"/>
          </xsl:call-template> </a>
        </th>
        <td><h1><i18n:text>Media Document</i18n:text></h1></td>
      </tr>
      <tr>
        <th class="vertical"><i18n:text>Title</i18n:text>:</th>
        <td><strong><xsl:value-of select="dc:elements/dc:title"/></strong></td>
      </tr>
      <tr>
        <th class="vertical"><i18n:text>Description</i18n:text>:</th>
        <td><xsl:value-of select="dc:elements/dc:description"/></td>
      </tr>
      <tr>
        <th class="vertical"><i18n:text>Content</i18n:text>:</th>
        <td><a href="{$mediaURI}" target="_new"><xsl:value-of select="$mediaUrl"/>
          </a></td>
      </tr>
      <tr>
        <th class="vertical"><i18n:text>Size</i18n:text>:</th>
        <td><xsl:value-of select="$size"/> KB</td>
      </tr>
      <tr>
        <th class="vertical"><i18n:text>MimeType</i18n:text>:</th>
        <td><xsl:value-of select="$mimeType"/></td>
      </tr>
      <xsl:if test="starts-with(docmeta:elements/docmeta:mimeType, 'image/')">
        <tr>
          <th class="vertical"><i18n:text>Width</i18n:text>:</th>
          <td><xsl:value-of select="mediameta:elements/mediameta:width"/></td>
        </tr>
        <tr>
          <th class="vertical"><i18n:text>Height</i18n:text>:</th>
          <td><xsl:value-of select="mediameta:elements/mediameta:height"/></td>
        </tr>
      </xsl:if>
      <tr>
        <th class="vertical" style="padding-top: 1em; vertical-align: top;"><i18n:text>Preview</i18n:text>:</th>
        <td style="padding-top: 1em;">
          <xsl:call-template name="preview">
            <xsl:with-param name="mimetype" select="$mimeType"/>
            <xsl:with-param name="mediaURI" select="$mediaURI"/>
            <xsl:with-param name="width" select="mediameta:elements/mediameta:width"/>
            <xsl:with-param name="height" select="mediameta:elements/mediameta:height"/>
          </xsl:call-template><br/><br/>
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
