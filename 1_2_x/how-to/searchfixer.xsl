<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0" 
    xmlns:session="http://www.apache.org/xsp/session/2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
    <xsl:param name="area" select="'live'"/>

<xsl:variable name="pubid">
<xsl:value-of select="search-and-results/configuration/publication/name" />
</xsl:variable>


    <xsl:template match="search-results">
        <xsl:apply-templates select="search-and-results"/>
    </xsl:template>

    <xsl:template match="search-and-results">
       <search-and-results>
         <configuration>
           <xsl:apply-templates select="configuration"/>
         </configuration>
         <xsl:apply-templates select="search"/>
         <xsl:apply-templates select="results"/>
       </search-and-results>
    </xsl:template>
    
    <xsl:template match="configuration">
        <xsl:apply-templates/>
        <xsl:apply-templates select="//search-results/publication/languages" />
    </xsl:template>

    <xsl:template match="languages">
        <languages>
           <xsl:apply-templates select="language"/>
        </languages>
    </xsl:template>

    <xsl:template match="results">
         <results total-hits="{total-hits}">
           <xsl:apply-templates select="pages"/>
           <xsl:apply-templates select="hits"/>
         </results>
    </xsl:template>

    <xsl:template match="hits">
       <hits>
          <xsl:apply-templates select="hit"/>
      </hits>
    </xsl:template>

    <xsl:template match="hit">
       <xsl:if test="uri[@filename != 'sitetree.xml']">
          <hit pos="{@pos}">
             <xsl:apply-templates select="score"/>
             <xsl:apply-templates select="uri"/>
<title>
<xsl:choose>
<xsl:when test="string-length(fields/title) &gt; 0"><xsl:value-of select="fields/title"/></xsl:when>
<xsl:when test="string-length(fields/htmltitle) &gt; 0"><xsl:value-of select="fields/htmltitle"/></xsl:when>
<xsl:when test="string-length(title) &gt; 0"><xsl:value-of select="title"/></xsl:when>
<xsl:otherwise>Untitled</xsl:otherwise>
</xsl:choose>
</title>

<!--             <xsl:apply-templates select="excerpt"/>   Lucene Excerpt -->
<!--             <xsl:apply-templates select="fields/description"/> Lenya Description -->
             <xsl:apply-templates select="fields/htmlbody"/> <!-- HTML Body -->
          </hit>
       </xsl:if>
    </xsl:template>
    
    <xsl:template match="uri">
 <uri><xsl:value-of select="$pubid"/><xsl:value-of select="@parent"/>_<xsl:value-of select="../fields/language"/>.html</uri>
    </xsl:template>
    
    <xsl:template match="description">
        <excerpt><xsl:value-of select="."/></excerpt>
    </xsl:template>
    <xsl:template match="htmlbody">
        <excerpt><xsl:apply-templates/></excerpt>
    </xsl:template>

    <xsl:template match="@*|node()" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
