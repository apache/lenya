<?xml version="1.0"?>
<!--
This stylesheet contains the majority of templates for converting documentv11
to HTML.  It renders XML as HTML in this form:

<div class="content">
...
</div>

..which site2xhtml.xsl then combines with HTML from the index (book2menu.xsl)
and tabs (tab2menu.xsl) to generate the final HTML.

Section handling
- <a name/> anchors are added if the id attribute is specified

$Id: document2txt.xsl,v 1.1 2004/02/05 10:18:44 andreas Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
<xsl:strip-space elements="*"/>
  
<xsl:output method="text"/>

<!-- the skinconf file -->

<xsl:param name="notoc"/>
<xsl:param name="path"/>

<xsl:include href="../skins/common/xslt/html/dotdots.xsl"/>
<xsl:include href="../skins/common/xslt/html/pathutils.xsl"/>


<xsl:template name="underline">
<xsl:param name="text"/>
<xsl:param name="character" select="'-'"/>
<xsl:if test="$text != ''">
<xsl:value-of select="$character"/>
<xsl:call-template name="underline">
<xsl:with-param name="text" select="substring($text, 2)"/>
<xsl:with-param name="character" select="$character"/>
</xsl:call-template>
</xsl:if>
</xsl:template>


<!-- Path to site root, eg '../../' -->
<xsl:variable name="root">
<xsl:call-template name="dotdots">
<xsl:with-param name="path" select="$path"/>
</xsl:call-template>
</xsl:variable>

<xsl:variable name="filename-noext">
<xsl:call-template name="filename-noext">
<xsl:with-param name="path" select="$path"/>
</xsl:call-template>
</xsl:variable>

<xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>

<!-- ====================================================================== -->
<!-- document -->
<!-- ====================================================================== -->
<xsl:template match="document">

<xsl:if test="normalize-space(header/title) != ''">
<xsl:value-of select="translate(header/title, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/><xsl:text>
</xsl:text>
<xsl:call-template name="underline">
  <xsl:with-param name="text" select="header/title"/>
  <xsl:with-param name="character">=</xsl:with-param>
</xsl:call-template>
</xsl:if>

<xsl:if test="normalize-space(header/subtitle) != ''">
<xsl:value-of select="header/subtitle"/><xsl:text>
</xsl:text>
<xsl:call-template name="underline">
  <xsl:with-param name="text" select="header/subtitle"/>
  <xsl:with-param name="character">-</xsl:with-param>
</xsl:call-template>
</xsl:if>

<xsl:if test="header/authors">
<xsl:text>
</xsl:text>
<xsl:for-each select="header/authors/person">
<xsl:choose>
<xsl:when test="position()=1">by </xsl:when>
<xsl:otherwise>, </xsl:otherwise>
</xsl:choose>
<xsl:value-of select="@name"/>
</xsl:for-each>
</xsl:if>

<xsl:apply-templates select="body"/>

</xsl:template>


<xsl:template match="body">
<xsl:if test="section and not($notoc='true')">
<xsl:text>

CONTENTS
</xsl:text>
<xsl:for-each select="section">
<xsl:value-of select="position()"/>. <xsl:call-template name="toclink"/>
<xsl:if test="section">
<xsl:for-each select="section">
<xsl:value-of select="count(../preceding-sibling::section)+1"/>.<xsl:value-of select="count(preceding-sibling::section)+1"/> <xsl:call-template name="toclink"/><xsl:text>
</xsl:text>
</xsl:for-each>
</xsl:if><xsl:text>
</xsl:text>
</xsl:for-each>
<xsl:text>
</xsl:text>
</xsl:if>

<xsl:apply-templates/>
</xsl:template>


<!-- Generate a <a name="..."> tag for an @id -->
<xsl:template match="@id"/>


<xsl:template match="section">
<xsl:text>
  
