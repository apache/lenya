<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="lenya.org.xsl"/>

<xsl:template match="/">
  <xsl:apply-templates/>
<!--
  <xsl:call-template name="body"/>
-->
</xsl:template>

<xsl:template name="body">
 <xsl:apply-templates select="/site/tree"/><br />
</xsl:template>

<xsl:template match="tree">
  <html>
  <head><title>Lenya CMS Documentation</title></head>
  <body>
  <font face="verdana">
  <xsl:apply-templates select="branch" mode="trunk"/>
  </font>
  </body>
  </html>
</xsl:template>

<xsl:template match="branch" mode="trunk">
 <h1>Documentation</h1>
 <p>
We are currently migrating from <b>HTML</b> to <b>xdoc</b>.
The previous documentation can still be found <a href="previous-index.html">here</a>.
 </p>
 <p>
We also provide an expandable/collapsable <a href="js-index.html">ToC</a> based on a JavaScript from Dieter Bungers.
 </p>
 <h2>Table of Contents</h2>
 <ol>
  <xsl:apply-templates mode="tree">
    <xsl:with-param name="parentPath">/lenya/docs</xsl:with-param>
  </xsl:apply-templates>
 </ol>
</xsl:template>

<xsl:template match="branch" mode="tree">
 <xsl:param name="parentPath"/>
 <li>
 <xsl:choose>
   <xsl:when test="@doctype='Guide'">
     <a name="{@relURI}"/>
     <b><xsl:value-of select="@menuName"/></b>
   </xsl:when>
   <xsl:when test="@doctype='Empty'">
     <xsl:value-of select="@menuName"/>
   </xsl:when>
   <xsl:when test="@doctype='HTML'">
     <a href="{$parentPath}/{@relURI}/index.html"><xsl:value-of select="@menuName"/></a>
   </xsl:when>
   <xsl:when test="@doctype='XDoc'">
     <a href="xdocs/{@relURI}"><xsl:value-of select="@menuName"/></a>
   </xsl:when>
   <xsl:otherwise>
   [template match="branch" mode="tree"] EXCEPTION: No such doctype
   </xsl:otherwise>
 </xsl:choose>
 <ol>
  <xsl:apply-templates mode="tree">
    <xsl:with-param name="parentPath"><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@relURI"/></xsl:with-param>
  </xsl:apply-templates>
 </ol>
   <xsl:if test="@doctype='Guide'">
   <br />&#160;
   </xsl:if>
 </li>
</xsl:template>

<xsl:template match="leaf" mode="tree">
 <xsl:param name="parentPath"/>
 <xsl:choose>
   <xsl:when test="@doctype='HTML'">
     <li><a>
     <xsl:attribute name="href">
     <xsl:if test="@relURI"><xsl:value-of select="$parentPath"/>/</xsl:if>
     <xsl:apply-templates select="@relURI"><xsl:with-param name="parentPath" select="$parentPath"/></xsl:apply-templates>
     <xsl:apply-templates select="@absURI"/>
     <xsl:apply-templates select="@URL"/>
     </xsl:attribute>
     <xsl:if test="@URL">
     <xsl:attribute name="target">_blank</xsl:attribute>
     </xsl:if>
     <xsl:value-of select="@menuName"/></a></li>
     <!--
     <li><a href="{$parentPath}/{@relURI}.html"><xsl:value-of select="@menuName"/></a></li>
     -->
   </xsl:when>
   <xsl:when test="@doctype='XDoc'">
     <li><a href="xdocs/{@relURI}"><xsl:value-of select="@menuName"/></a></li>
   </xsl:when>
   <xsl:when test="@doctype='Empty'">
     <li><xsl:value-of select="@menuName"/></li>
   </xsl:when>
   <xsl:when test="@doctype='Shared'">
     <li><a href="{@relURI}"><xsl:value-of select="@menuName"/></a></li>
   </xsl:when>
   <xsl:otherwise>
     ! INVALID DOCTYPE DEFINITION !
   </xsl:otherwise>
 </xsl:choose>
</xsl:template>

</xsl:stylesheet>

