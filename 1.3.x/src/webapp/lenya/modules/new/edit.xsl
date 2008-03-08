<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    exclude-result-prefixes="page xhtml"
    >
    
<xsl:param name="root"/>

<xsl:template match="/data">
  <html>
   <xsl:apply-templates select="newresource[1]"/>
  </html>
</xsl:template>

<xsl:template match="newresource">
<xsl:variable name="newurl"><xsl:value-of select="$root"/><xsl:value-of select="@unid"/></xsl:variable>
    <head>
            <xsl:choose>
              <xsl:when test="@status = 'SUCCESS'">
      <meta http-equiv="Refresh" content="0;URL={$newurl}"/>
      <title>New Resource Created</title>
              </xsl:when>
              <xsl:otherwise>
      <title>New Resource Failed</title>
              </xsl:otherwise>
	    </xsl:choose>

    </head>	
    <body>
      <div id="page">
            <xsl:choose>
              <xsl:when test="@status = 'SUCCESS'">
Should be redirected to <a href="{$newurl}"><xsl:value-of select="@newurl"/></a>
              </xsl:when>
              <xsl:otherwise>
Creating resource failed.
<br/><xsl:value-of select="."/>
              </xsl:otherwise>
	    </xsl:choose>
      </div>
    </body>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
</xsl:template>


</xsl:stylesheet> 
