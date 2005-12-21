<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
  xmlns:xlink="http://www.w3.org/1999/xlink"
>

<!-- default parameter value -->
<xsl:param name="rendertype" select="''"/>

<!-- TODO: The language does seem to be passed to this XSLT -->
<xsl:param name="language" select="'HUGO'"/>

<xsl:template match="office:document-content">
  <div id="body">
<!--
Language: <xsl:value-of select="$language"/>
-->
    <h1>OpenDocument Content (content.xml)</h1>
    <xsl:apply-templates select="office:body/office:text"/>
  </div>
</xsl:template>

<xsl:template match="text:p">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="text:list">
<ul>
  <xsl:apply-templates/>
</ul>
</xsl:template>

<xsl:template match="text:list-item">
<li>
  <xsl:apply-templates/>
</li>
</xsl:template>

<xsl:template match="text:a">
  <a href="{@xlink:href}"><xsl:apply-templates/></a>
</xsl:template>

</xsl:stylesheet>
