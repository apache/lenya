<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
 <font face="verdana">
 <h3>Content Management Frameworks/Systems Overview</h3>
 <h4>Content Management Frameworks</h4>
  <table cellspacing="0" cellpadding="0" width="450">
  <tr>
    <td bgcolor="#000000">&#160;</td>
    <td height="20" bgcolor="#000000"><font size="-2" color="#ffffff"><b>project</b></font></td>
    <td bgcolor="#000000">&#160;</td>
    <td bgcolor="#ffffff">&#160;</td>
    <td bgcolor="#000000">&#160;</td>
    <td bgcolor="#000000"><font size="-2" color="#ffffff"><b>license</b></font></td>
  </tr>
  <xsl:apply-templates select="system[@type='framework']"/>
  </table>


 <h4>Content Management Systems</h4>
  <table cellspacing="0" cellpadding="0" width="450">
  <tr>
    <td bgcolor="#000000">&#160;</td>
    <td height="20" bgcolor="#000000"><font size="-2" color="#ffffff"><b>project</b></font></td>
    <td bgcolor="#000000">&#160;</td>
    <td bgcolor="#ffffff">&#160;</td>
    <td bgcolor="#000000">&#160;</td>
    <td bgcolor="#000000"><font size="-2" color="#ffffff"><b>license</b></font></td>
  </tr>
  <xsl:apply-templates select="system[@type='cms']"/>
  </table>
 </font>
</xsl:template>

<xsl:template match="system">
  <tr>
   <td>&#160;</td>
   <td height="20"><font size="-1"><a href="{main_url}"><xsl:value-of select="system_name"/></a></font></td>
   <td>&#160;</td>
   <td>&#160;</td>
   <td>&#160;</td>
   <td><font size="-1"><a><xsl:apply-templates select="license/license_url"/><xsl:value-of select="license/license_name"/></a></font></td>
  </tr>
</xsl:template>

<xsl:template match="license_url">
<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
 
</xsl:stylesheet>  
