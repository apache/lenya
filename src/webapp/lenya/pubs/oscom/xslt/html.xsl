<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/" xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="tablecolor">orange</xsl:variable>
<!-- context_prefix is just a temporary setting, will be given by general logicsheet -->
<xsl:variable name="CONTEXT_PREFIX">/wyona-cms/oscom</xsl:variable>
<xsl:variable name="images"><xsl:value-of select="$CONTEXT_PREFIX"/>/images</xsl:variable>

<xsl:include href="navigation.xsl"/>
<xsl:include href="oscom.xsl"/>

<!--
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
      <td colspan="8" height="5"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
    </tr>

    <tr>
      <td bgcolor="{$tablecolor}" colspan="8" height="2"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
    </tr>

    <tr>
    <td valign="top" width="120">
      <xsl:apply-templates select="oscom:navigation"/>
    </td>

    <td width="8"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
    <td bgcolor="{$tablecolor}" width="1"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
    <td width="8"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>

    <td valign="top" width="460">
      <xsl:call-template name="body"/>
    </td>

    <td width="9"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
    <td bgcolor="{$tablecolor}" width="1"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>

    <td valign="top" width="150">
      <xsl:apply-templates select="news"/>
    </td>
    </tr>
  </table>
  <font face="verdana" size="-2">
  copyright &#169; 2002 oscom.org&#160;&#160;&#160;&#160;&#160;&#160;Please contact <a href="mailto:abuse@oscom.org">abuse@oscom.org</a> to address spam or abuse complaints
  </font>
</body>
<span status="200"/>
</html>
</xsl:template>
-->
 
</xsl:stylesheet>  
