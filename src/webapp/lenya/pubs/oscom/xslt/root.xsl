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
    <a href="index.html">Home</a><br />
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
      <xsl:call-template name="body"/>
<!--
      <xsl:apply-templates select="about"/>
      <xsl:apply-templates select="features"/>
-->
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
 
</xsl:stylesheet>  
