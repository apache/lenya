<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="results">
  <xsl:choose>
    <xsl:when test="hit">
      <h3>Wyona Results View</h3>
      <table width="90%" cellpadding="4" border="1">
        <tr>
          <td>&#160;</td><td>Score</td><td>URL resp. File</td>
        </tr>
      <xsl:apply-templates select="hit"/>
      </table>
    </xsl:when>
    <xsl:otherwise>
      <p>Sorry, nothing found!</p>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="hit">
<tr>
  <td><xsl:value-of select="position()"/></td>
  <td><xsl:value-of select="score"/></td>
  <xsl:choose>
    <xsl:when test="path">
      <td>File: <xsl:value-of select="path"/></td>
    </xsl:when>
    <xsl:when test="uri">
<!--
      <td>URL: <xsl:value-of select="uri"/></td>
-->
      <td>URL: <a><xsl:attribute name="href"><xsl:value-of select="normalize-space(uri)"/></xsl:attribute><xsl:value-of select="uri"/></a></td>
    </xsl:when>
    <xsl:otherwise>
      <td>Neither PATH nor URL</td>
    </xsl:otherwise>
  </xsl:choose>
</tr>
</xsl:template>

<xsl:template match="@*|node()">
<xsl:copy>
  <xsl:apply-templates select="@*|node()"/>
</xsl:copy>
</xsl:template>

</xsl:stylesheet>
