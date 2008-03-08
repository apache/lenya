<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
      <xsl:apply-templates select="merge" mode="root"/>
</xsl:template>

<xsl:template match="merge" mode="root">
   <xsl:element name="{name(node()[1])}" namespace="{namespace-uri(node()[1])}">
      <xsl:apply-templates select="node()[1]/@*" mode="properties"/>
      <xsl:apply-templates select="node()[2]/@*" mode="properties"/>
      <xsl:apply-templates select="node()[1]/*" mode="template">
         <xsl:with-param name="otherroot" select="node()[2]"/>
      </xsl:apply-templates>
   </xsl:element>
</xsl:template>

<xsl:template match="@*" mode="properties">
   <xsl:copy/>
</xsl:template>
<xsl:template match="*|text()" mode="properties"/>

<xsl:template match="*" mode="template">
   <xsl:param name="otherroot"/>
   <xsl:variable name="name"><xsl:value-of select="name()"/></xsl:variable>
   <xsl:variable name="replacenode" select="$otherroot/node()[name() = $name]"/>
   <xsl:element name="{name(.)}" namespace="{namespace-uri(.)}">
      <xsl:apply-templates select="./@*" mode="properties"/>
   <xsl:choose>
      <xsl:when test="$replacenode">
         <xsl:call-template name="mergetemplate">
            <xsl:with-param name="node1" select="." />
            <xsl:with-param name="node2" select="$replacenode" />
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
          <xsl:apply-templates select="*" mode="copy"/>
      </xsl:otherwise>
   </xsl:choose>
   </xsl:element>
</xsl:template>

<xsl:template match="*" mode="extras">
   <xsl:param name="otherroot"/>
   <xsl:variable name="name" select="name()"/>
   <xsl:variable name="replacenode" select="$otherroot/node()[name() = $name]"/>
   <xsl:choose>
      <xsl:when test="$replacenode">
         <xsl:apply-templates select="*" mode="extras">
            <xsl:with-param name="otherroot" select="$replacenode"/>
         </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
          <xsl:apply-templates select="." mode="copy"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="mergetemplate">
   <xsl:param name="node1"/>
   <xsl:param name="node2"/>
   <xsl:apply-templates select="$node2" mode="properties"/>
   <xsl:apply-templates select="$node2" mode="text"/>
   <xsl:apply-templates select="$node1/*" mode="template">
       <xsl:with-param name="otherroot" select="$node2"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="$node2/*" mode="extras">
       <xsl:with-param name="otherroot" select="$node1"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="text"><xsl:value-of select="child::text()"/></xsl:template>
<xsl:template match="*|@*" mode="copy">
   <xsl:copy>
      <xsl:apply-templates select="*|@*|text()" mode="copy"/>
   </xsl:copy>
</xsl:template>

</xsl:stylesheet>
