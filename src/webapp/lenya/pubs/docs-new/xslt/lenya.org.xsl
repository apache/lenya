<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" version="1.0" indent="yes"/>

<!-- CONTEXT_PREFIX is just a temporary setting, will be given by general logicsheet -->
<xsl:variable name="CONTEXT_PREFIX">/lenya/lenya.org</xsl:variable>
<xsl:variable name="images"><xsl:value-of select="$CONTEXT_PREFIX"/>/images</xsl:variable>

<xsl:include href="navigation.xsl"/>

<xsl:template match="site">
 <html>
  <head>
   <link href="{$CONTEXT_PREFIX}/css/pc-mac-lunix.css" type="text/css" rel="stylesheet"/>
<!--
   <title><xsl:call-template name="htmltitle"/></title>
-->
   <title>Lenya - Open Source Content Management - Documentation - <xsl:apply-templates select="document/title"/></title>
  </head>
  <body bgcolor="#ffffff">
   <table width="760" cellpadding="0" cellspacing="0" border="0">
    <tr>
     <td>
     <img src="{$images}/lenya_org.gif"/>
     </td>
     <td align="right" valign="top">
     <span class="revision">
     <a href="mailto:contact@lenya.org">contact@lenya.org</a>
     </span>
     </td>
    </tr>
    <tr>
     <td height="20" colspan="2">&#160;</td>
    </tr>
   </table>

   <table cellpadding="0" cellspacing="0" border="0">
    <tr>
     <td valign="top"> 
     <table width="120" border="0"><tr><td>
     <span class="nav">
       <xsl:apply-templates select="lenya.org:navigation" xmlns:lenya.org="http://www.lenya.org/2002/lenya.org"/>
     </span>
     </td></tr></table>
     </td>
     <td width="20">&#160;&#160;&#160;&#160;&#160;&#160;&#160;</td>
     <td valign="top">
      <xsl:call-template name="body"/>
     </td>
    </tr>
   </table>
<p>
<font size="-2" face="verdana">
copyright &#169; 2003 lenya.org
</font>
</p>
  </body>
 </html>
</xsl:template>

</xsl:stylesheet>