</xsl:text>
<!-- count the number of section in the ancestor-or-self axis to compute
the title element name later on -->
<xsl:variable name="sectiondepth" select="count(ancestor-or-self::section)"/>
<xsl:apply-templates select="@id"/>
<!-- generate a title element, level 1 -> h3, level 2 -> h4 and so on... -->

<xsl:value-of select="count(preceding-sibling::section)+1"/>. <xsl:value-of select="title"/><xsl:text>
</xsl:text>
<xsl:call-template name="underline">
  <xsl:with-param name="text" select="concat(count(preceding-sibling::section)+1, '. ', title)"/>
  <xsl:with-param name="character">-</xsl:with-param>
</xsl:call-template>

<!-- Indent FAQ entry text 15 pixels -->
<!--
<xsl:variable name="indent">
<xsl:choose>
<xsl:when test="$notoc='true' and $sectiondepth = 3">
<xsl:text>15</xsl:text>
</xsl:when>
<xsl:otherwise>
<xsl:text>0</xsl:text>
</xsl:otherwise>
</xsl:choose>
</xsl:variable>
-->

<xsl:text>

</xsl:text>
<xsl:apply-templates select="*[not(self::title)]"/>
</xsl:template>


<xsl:template match="note | warning | fixme">
<xsl:text>
  
.................................................
</xsl:text>
<xsl:choose>
<xsl:when test="local-name() = 'note'">NOTE:</xsl:when>
<xsl:when test="local-name() = 'warning'">WARNING:</xsl:when>
<xsl:otherwise>FIXME (<xsl:value-of select="@author"/>)</xsl:otherwise>
</xsl:choose><xsl:text>
</xsl:text>
<xsl:apply-templates/>
<xsl:text>
.................................................

</xsl:text>
</xsl:template>



<xsl:template match="link">
<xsl:choose>
  
<xsl:when test="starts-with(@href, 'mailto:') and contains(@href, '@')">
<xsl:variable name="mailto-1" select="substring-before(@href,'@')"/>
<xsl:variable name="mailto-2" select="substring-after(@href,'@')"/>
<xsl:apply-templates/> [<xsl:value-of select="concat($mailto-1, '@', $mailto-2)"/>]</xsl:when>

<xsl:otherwise><xsl:apply-templates/> [<xsl:value-of select="@href"/>]</xsl:otherwise>

</xsl:choose>
</xsl:template>



<xsl:template match="jump">
<xsl:apply-templates/> (<xsl:value-of select="@href"/>)
</xsl:template>


<xsl:template match="fork">
<xsl:apply-templates/> (<xsl:value-of select="@href"/>)</xsl:template>


<xsl:template match="p[@xml:space='preserve']">

<xsl:apply-templates/>

</xsl:template>


<xsl:template match="source">
<xsl:apply-templates/>
<xsl:text>

</xsl:text>
</xsl:template>


<xsl:template match="anchor"/>


<xsl:template match="icon"/>


<xsl:template match="code"><xsl:value-of select="."/></xsl:template>


<xsl:template match="figure"/>


<xsl:template match="table"/>


<xsl:template match="acronym/@title"><xsl:value-of select="normalize-space(.)"/></xsl:template>


<xsl:template name="toclink"><xsl:value-of select="title"/></xsl:template>


<xsl:template match="p">
<xsl:apply-templates/>
<xsl:text>

</xsl:text>
</xsl:template>


<xsl:template match="ul|ol">
<xsl:text>
</xsl:text>
<xsl:apply-templates/>
</xsl:template>


<xsl:template match="ol/li">
<xsl:value-of select="count(preceding-sibling::li)+1"/>. <xsl:apply-templates/>
</xsl:template>


<xsl:template match="ul/li">
*. <xsl:apply-templates/>
</xsl:template>


<xsl:template match="@*" priority="-1"/>


<xsl:template match="node()" priority="-1">
<xsl:copy>
<xsl:apply-templates select="@*"/>
<xsl:apply-templates/>
</xsl:copy>
</xsl:template>


</xsl:stylesheet>
