<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="about"/>
  <xsl:apply-templates select="features"/>
</xsl:template>

<xsl:template match="about">
 <font face="verdana">
 <xsl:apply-templates/>
 </font>
</xsl:template>

<xsl:template match="features">
 <font face="verdana">
 <xsl:apply-templates/>
 </font>
</xsl:template>

<xsl:template match="feature">
 <h3><xsl:apply-templates select="title"/></h3>
 <xsl:apply-templates select="p"/>
</xsl:template>

<xsl:template match="p">
 <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="a">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="news">
  <xsl:apply-templates select="site"/>
</xsl:template>

<xsl:template match="site">
  <xsl:apply-templates select="rdf:RDF"/>
  <xsl:apply-templates select="rss"/>
  <!--<xsl:apply-templates select="error:notify"/>-->
</xsl:template>

<xsl:template match="rdf:RDF">
<!--<xsl:template match="rdf:RDF|rss">-->
 <table cellpadding="0" cellspacing="0" border="0" width="150">
  <tr>
    <td bgcolor="{$tablecolor}">&#160;</td>
    <td bgcolor="{$tablecolor}">
      <p>
        <font face="verdana" color="white"><xsl:value-of select="../@name"/>
        &#160;<a href="{n-rdf:channel/n-rdf:link}" target="_blank">&#187;</a></font>
      </p>
    </td>
  </tr>
  <tr>
    <td>&#160;</td>
    <td>
  <font face="verdana" size="-2">
  <xsl:variable name="n">5</xsl:variable>
  <!--  rdf:RDF -->
  <xsl:for-each select="n-rdf:item[position() &lt;= $n]">
    <p>
    <a href="{n-rdf:link}" target="_blank"><xsl:value-of select="n-rdf:title"/></a>
    </p>
  </xsl:for-each>
  <!-- rss -->
<!--
  <xsl:for-each select="channel/item[position() &lt;= $n]">
    <p>
    <a href="{link}" target="_blank"><xsl:value-of select="title"/></a>
    </p>
  </xsl:for-each>
-->
  </font>
    </td>
  </tr>
  <tr>
    <td colspan="2">&#160;</td>
  </tr>
 </table>

<!--
  <p>
  <i><xsl:apply-templates/></i>
  <RDF><xsl:copy-of select="*"/></RDF>
  </p>
-->
</xsl:template>

<xsl:template match="rss">
 <table cellpadding="0" cellspacing="0" border="0" width="150">
  <tr>
    <td bgcolor="{$tablecolor}">&#160;</td>
    <td bgcolor="{$tablecolor}">
      <p>
        <font face="verdana" color="white"><xsl:value-of select="../@name"/>
        &#160;<a href="{channel/link}" target="_blank">&#187;</a></font>
      </p>
    </td>
  </tr>
  <tr>
    <td>&#160;</td>
    <td>
  <font face="verdana" size="-2">
  <xsl:variable name="n">5</xsl:variable>
  <!-- rss -->
  <xsl:for-each select="channel/item[position() &lt;= $n]">
    <p>
    <a href="{link}" target="_blank"><xsl:value-of select="title"/></a>
    </p>
  </xsl:for-each>
  </font>
    </td>
  </tr>
  <tr>
    <td colspan="2">&#160;</td>
  </tr>
 </table>

<!--
  <p>
  <i><xsl:apply-templates/></i>
  <RDF><xsl:copy-of select="*"/></RDF>
  </p>
-->
</xsl:template>

<xsl:template match="error:notify">
  EXCEPTION
</xsl:template>
 
</xsl:stylesheet>  
