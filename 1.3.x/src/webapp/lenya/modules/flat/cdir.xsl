<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<xsl:param name="area" select="live"/>

<xsl:template match="/dir:directory" priority="3">
<files>
<xsl:apply-templates select="dir:directory"/>
</files>
</xsl:template>

<xsl:template match="dir:directory">
   <xsl:param name="path" select="''"/>
   <xsl:apply-templates select="dir:file">
      <xsl:with-param name="path" select="concat($path, '/', @name)"/>
   </xsl:apply-templates>
   <xsl:apply-templates select="dir:directory">
      <xsl:with-param name="path" select="concat($path, '/', @name)"/>
   </xsl:apply-templates>
</xsl:template>

<xsl:template match="dir:file[contains(@name, '_')]">
<xsl:param name="path" select="''"/>
<xsl:element name="file">
<xsl:attribute name="idl"><xsl:value-of select="$path"/>_<xsl:value-of select="substring-before(substring-after(@name, '_'), '.')"/></xsl:attribute>
<xsl:attribute name="filename"><xsl:value-of select="$path"/>/<xsl:value-of select="@name"/></xsl:attribute>
<xsl:attribute name="time"><xsl:value-of select="@lastModified"/></xsl:attribute>
</xsl:element>
</xsl:template>



</xsl:stylesheet> 