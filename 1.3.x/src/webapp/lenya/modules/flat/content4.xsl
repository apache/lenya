<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
<!-- Sort id="/index" first. -->
<!-- Sort and Unique "index" elements.   -->

<xsl:key name="uniqueIndex" match="index" use="concat(../@unid, @name)"/>
<xsl:template match="/content">
<resources>
   <xsl:apply-templates select="@*"/>
   <xsl:apply-templates select="resource[@id='/index']"/>
   <xsl:apply-templates select="resource[@id!='/index']"/>
</resources>
</xsl:template>

<xsl:template match="resource">
<xsl:copy>
   <xsl:apply-templates select="@*"/>
   <xsl:choose>
      <xsl:when test="@id='/index'">
         <!-- Homepage Indexes -->
<index name="authoring" position="1"/>
<index name="homepage" position="1"/>
<index name="live" position="1"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:apply-templates select="index"><xsl:sort select="@name"/></xsl:apply-templates>
      </xsl:otherwise>
   </xsl:choose>
   <xsl:apply-templates select="*[local-name() != 'index']"/>
</xsl:copy>
</xsl:template>

<xsl:template match="index">
<xsl:if test="generate-id()=generate-id(key('uniqueIndex', concat(../@unid, @name)))">
<xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
</xsl:copy>
</xsl:if>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 