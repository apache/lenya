<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: oscom.xsl,v 1.10 2004/03/13 12:42:18 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
                xmlns:error="http://apache.org/cocoon/error/2.0" 
                xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:output version="1.0" indent="yes"/>

<xsl:template match="oscom">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>OSCOM - Open Source Content Management</title></head>
<body bgcolor="#ffffff">
  <table cellpadding="0" cellspacing="0" border="0">
    <!-- HEAD -->
    <tr>
      <td bgcolor="{$tablecolor}" colspan="8">
       <font face="verdana" color="white" size="+2"><b>OSCOM</b></font><br />
       <font face="verdana" color="white" size="0"><b>OPEN SOURCE CONTENT MANAGEMENT</b></font>
      </td>
    </tr>

    <tr>
      <!-- NAVIGATION -->
      <td valign="top" width="120">
        <xsl:apply-templates select="oscom_navigation"/>
      </td>

      <td width="8"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
      <td bgcolor="{$tablecolor}" width="1"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>
      <td width="8"><img src="{$images}/pixel.gif" alt="." width="1" height="1"/></td>

      <!-- BODY -->
      <td valign="top" width="468">
        <xsl:call-template name="body"/>
      </td>

      <td valign="top" width="150">
        <xsl:apply-templates select="news"/>
        <xsl:apply-templates select="related-content"/>
      </td>
    </tr>
  </table>


  <!-- FOOTER -->
  <font face="verdana" size="-2">
  copyright &#169; 2002 oscom.org&#160;&#160;&#160;&#160;&#160;&#160;Please contact <a href="mailto:abuse@oscom.org">abuse@oscom.org</a> to address spam or abuse complaints
  </font>
</body>
<span status="200"/>
</html>
</xsl:template>
 
</xsl:stylesheet>
