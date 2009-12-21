<?xml version="1.0" encoding="utf-8"?>

<!-- 
   xml prettyprinter for apache cocoon/lenya, contributed by <nettings@apache.org>
   everything that is non-trivial in this script has been borrowed from somewhere.
   this script is in the public domain.
-->

<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xhtml="http://www.w3.org/1999/xhtml"
>
<xsl:output method="xml"/>

<xsl:param name="indent-increment" select="'  '" />

  <!-- 
    indentation
    thanks to John Mongan, taken from http://www.dpawson.co.uk/xsl/sect2/pretty.html
  -->

  <xsl:template match="*">
    <xsl:param name="indent" select="'&#xA;'"/>

    <xsl:value-of select="$indent"/>

    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates>
        <xsl:with-param name="indent" select="concat($indent, $indent-increment)"/>
      </xsl:apply-templates>
      <!-- add a trailing newline if the node has children and is not a mixed content node -->
      <xsl:if test="* and not(*[../text()[normalize-space(.) != '']])">
        <xsl:value-of select="$indent"/>
      </xsl:if>
    </xsl:copy>
   </xsl:template>

   <xsl:template match="comment()|processing-instruction()">
      <xsl:copy />
   </xsl:template>

  <!-- 
    mixed content detection and handling
    thanks to David Carlisle and Wendell Piez, taken from http://www.dpawson.co.uk/xsl/sect2/normalise.html#d7206e52 
  -->
  <xsl:template match="*[../text()[normalize-space(.) != '']]">
    <!-- but this template matches any element appearing in mixed content -->
    <xsl:variable name="textbefore"
         select="preceding-sibling::node()[1][self::text()]"/>
    <xsl:variable name="textafter"
         select="following-sibling::node()[1][self::text()]"/>
    <!-- Either of the preceding variables will be an empty node set 
         if the neighbor node is not text(), right? -->
    <xsl:variable name="prevchar"
         select="substring($textbefore, string-length($textbefore))"/>
    <xsl:variable name="nextchar"
         select="substring($textafter, 1, 1)"/>
  
    <!-- Now the action: -->
    <xsl:if test="$prevchar != normalize-space($prevchar)">
    <!-- If the original text had a space before, add one back -->
      <xsl:text> </xsl:text>
    </xsl:if>
  
    <xsl:copy>
    <!-- Copy the element over -->
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  
    <xsl:if test="$nextchar != normalize-space($nextchar)">
    <!-- If the original text had a space after, add one back -->
      <xsl:text> </xsl:text>
    </xsl:if>
  
  </xsl:template>

<!--
  normalize all whitespace in text nodes (i.e. those that don't get matched by the mixed content handler)
-->
  <xsl:template match="text()">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

<!--
  leave the contents of the xhtml pre tag alone to preserve intended formatting
-->
  <xsl:template match="xhtml:pre/node()">
    <xsl:copy-of select="."/>
  </xsl:template>

<!--
  leave the contents of script tags alone, since we don't know about script whitespace semantics
-->
  <xsl:template match="xhtml:script/node()">
    <xsl:copy-of select="."/>
  </xsl:template>
  
<!-- 
  prevent collapsing of <textarea> </textarea> to <textarea /> 
-->
  <xsl:template match="xhtml:textarea/node()">
    <xsl:copy-of select="."/>
  </xsl:template>
</xsl:stylesheet>
