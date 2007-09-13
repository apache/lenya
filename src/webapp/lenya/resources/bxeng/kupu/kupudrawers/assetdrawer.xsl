<?xml version="1.0" encoding="UTF-8" ?>
<!--
##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################

XSL transformation from Kupu Library XML to HTML for the link library
drawer.

$Id: linkdrawer.xsl 810 2004-09-14 11:49:42Z silvan $
-->
<xsl:stylesheet
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:import
    href="librarydrawer.xsl"
    />

  <xsl:template match="resource|collection" mode="properties">
   <form onsubmit="return false;">
     <table>
       <tr>
         <td>
           <strong>Title</strong><br />
            <input type="text" id="asset_title" size="10"/>
         </td>
       </tr>
       <tr>
         <td>
           <strong>Type</strong><br />
            <input type="text" id="asset_type" size="10" value="application/octet-stream"/>
         </td>
       </tr>
       <tr>
         <td>
           <strong>Lang</strong><br />
            <input type="text" id="asset_lang" size="10"/>
         </td>
       </tr>
       <tr>
         <td>
           <strong>CSS-Class</strong><br />
           <input type="text" id="asset_cssclass" size="10" />
         </td>
       </tr>
       <tr>
         <td>
           <strong>Target</strong><br />
           <input type="text" id="asset_target" value="_self" size="10" />
         </td>
       </tr>
     </table>
    </form>
  </xsl:template>
</xsl:stylesheet>
