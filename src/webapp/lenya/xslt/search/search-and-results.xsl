<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:template match="search-and-results">
<html>
<head>
  <title>Search</title>
</head>
<body>
<h1>Wyona CMS Search Interface -- powered by Lucene</h1>
  <form>
    <xsl:for-each select="configuration/publication">
      <xsl:choose>
        <xsl:when test="@pid = ../@checked-pid">
          <input type="radio" name="publication-id"><xsl:attribute name="value"><xsl:value-of select="@pid"/></xsl:attribute><xsl:attribute name="checked"/></input><xsl:value-of select="name"/>
        </xsl:when>
        <xsl:otherwise>
          <input type="radio" name="publication-id"><xsl:attribute name="value"><xsl:value-of select="@pid"/></xsl:attribute></input><xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <br />
    <input type="text" name="queryString" size="60">
      <xsl:attribute name="value"><xsl:value-of select="search/query-string"/></xsl:attribute>
    </input>
    <input type="submit" name="find" value="Search"/>
  </form>

  <xsl:apply-templates select="results"/>
</body>
</html>
</xsl:template>

<xsl:template match="results">
  <xsl:choose>
    <xsl:when test="hit">
      <h3>Results (Publication <xsl:value-of select="../search/publication-id"/>)</h3>
      <xsl:apply-templates select="../search/exception"/>
      <p>Total Hits: <xsl:value-of select="@total-hits"/></p>
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
      <td>URL: <a><xsl:attribute name="href"><xsl:value-of select="normalize-space(uri)"/></xsl:attribute><xsl:apply-templates select="title"/><xsl:apply-templates select="no-title"/></a></td>
    </xsl:when>
    <xsl:otherwise>
      <td>Neither PATH nor URL</td>
    </xsl:otherwise>
  </xsl:choose>
</tr>
</xsl:template>

<xsl:template match="title">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="no-title">
No Title!
</xsl:template>

<xsl:template match="exception">
<p>
<font color="red"><xsl:value-of select="."/></font>
</p>
</xsl:template>

<xsl:template match="@*|node()">
<xsl:copy>
  <xsl:apply-templates select="@*|node()"/>
</xsl:copy>
</xsl:template>

</xsl:stylesheet>
