<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    >
    
<xsl:param name="chosenlanguage"/>

<xsl:template match="nav:site">
  <div id="search">
<form action=""><div style="display:inline;"><input type="hidden" name="lenya.usecase" value="search"/><input type="hidden" name="language" value="{$chosenlanguage}"/><input class="searchfield" type="text" name="query" alt="Search field"/><input class="searchsubmit" type="submit" i18n:attr="value" value="Search" name="find"/></div></form>
  </div>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
