<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : page2xhtml.xsl
    Created on : 11. April 2003, 11:09
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:simple="http://www.lenya.org/2003/simple-document"
    exclude-result-prefixes="xhtml"
    >

        
<xsl:template match="/">
  <div id="body">
    <xsl:apply-templates select="simple-document/body/node()"/>
  </div>
</xsl:template>


<xsl:template match="simple:p">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>


<xsl:template match="simple:crossheading">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>


<xsl:template match="simple:informaltable">
  <table>
    <xsl:apply-templates select="@*|node()"/>
  </table>
</xsl:template>


<xsl:template match="simple:row">
  <tr>
    <xsl:apply-templates select="@*|node()"/>
  </tr>
</xsl:template>


<xsl:template match="simple:entry">
  <td><xsl:apply-templates/></td>
</xsl:template>


<xsl:template match="simple:subtitle">
  <h1><xsl:apply-templates/></h1>
</xsl:template>


<xsl:template match="simple:listitem">
  <ul><xsl:apply-templates/></ul>
</xsl:template>


<xsl:template match="itemizedlist">
  <li><xsl:apply-templates/></li>
</xsl:template>


<xsl:template match="simple:tbody">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>


<xsl:template match="simple:tgroup">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>


<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
