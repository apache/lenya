<?xml version="1.0"?>

<xsl:stylesheet version="1.0" 
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0" 
    xmlns:session="http://www.apache.org/xsp/session/2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    
    <xsl:variable name="uriName">lucene</xsl:variable>
    <xsl:param name="area" select="'live'"/>
    <xsl:param name="contextprefix"/>
    
    <xsl:template match="search-and-results">
        <page:page>
            <page:title>Search <xsl:value-of 
                select="search/publication-name"/></page:title>
            <page:body>
                <div class="lenya-box">
                    <div class="lenya-box-title">Search <xsl:value-of select="search/publication-name"/></div>
                    <div class="lenya-box-body">
                        <form>
                            <table class="lenya-table-noborder">
                                <tr>
                                    <td>Search</td>
                                    <td>
                                        <input type="text" name="queryString" size="60" class="lenya-form-element">
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="search/query-string"/>
                                            </xsl:attribute>
                                        </input>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="configuration"/>
                                <tr>
                                    <td>Sort by</td>
                                    <td>
                                        <select name="sortBy" class="lenya-form-element">
                                            <option value="score"> <xsl:if test="search/sort-by='score'"> 
                                                <xsl:attribute name="selected">selected</xsl:attribute> 
                                                </xsl:if> Score </option>
                                            <option value="title"> <xsl:if 
                                                test="search/sort-by='title'"> 
                                                <xsl:attribute 
                                                name="selected">selected</xsl:attribute> 
                                                </xsl:if> Title </option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="right">
                                        <input type="submit" name="find" 
                                            value="Search"/>
                                    </td>
                                </tr>
                            </table>
                        </form>
                        <xsl:apply-templates select="search/exception"/>
                        <xsl:apply-templates select="results"/>
                    </div>
                </div>
            </page:body>
        </page:page>
    </xsl:template>
    
    <xsl:template match="configuration">
        <xsl:for-each select="publication">
            <tr>
                <td>Publications</td>
                <td>
                    <xsl:choose>
                        <xsl:when test="@pid = ../@checked-pid">
                            <input type="radio" name="publication-id">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="@pid"/>
                                </xsl:attribute>
                                <xsl:attribute name="checked"/>
                            </input>
                            <xsl:value-of select="name"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <input type="radio" name="publication-id">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="@pid"/>
                                </xsl:attribute>
                            </input>
                            <xsl:value-of select="name"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td>Fields</td>
                <td>
                    <select name="{@pid}.fields" class="lenya-form-element">
                        <xsl:for-each select="search-fields/field">
                            <option value="{.}">
                                <xsl:if 
                                    test="/search-and-results/search/fields/field = .">
                                    <xsl:attribute name="selected">
                                        selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="."/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
            </tr>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="results">
        <h3>Results for <xsl:value-of select="../search/publication-name"/></h3>
        <xsl:choose>
            <xsl:when test="hits">
                <p> Documents <xsl:value-of 
                    select="pages/page[@type='current']/@start"/> - 
                    <xsl:value-of select="pages/page[@type='current']/@end"/> 
                    of <xsl:value-of select="@total-hits"/> matches </p>
                <!--
      <p>Total Hits: <xsl:value-of select="@total-hits"/></p>
-->
                <table width="90%" cellpadding="4" border="1">
                    <tr>
                        <td>&#160;</td>
                        <td>Score</td>
                        <td>URL resp. File</td>
                    </tr>
                    <xsl:apply-templates select="hits/hit"/>
                </table>
            </xsl:when>
            <xsl:otherwise>
                <p><b>No results</b> found.</p>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="pages"/>
    </xsl:template>
    
    <xsl:template match="hit">
        <tr>
            <td valign="top">
                <xsl:value-of select="@pos"/>
            </td>
            <!--
  <td><xsl:value-of select="position()"/></td>
-->
            <td valign="top"><xsl:value-of select="score/@percent"/>%</td>
            <xsl:choose>
                <xsl:when test="path">
                    <td>File: <xsl:value-of select="path"/></td>
                </xsl:when>
                <xsl:when test="uri">
                    <td> Title: <a><xsl:attribute name="href"><xsl:value-of 
                        select="/search-and-results/search/publication-prefix"/><xsl:value-of 
                        select="normalize-space(uri)"/></xsl:attribute><xsl:apply-templates 
                        select="title"/></a><xsl:apply-templates 
                        select="no-title"/> <br /> <font size="-1">Excerpt: 
                        <xsl:apply-templates 
                        select="excerpt"/><xsl:apply-templates 
                        select="no-excerpt"/></font> <br /> <font 
                        size="-1">URL: <a><xsl:attribute 
                        name="href"><xsl:value-of 
                        select="/search-and-results/search/publication-prefix"/><xsl:value-of 
                        select="normalize-space(uri)"/></xsl:attribute><xsl:value-of 
                        select="/search-and-results/search/publication-prefix"/><xsl:apply-templates 
                        select="uri"/></a></font> <br /> <font 
                        size="-1">Mime-Type: <xsl:apply-templates 
                        select="mime-type"/><xsl:apply-templates 
                        select="no-mime-type"/></font> </td>
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
    <xsl:template match="no-title"> (No Title!) </xsl:template>
    <xsl:template match="excerpt"> ...&#160;<xsl:apply-templates/>&#160;... 
        </xsl:template>
    <xsl:template match="word">
        <b>
            <xsl:value-of select="."/>
        </b>
    </xsl:template>
    
    <xsl:template match="no-excerpt"> No excerpt available: <xsl:value-of 
        select="file/@src"/> </xsl:template>
    
    <xsl:template match="mime-type">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="no-mime-type"> No mime-type! </xsl:template>

    <xsl:template match="exception">
        <p>
            <font color="red">
                <xsl:value-of select="."/>
            </font>
        </p>
    </xsl:template>

    <xsl:template match="pages">
        <p> Result Pages <xsl:apply-templates select="page[@type='previous']" 
            mode="previous"/> <xsl:for-each select="page"> <xsl:choose> 
            <xsl:when test="@type='current'"> <xsl:value-of 
            select="position()"/> </xsl:when> <xsl:otherwise> <a 
            href="{$uriName}?publication-id={../../../search/publication-id}&amp;queryString={../../../search/query-string}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}"><xsl:value-of 
            select="position()"/></a> </xsl:otherwise> </xsl:choose> <xsl:text> 
            </xsl:text> </xsl:for-each> <xsl:apply-templates 
            select="page[@type='next']" mode="next"/> </p>
    </xsl:template>

    <xsl:template match="page" mode="next"> [<a 
        href="{$uriName}?publication-id={../../../search/publication-id}&amp;queryString={../../../search/query-string}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}">Next</a>&gt;&gt;] 
        </xsl:template>

    <xsl:template match="page" mode="previous"> [&lt;&lt;<a 
        href="{$uriName}?publication-id={../../../search/publication-id}&amp;queryString={../../../search/query-string}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}">Previous</a>] 
        </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
