<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    
<xsl:param name="area"/>

<xsl:template match="nav:site">
  <div id="search">
    <form action="{@href}../search-{$area}/lucene"><input class="searchfield" type="text" name="queryString"/><input class="searchsubmit" type="submit" value="Search" name="find"/></form>
  </div>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
