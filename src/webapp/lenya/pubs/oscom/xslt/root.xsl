<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="tablecolor">orange</xsl:variable>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="oscom">
<html>
<head><title>OSCOM - Open Source Content Management</title></head>
<body bgcolor="#ffffff">
  <table width="750" cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td bgcolor="{$tablecolor}" colspan="8">
       <font face="verdana" color="white" size="+2"><b>OSCOM</b></font><br />
       <font face="verdana" color="white" size="0"><b>OPEN SOURCE CONTENT MANAGEMENT</b></font>
      </td>
    </tr>

    <tr>
      <td colspan="8" height="5"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>
    </tr>

    <tr>
      <td bgcolor="{$tablecolor}" colspan="8" height="2"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>
    </tr>

    <tr>
    <td valign="top" width="120">
    <font face="verdana" size="-1">
    Conferences<br />
    &#160;&#160;<a href="http://www.oscom.org/conferences/zurich2002/">Z&#252;rich</a><br />
    &#160;&#160;San Francisco<br />
    &#160;&#160;Japan<br />
    CMF/S Overview<br />
    Glossary<br />
    Mailing Lists<br />
    Board<br />
    </font>
    </td>

    <td width="8"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>
    <td bgcolor="{$tablecolor}" width="1"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>
    <td width="8"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>

    <td valign="top" width="460">
      <xsl:apply-templates select="about"/>
      <xsl:apply-templates select="features"/>
    </td>

    <td width="9"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>
    <td bgcolor="{$tablecolor}" width="1"><img src="images/pixel.gif" alt="." width="1" height="1"/></td>

    <td valign="top" width="150">
      <xsl:apply-templates select="news"/>
    </td>
    </tr>
  </table>
  <font face="verdana" size="-2">
  copyright &#169; 2002 oscom.org
  </font>
</body>
<span status="200"/>
</html>
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

<xsl:template match="rdf:RDF|rss">
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
  <!--  rdf:RDF -->
  <xsl:for-each select="n-rdf:item">
    <p>
    <a href="{n-rdf:link}" target="_blank"><xsl:value-of select="n-rdf:title"/></a>
    </p>
  </xsl:for-each>
  <!-- rss -->
  <xsl:for-each select="channel/item">
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
