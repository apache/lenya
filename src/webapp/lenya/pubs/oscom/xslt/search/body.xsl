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
<!--
<table bgcolor="#99ccff" width="100%">
-->
<table bgcolor="#f0f0f0" width="100%" cellpadding="4" border="1">
<tr><td>
<table>
<tr><td>
Search within: 
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
<!--
      <xsl:choose>
        <xsl:when test="@pid = ../@checked-pid">
          <input type="radio" name="publication-id"><xsl:attribute name="value"><xsl:value-of select="@pid"/></xsl:attribute><xsl:attribute name="checked"/></input><xsl:value-of select="name"/>
        </xsl:when>
        <xsl:otherwise>
          <input type="radio" name="publication-id"><xsl:attribute name="value"><xsl:value-of select="@pid"/></xsl:attribute></input><xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
-->
    </xsl:for-each>
</select>
</td></tr>
<tr><td>
    <input type="text" name="queryString" size="60">
      <xsl:attribute name="value"><xsl:value-of select="search/query-string"/></xsl:attribute>
    </input>
</td></tr>
<!--
<tr><td>
    Sort by 
    <select name="sortBy">
      <option value="score">
        <xsl:if test="search/sort-by='score'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Score
      </option>
      <option value="title">
        <xsl:if test="search/sort-by='title'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Title
      </option>
    </select>
</td></tr>
-->
<tr><td>
Limit your search to field:  
    <select name="fields">
      <option value="all">
        <xsl:if test="search/fields='all'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Contents (Title or Body)
      </option>
      <option value="title">
        <xsl:if test="search/fields='title'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        Title
      </option>
    </select>
</td></tr>
<tr><td align="right">
    <input type="submit" name="find" value="Search"/>
</td></tr>
<tr><td align="left">
    <font size="-2"><a href="http://jakarta.apache.org/lucene/" class="navigationwhite">Powered by Apache Lucene</a></font>
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
<!--
        <tr>
          <td>&#160;</td><td>Score</td><td>URL resp. File</td>
        </tr>
-->
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
<!--
  <td valign="top"><xsl:value-of select="@pos"/></td>
-->
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


<!--
        <font size="-1">Mime-Type: <xsl:apply-templates select="mime-type"/><xsl:apply-templates select="no-mime-type"/></font>
        <br />
-->
        <br />
      </td>
    </xsl:when>
    <xsl:otherwise>
      <td>Neither PATH nor URL</td>
    </xsl:otherwise>
  </xsl:choose>
</tr>
</xsl:template>

<!--
<xsl:template match="title">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="no-title">
(No Title!)
</xsl:template>

<xsl:template match="excerpt">
...&#160;<xsl:apply-templates/>&#160;...
</xsl:template>

<xsl:template match="word">
<b><xsl:value-of select="."/></b>
</xsl:template>

<xsl:template match="no-excerpt">
No excerpt available: <xsl:value-of select="file/@src"/>
</xsl:template>

<xsl:template match="mime-type">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="no-mime-type">
No mime-type!
</xsl:template>

<xsl:template match="exception">
<p>
<font color="red"><xsl:value-of select="."/></font>
</p>
</xsl:template>

<xsl:template match="pages">
<p>
Result Pages
<xsl:apply-templates select="page[@type='previous']" mode="previous"/>
<xsl:for-each select="page">
  <xsl:choose>
    <xsl:when test="@type='current'">
      <xsl:value-of select="position()"/>
    </xsl:when>
    <xsl:otherwise>
      <a href="lucene?publication-id={../../../search/publication-id}&amp;queryString={../../../search/query-string}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}"><xsl:value-of select="position()"/></a>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text> </xsl:text>
</xsl:for-each>
<xsl:apply-templates select="page[@type='next']" mode="next"/>
</p>
</xsl:template>

<xsl:template match="page" mode="next">
[<a href="lucene?publication-id={../../../search/publication-id}&amp;queryString={../../../search/query-string}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}">Next</a>&gt;&gt;]
</xsl:template>

<xsl:template match="page" mode="previous">
[&lt;&lt;<a href="lucene?publication-id={../../../search/publication-id}&amp;queryString={../../../search/query-string}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}">Previous</a>]
</xsl:template>

<xsl:template match="@*|node()">
<xsl:copy>
  <xsl:apply-templates select="@*|node()"/>
</xsl:copy>
</xsl:template>
-->

</xsl:stylesheet>
