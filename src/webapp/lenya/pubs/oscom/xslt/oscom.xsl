<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
                xmlns:error="http://apache.org/cocoon/error/2.0" 
                xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:output version="1.0" indent="yes"/>

<xsl:template match="oscom">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>OSCOM - Open Source Content Management</title></head>
<body bgcolor="#ffffff">
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td bgcolor="{$tablecolor}" colspan="8">
       <font face="verdana" color="white" size="+2"><b>OSCOM</b></font><br />
       <font face="verdana" color="white" size="0"><b>OPEN SOURCE CONTENT MANAGEMENT</b></font>
      </td>
    </tr>

    <tr>
    <td valign="top" width="468">
      <xsl:call-template name="body"/>
    </td>

    <td valign="top" width="150">
      <xsl:apply-templates select="news"/>
      <xsl:apply-templates select="related-content"/>
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
 
</xsl:stylesheet>
