<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : select-dossier.xsl
    Created on : November 13, 2002, 7:49 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:param name="article-file"/>
    
<!-- template rule matching source root element -->
<xsl:template match="/">
  <html>
    <head>
      <title>Assign Article to Dossier</title>
<link rel="stylesheet" type="text/css" href="/wyona-cms/wyona/default.css" />
    </head>
    <body>
      <h1>Assign Article to Dossier</h1>
      <form action="selectDossier" method="POST">
        <input type="hidden" name="article-file" value="{$article-file}"/>
        <table cellpadding="5" cellspacing="0" border="0">
        <xsl:call-template name="dossier-entry">
          <xsl:with-param name="id" select="'none'"/>
          <xsl:with-param name="title" select="'No Dossier'"/>
        </xsl:call-template>
        <xsl:apply-templates select="dossiers/dossier-list/dossier"/>
        <tr>
          <td>
            <input type="submit" value="OK"/>
            &#160;
            <a href="{dossiers/dossier-list/referer}">Cancel</a>
          </td>
        </tr>
        </table>
      </form>
    </body>
  </html>
</xsl:template>

<xsl:template match="dossier[@path != '']">
  <xsl:call-template name="dossier-entry">
    <xsl:with-param name="id" select="@id"/>
    <xsl:with-param name="title" select="document(concat('file://', @path))/dossier/head/title"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="dossier-entry">
  <xsl:param name="id"/>
  <xsl:param name="title"/>
  <tr>
    <td>
      <input type="radio" name="dossier-id" value="{$id}">
        <xsl:choose>
          <xsl:when test="//body.head/dossier[@id = $id]">
            <xsl:attribute name="checked"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="$id = 'none'">
              <xsl:attribute name="checked"/>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </input>
      &#160;<strong><xsl:value-of select="$title"/></strong>
      <xsl:if test="$id != 'none'">
        (<xsl:value-of select="$id"/>)
      </xsl:if>
    </td>
  </tr>
</xsl:template>

<xsl:template match="*"/>

</xsl:stylesheet> 
