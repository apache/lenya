<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
>

<xsl:param name="unid"/>

<xsl:template match="/save">
<save>
<xsl:attribute name="unid"><xsl:value-of select="$unid"/></xsl:attribute>
<xsl:apply-templates select="resource"/>
</save>
</xsl:template>

<xsl:template match="resource">
<xsl:variable name="newid"><xsl:value-of select="/save/form/field[@name='new.id']"/></xsl:variable>
<xsl:variable name="newdefaultlanguage"><xsl:value-of select="/save/form/field[@name='defaultlanguage']"/></xsl:variable>
<xsl:element name="resource">
<xsl:if test="@id != $newid"><xsl:attribute name="id"><xsl:value-of select="$newid"/></xsl:attribute></xsl:if>
<xsl:if test="@defaultlanguage != $newdefaultlanguage"><xsl:attribute name="defaultlanguage"><xsl:value-of select="$newdefaultlanguage"/></xsl:attribute></xsl:if>
<xsl:apply-templates select="translation" mode="info"/>
</xsl:element>
<xsl:apply-templates select="translation" mode="new"/>
<xsl:call-template name="newtranslation"/>
</xsl:template>

<xsl:template match="translation" mode="info">
<xsl:variable name="language"><xsl:value-of select="@language"/></xsl:variable>
<xsl:variable name="livenew"><xsl:value-of select="/save/form/field[@name=concat($language, '.live')]"/></xsl:variable>
<xsl:variable name="editnew"><xsl:value-of select="/save/form/field[@name=concat($language, '.edit')]"/></xsl:variable>
<xsl:variable name="href"><xsl:value-of select="/save/form/field[@name=concat($language , '.href')]"/></xsl:variable>
<xsl:variable name="title"><xsl:value-of select="/save/form/field[@name=concat($language , '.title')]"/></xsl:variable>
<xsl:variable name="newrevision"><xsl:if test="(string-length($href) &gt; 0) and (string-length($title) &gt; 0)">1</xsl:if></xsl:variable>
<xsl:element name="translation">
<xsl:attribute name="language"><xsl:value-of select="@language"/></xsl:attribute>
<xsl:choose>
<xsl:when test="/save/form/field[@name='delete'] = $language">
<xsl:attribute name="action">delete</xsl:attribute>
</xsl:when>
<xsl:otherwise>
<xsl:if test="(@live != $livenew) and (($livenew != 'new') or (newrevision='1'))">
<xsl:attribute name="live"><xsl:value-of select="$livenew"/></xsl:attribute>
</xsl:if>
<xsl:if test="(@edit != $editnew) and (newrevision != '1')"><xsl:attribute name="edit"><xsl:value-of select="$editnew"/></xsl:attribute></xsl:if>
<xsl:apply-templates select="revision" mode="info">
   <xsl:with-param name="language" select="$language"/>
</xsl:apply-templates>
</xsl:otherwise>
</xsl:choose>
</xsl:element>
</xsl:template>

<xsl:template match="translation" mode="new">
<xsl:variable name="language"><xsl:value-of select="@language"/></xsl:variable>
<xsl:call-template name="newrevision">
   <xsl:with-param name="language" select="$language"/>
</xsl:call-template>

</xsl:template>

<xsl:template match="revision" mode="info">
<xsl:param name="language"/>
<xsl:if test="/save/form/field[@name='delete'] = concat($language , '.', @revision)">
<xsl:element name="revision">
<xsl:attribute name="action">delete</xsl:attribute>
<xsl:attribute name="revision"><xsl:value-of select="@revision"/></xsl:attribute>
</xsl:element>
</xsl:if>
</xsl:template>

<xsl:template name="newtranslation">
<xsl:variable name="language"><xsl:value-of select="/save/form/field[@name='new.language']"/></xsl:variable>
<xsl:if test="string-length($language) &gt; 0">
<xsl:call-template name="newrevision">
   <xsl:with-param name="language" select="$language"/>
   <xsl:with-param name="fieldlanguage" select="'new'"/>
</xsl:call-template>
</xsl:if>
</xsl:template>

<xsl:template name="newrevision">
<xsl:param name="language"/>
<xsl:param name="fieldlanguage" select="$language"/>
<xsl:variable name="href"><xsl:value-of select="/save/form/field[@name=concat($fieldlanguage , '.href')]"/></xsl:variable>
<xsl:variable name="title"><xsl:value-of select="/save/form/field[@name=concat($fieldlanguage , '.title')]"/></xsl:variable>
<xsl:variable name="live"><xsl:value-of select="/save/form/field[@name=concat($fieldlanguage , '.live')]"/></xsl:variable>

<xsl:variable name="editrev"><xsl:value-of select="/save/resource/translation[@language = $fieldlanguage]/@edit"/></xsl:variable>
<xsl:variable name="edithref"><xsl:value-of select="/save/resource/translation[@language = $fieldlanguage]/revision[@revision = $editrev]/@href"/></xsl:variable>
<xsl:variable name="edittitle"><xsl:value-of select="/save/resource/translation[@language = $fieldlanguage]/revision[@revision = $editrev]/@title"/></xsl:variable>
<xsl:if test="($href != $edithref) or ($title != $edittitle)">
<xsl:element name="revision">
<xsl:attribute name="action">new</xsl:attribute>
<xsl:attribute name="language"><xsl:value-of select="$language"/></xsl:attribute>
<xsl:if test="$live = 'new'"><xsl:attribute name="live">true</xsl:attribute></xsl:if>
<link>
<xsl:attribute name="href"><xsl:value-of select="$href"/></xsl:attribute>
<xsl:attribute name="title"><xsl:value-of select="$title"/></xsl:attribute>
</link>
</xsl:element>
</xsl:if>
</xsl:template>

</xsl:stylesheet> 
