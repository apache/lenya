<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:import href="../../../../xslt/search/search-and-results.xsl"/>

<xsl:template name="html-title">
Search
</xsl:template>

<xsl:template name="admin-url">
<xsl:param name="prefix"/>
<span class="breadcrumb">Apache Lenya</span>
</xsl:template>

<xsl:template name="body">
<xsl:apply-templates select="/oscom/search-and-results"/>
</xsl:template>

<xsl:template match="search-and-results">
  <form>
<table bgcolor="#f0f0f0" width="100%" cellpadding="4" border="1">
<tr><td>


<table>
<tr><td>
Search within: 
<xsl:choose>
<xsl:when test="configuration/@checked-pid = 'matrix'">
the Matrix or within <a href="lucene">General</a>
<input type="hidden" name="publication-id" value="matrix"/>
</xsl:when>
<xsl:otherwise>
<select name="publication-id">
    <xsl:for-each select="configuration/publication">
      <xsl:choose>
        <xsl:when test="@pid = ../@checked-pid">
          <option><xsl:attribute name="value"><xsl:value-of select="@pid"/></xsl:attribute><xsl:attribute name="selected"/><xsl:value-of select="name"/></option>
        </xsl:when>
        <xsl:otherwise>
          <option><xsl:attribute name="value"><xsl:value-of select="@pid"/></xsl:attribute><xsl:value-of select="name"/></option>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
</select>
<a href="?publication-id=matrix">Matrix Advanced</a>
</xsl:otherwise>
</xsl:choose>
</td></tr>


<tr><td>
    <input type="text" name="queryString" size="60">
      <xsl:attribute name="value"><xsl:value-of select="search/query-string"/></xsl:attribute>
    </input>
</td></tr>


<tr><td>
Limit your search to field:
<xsl:choose>
<xsl:when test="configuration/@checked-pid = 'matrix'">
  <input type="checkbox" name="matrix.fields" value="contents">
    <xsl:if test="search/fields/field[1]='contents'">
      <xsl:attribute name="checked">checked</xsl:attribute>
    </xsl:if>
    Contents
  </input>
  <input type="checkbox" name="matrix.fields" value="title">
    <xsl:if test="search/fields/field[1]='title'">
      <xsl:attribute name="checked">checked</xsl:attribute>
    </xsl:if>
    Title
  </input>
  <input type="checkbox" name="matrix.fields" value="license">
    <xsl:if test="search/fields/field[1]='license'">
      <xsl:attribute name="checked">checked</xsl:attribute>
    </xsl:if>
    License
  </input>

<!--
    <select name="matrix.fields">
      <option value="contents">
        <xsl:if test="search/fields/field[1]='contents'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Contents (Title and Body)
      </option>
      <option value="title">
        <xsl:if test="search/fields/field[1]='title'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Title
      </option>
      <option value="license">
        <xsl:if test="search/fields/field[1]='license'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        License
      </option>
    </select>
    -->
</xsl:when>
<xsl:otherwise>
    <select name="dummy-index-id.fields">
      <option value="contents">
        <xsl:if test="search/fields/field[1]='contents'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Contents (Title and Body)
      </option>
      <option value="title">
        <xsl:if test="search/fields/field[1]='title'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Title
      </option>
    </select>
</xsl:otherwise>
</xsl:choose>
</td></tr>


<tr><td align="right">
    <input type="submit" name="find" value="Search"/>
</td></tr>
<tr><td align="left">
    <font size="-2"><a href="http://jakarta.apache.org/lucene/">Powered by Apache Lucene</a></font>
</td></tr>
</table>
</td></tr>
</table>
  </form>



  <xsl:apply-templates select="search/exception"/>
  <xsl:apply-templates select="results"/>
</xsl:template>

<xsl:template match="results">
  <h3>Search Results (within <xsl:value-of select="../search/publication-name"/>)</h3>
  <xsl:choose>
    <xsl:when test="hits">
<p>
      <xsl:value-of select="pages/page[@type='current']/@start"/> - <xsl:value-of select="pages/page[@type='current']/@end"/> of <xsl:value-of select="@total-hits"/> results
</p>
      <table width="100%" border="0">
      <xsl:apply-templates select="hits/hit"/>
      </table>
    </xsl:when>
    <xsl:otherwise>
      <p>Sorry, <b>nothing</b> found!</p>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:apply-templates select="pages"/>
</xsl:template>

<xsl:template match="hit">
<tr>
  <td valign="top"><b><xsl:value-of select="score/@percent"/>%</b></td>
  <td valign="top">&#160;&#160;&#160;</td>
  <xsl:choose>
    <xsl:when test="path">
      <td>File: <xsl:value-of select="path"/></td>
    </xsl:when>
    <xsl:when test="uri">
      <td>
        <xsl:variable name="url"><xsl:value-of select="/oscom/search-and-results/search/publication-prefix"/><xsl:choose><xsl:when test="uri/@filename = 'index.html'"><xsl:value-of select="normalize-space(uri/@parent)"/>/</xsl:when><xsl:otherwise><xsl:value-of select="normalize-space(uri)"/></xsl:otherwise></xsl:choose></xsl:variable>

        <a href="{$url}"><xsl:apply-templates select="title"/></a><xsl:apply-templates select="no-title"/>
        <br />
        <font size="-1"><xsl:apply-templates select="excerpt"/><xsl:apply-templates select="no-excerpt"/></font>
        <br />
        <font size="-1">URL: <a href="{$url}"><xsl:value-of select="$url"/></a></font>
        <br />
        <br />
      </td>
    </xsl:when>
    <xsl:otherwise>
      <td>Neither PATH nor URL</td>
    </xsl:otherwise>
  </xsl:choose>
</tr>
</xsl:template>

</xsl:stylesheet>
