<?xml version="1.0" encoding="UTF-8" ?>

<!-- $Id: mimetype.xsl 9187 2005-12-22 15:53:21Z josias $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:media="http://apache.org/lenya/pubs/default/media/1.0"    
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:dcterms="http://purl.org/dc/terms/"
    exclude-result-prefixes="xhtml lenya dc">
    
  <xsl:template name="icon">
    <xsl:param name="mimetype"/>
    <xsl:param name="imageprefix"/>
    
    <xsl:choose>
      <!-- Audio file types -->
      <xsl:when test="$mimetype = 'audio/x-aiff'">
        <img src="{$imageprefix}/icons/aif_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'audio/mpeg'">
        <img src="{$imageprefix}/icons/mp3_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'audio/x-pn-realaudio'">
        <img src="{$imageprefix}/icons/rm_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'audio/x-wav'">
        <img src="{$imageprefix}/icons/wav_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Video file types -->
      <xsl:when test="$mimetype = 'video/x-msvideo'">
        <img src="{$imageprefix}/icons/avi_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'video/quicktime'">
        <img src="{$imageprefix}/icons/mov_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'video/mpeg'">
        <img src="{$imageprefix}/icons/mpg_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'video/x-ms-wmv'">
        <img src="{$imageprefix}/icons/wmv_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Image file types -->
      <xsl:when test="$mimetype = 'image/gif'">
        <img src="{$imageprefix}/icons/gif_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/png'">
        <img src="{$imageprefix}/icons/png_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/x-png'">
        <img src="{$imageprefix}/icons/png_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/jpeg'">
        <img src="{$imageprefix}/icons/jpg_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/pjpeg'">
        <img src="{$imageprefix}/icons/jpg_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/tiff'">
        <img src="{$imageprefix}/icons/tif_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/bmp'">
        <img src="{$imageprefix}/icons/bmp_med.gif" border="0" alt=""/>
      </xsl:when>
 
      <!-- Compressed file types -->
      <xsl:when test="$mimetype = 'application/x-bzip2'">
        <img src="{$imageprefix}/icons/bz2_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/x-gzip'">
        <img src="{$imageprefix}/icons/gz_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/x-tar'">
        <img src="{$imageprefix}/icons/tgz_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/zip'">
        <img src="{$imageprefix}/icons/zip_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/x-zip-compressed'">
        <img src="{$imageprefix}/icons/zip_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Text file types -->
      <xsl:when test="$mimetype = 'application/rtf'">
        <img src="{$imageprefix}/icons/rtf_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/pdf'">
        <img src="{$imageprefix}/icons/pdf_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/x-pdf'">
        <img src="{$imageprefix}/icons/pdf_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'text/plain'">
        <img src="{$imageprefix}/icons/txt_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'text/html'">
        <img src="{$imageprefix}/icons/html_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'text/xml'">
        <img src="{$imageprefix}/icons/xml_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Application file types -->
      <xsl:when test="$mimetype = 'application/octet-stream'">
        <img src="{$imageprefix}/icons/exe_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Multimedia file types -->
      <xsl:when test="$mimetype = 'application/x-shockwave-flash'">
        <img src="{$imageprefix}/icons/swf_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Microsft Office file types -->
      <xsl:when test="$mimetype = 'application/msword'">
        <img src="{$imageprefix}/icons/doc_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/vnd.ms-excel'">
        <img src="{$imageprefix}/icons/xls_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/x-excel'">
        <img src="{$imageprefix}/icons/xls_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/vnd.ms-powerpoint'">
        <img src="{$imageprefix}/icons/ppt_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/mspowerpoint'">
        <img src="{$imageprefix}/icons/ppt_med.gif" border="0" alt=""/>
      </xsl:when>
      <xsl:when test="$mimetype = 'application/powerpoint'">
        <img src="{$imageprefix}/icons/ppt_med.gif" border="0" alt=""/>
      </xsl:when>

      <!-- Unknown file types -->
      <xsl:otherwise>
        <img src="{$imageprefix}/icons/default_med.gif" border="0" alt=""/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="preview">
    <xsl:param name="mimetype"/>
    <xsl:param name="mediaURI"/>
    
    <xsl:choose>
      <xsl:when test="$mimetype = 'image/gif'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/png'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/x-png'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/jpeg'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/pjpeg'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/tiff'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:when test="$mimetype = 'image/bmp'">
        <img src="{$mediaURI}" border="1" alt="preview"/>
      </xsl:when>
      <xsl:otherwise>
        <i><i18n:text>No preview avalable for this resource</i18n:text></i>
      </xsl:otherwise>
      
    </xsl:choose> 
  </xsl:template>
</xsl:stylesheet>
