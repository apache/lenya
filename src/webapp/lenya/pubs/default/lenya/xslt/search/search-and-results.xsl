<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0" 
    xmlns:session="http://www.apache.org/xsp/session/2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
>
    
    <xsl:param name="area" select="'live'"/>
    <xsl:param name="contextprefix"/>

    <xsl:variable name="languagesSelected"><xsl:value-of select="//search-and-results/search/language"/></xsl:variable>

    
    <xsl:template match="search-and-results">
<lenya:meta><dc:subject>Search</dc:subject></lenya:meta>
        <xhtml:div id="body">
<H2><i18n:text key="search-pagetitle">Search</i18n:text></H2>
                <div class="lenya-box">
                    <div class="lenya-box-body">
                        <form id="searchvalues"><input type="hidden" name="lenya.usecase" value="search"/>
                            <table class="lenya-table-noborder">
                                <tr>
                                    <td><i18n:text key="search-fieldlabel">Search</i18n:text></td>
                                    <td>
                                        <input type="text" name="query" size="60" class="lenya-form-element">
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="search/query"/>
                                            </xsl:attribute>
                                        </input>
                                    </td>
                                </tr>
<tr><td><i18n:text key="search-languagefieldlabel">Language(s)</i18n:text></td><td>
                                <xsl:apply-templates select="configuration/languages/language"/>
</td></tr>
                                <tr>
                                    <td><i18n:text key="search-sortfieldlabel">Sort by</i18n:text></td>
                                    <td>
                                        <select name="sortBy" class="lenya-form-element">
                                            <option value="score"><xsl:if test="search/sort-by='score'"> 
                                                <xsl:attribute name="selected">selected</xsl:attribute> 
                                                </xsl:if><i18n:text key="search-sort-scorevalue">Score</i18n:text></option>
                                            <option value="title"> <xsl:if 
                                                test="search/sort-by='title'"> 
                                                <xsl:attribute 
                                                name="selected">selected</xsl:attribute> 
                                                </xsl:if><i18n:text key="search-sort-titlevalue">Title</i18n:text></option>
                                        </select>
                                    </td>
                                </tr>
                            </table>
<input type="submit" value="Search"/>
                        </form>

                        <xsl:apply-templates select="search/exception"/>
                        <xsl:apply-templates select="results"/>
                    </div>
                </div>
            </xhtml:div>
    </xsl:template>

    <xsl:template match="language">
        <input type="checkbox" name="language">
           <xsl:attribute name="value">
               <xsl:value-of select="."/>
           </xsl:attribute>
          <xsl:if test="contains($languagesSelected, .)">
              <xsl:attribute name="checked"/>
          </xsl:if>
       </input>
       <xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="results">
        <h3>Results</h3>
        <xsl:choose>
            <xsl:when test="hits">
                <p> <i18n:text key="search-results-summarypages">Documents</i18n:text> <xsl:value-of 
                    select="pages/page[@type='current']/@start"/> <i18n:text key="search-results-summaryto">-</i18n:text> 
                    <xsl:value-of select="pages/page[@type='current']/@end"/> 
                    <i18n:text key="search-results-summaryof">of</i18n:text> <xsl:value-of select="@total-hits"/> <i18n:text key="search-results-summaryfit">matches</i18n:text></p>
                <table width="90%" cellpadding="4" class="lenya-table">
                    <tr>
                        <td><i18n:text key="search-results-columncount">&#160;</i18n:text></td>
                        <td><i18n:text key="search-results-columnscore">Score</i18n:text></td>
                        <td><i18n:text key="search-results-columninfo">Document</i18n:text></td>
                    </tr>
                    <xsl:apply-templates select="hits/hit"/>
                </table>
            </xsl:when>
            <xsl:otherwise>
                <p><strong><i18n:text key="search-noresults">No results found.</i18n:text></strong></p>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="pages"/>
    </xsl:template>
    
    <xsl:template match="hit">
        <tr>
            <td valign="top">
                <xsl:value-of select="@pos"/>
            </td>
            <td valign="top"><xsl:value-of select="score/@percent"/>%</td>
       <td><strong>
<a><xsl:attribute name="href"><xsl:apply-templates select="uri"/></xsl:attribute><xsl:apply-templates select="title"/></a></strong><br/>
<xsl:apply-templates select="excerpt"/>
<xsl:apply-templates select="no-excerpt"/>
</td>

        </tr>
    </xsl:template>
    
    <xsl:template match="uri">/<xsl:value-of select="/search-and-results/configuration/publication/@pid"/>/live<xsl:value-of select="."/>

    </xsl:template>
    
    <xsl:template match="title">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="no-title"> (No Title.) </xsl:template>

    <xsl:template match="excerpt"><xsl:apply-templates/></xsl:template>

    <xsl:template match="word">
        <strong>
            <xsl:value-of select="."/>
        </strong>
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
        <p><i18n:text key="search-resultpages">Result Pages</i18n:text> 
<xsl:apply-templates select="page[@type='previous']" mode="previous"/>
<xsl:for-each select="page">
<xsl:choose> 
   <xsl:when test="@type='current'">
       <xsl:value-of select="position()"/>
   </xsl:when>
   <xsl:otherwise>
       <a href="?lenya.usecase=search&amp;query={../../../search/query}&amp;language={$languagesSelected}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}">
<xsl:value-of select="position()"/></a>
   </xsl:otherwise>
</xsl:choose>
</xsl:for-each>
<xsl:apply-templates select="page[@type='next']" mode="next"/>
</p>
    </xsl:template>

    <xsl:template match="page" mode="next">
[<a href="?lenya.usecase=search&amp;query={../../../search/query}&amp;language={$languagesSelected}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}"><i18n:text>Next</i18n:text></a>&gt;&gt;] 
        </xsl:template>

    <xsl:template match="page" mode="previous">
[&lt;&lt;<a href="?lenya.usecase=search&amp;query={../../../search/query}&amp;language={$languagesSelected}&amp;sortBy={../../../search/sort-by}&amp;start={@start}&amp;end={@end}"><i18n:text>Previous</i18n:text></a>] 
        </xsl:template>

    <xsl:template match="@*|node()" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
