<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- search and replace from XSLT cookbook -->
<xsl:template name="search-and-replace">
  <xsl:param name="input"/>
  <xsl:param name="search-string"/>
  <xsl:param name="replace-string"/>
  
  <xsl:choose>
    <!-- See if the input contains the search string -->
    <xsl:when test="$search-string and contains($input, $search-string)">
      <!-- If so, then concatenate the substring before the search
      string to the replacement string and to the result of
      recursively applying this template to the remaining substring.
      -->
      <xsl:value-of select="substring-before($input, $search-string)"/>
      <xsl:value-of select="$replace-string"/>
      <xsl:call-template name="search-and-replace">
        <xsl:with-param name="input" select="substring-after($input, $search-string)"/>
        <xsl:with-param name="search-string" select="$search-string"/>
        <xsl:with-param name="replace-string" select="$replace-string"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- There are no more occurences of the search string so
      just return the current input string -->
      <xsl:value-of select="$input"/>
    </xsl:otherwise>
  </xsl:choose>
  
</xsl:template>
  
  <!--
    Transform the first letter of a string to uppercase. Works only with latin characters.
  -->
  <xsl:template name="capitalize">
    <xsl:param name="text"/>
    <xsl:if test="string-length($text) &gt; 0">
      <xsl:variable name="firstLetter" select="substring($text, 1, 1)"/>
      <xsl:value-of select="translate($firstLetter, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
      <xsl:text/>
      <xsl:value-of select="substring($text, 2)"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>