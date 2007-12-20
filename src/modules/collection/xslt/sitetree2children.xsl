<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:site="http://apache.org/cocoon/lenya/sitetree/1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  exclude-result-prefixes="site">
  
  
  <xsl:param name="language"/>
  <xsl:param name="allLanguages"/>
  <xsl:param name="defaultLanguage"/>
  
  
  <xsl:template match="site:fragment">
    <col:collection>
      <xsl:apply-templates select="site:node"/>
    </col:collection>
  </xsl:template>
  
  
  <xsl:template match="site:node">
    <xsl:choose>
      <xsl:when test="$allLanguages = 'true'">
        <xsl:choose>
          <xsl:when test="site:label[lang($language)]">
            <xsl:apply-templates select="site:label[lang($language)]"/>
          </xsl:when>
          <xsl:when test="site:label[lang($defaultLanguage)]">
            <xsl:apply-templates select="site:label[lang($defaultLanguage)]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="site:label[1]"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="site:label[lang($language)]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="site:label">
    <col:document uuid="{../@uuid}">
      <xsl:copy-of select="@xml:lang"/>
    </col:document>
  </xsl:template>

</xsl:stylesheet>