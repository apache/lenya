<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: lenya.org.xsl,v 1.4 2004/03/13 12:42:07 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output version="1.0" indent="yes"/>

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
copyright &#169; 2003 Apache Software Foundation
</font>
</p>
  </body>
 </html>
</xsl:template>

</xsl:stylesheet>
