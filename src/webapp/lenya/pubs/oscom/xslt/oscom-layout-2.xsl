<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/" xmlns:oscom="http://www.oscom.org/2002/oscom">

<xsl:variable name="oscombarcolor">#ffa500</xsl:variable>
<!--
<xsl:variable name="navbarcolor">#3366cc</xsl:variable>
-->
<xsl:variable name="navbarcolor">#ffc36b</xsl:variable>
<xsl:variable name="imagesPrefix">go/oscom-proposals_files</xsl:variable>
<!--
<xsl:variable name="imagesPrefix">oscom-proposals_files</xsl:variable>
-->
<!--
<xsl:variable name="searchURL">/search/go</xsl:variable>
-->
<xsl:variable name="searchURL">/lenya/oscom/search-live/lucene</xsl:variable>

<xsl:template match="oscom">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org" />
<title>
<xsl:call-template name="html-title"/> - OSCOM - Open Source Content Management
</title>
<meta http-equiv="Content-Type"
content="text/html; charset=iso-latin-1" />
<meta http-equiv="Content-Language" content="EN" />
<meta name="description" content="@DESCRIPTION@" />
<style type="text/css">
     <xsl:comment>
            BODY {
        font-family: Verdana, Arial, Helvetica, sans-serif;
      }
      H1 {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #333333;
        background-color: transparent;
        font-size: 14px;
      }
      H2 {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 13px;
      }
      H3 {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 12px;
      }
      P, LI, PRE, BLOCKQUOTE,DT,DD {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px;
      }
      TD.topnavi {
        padding: 0px;
        padding-left  : 15px;
        font-size: 11px;
      }
      TD.rightbar {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        border-left: 1px #a0a0a0 dotted;
        padding: 7px;
        padding-bottom: 10px;
        font-size: 11px;
      }
      TD.sitelogoarea {
        padding-left  : 10px;
      }
      TD.content {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px;
        padding: 7px;
        padding-bottom: 10px;
        padding-left  : 15px;
      }
      TD.footer {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        border-top: 1px #FFC36B solid;
        padding: 7px;
        padding-bottom: 10px;
        padding-left  : 15px;
        font-size: 10px;
        color: #a0a0a0;
        background-color: transparent;
      }
      .rightbox {
        border-top: 1px #a0a0a0 dotted;
      }
      .navigationwhite {
        color: #FFFFFF;
        background-color: transparent;
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 12px; 
        font-style: normal; 
        font-weight: normal;
        text-decoration: none;
      }
      .navigationwhite:hover {
        text-decoration: underline;
      }
      .naviselected {
         font-weight: bold;
      }
      .nnbr {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px; 
        background-color: transparent;
        text-decoration: none;
      }
      .nnbr:hover {
        background-color: transparent;
        text-decoration: underline;
      }
      .nnbs {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 11px; 
        background-color: transparent;
        text-decoration: none;
        font-weight: bolder;
      }
      .nnbe {
        margin-bottom: 7px;
      }
      A {
        color: #663300;
        background-color: transparent;
      }
      .breadcrumb {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #a0a0a0;
        background-color: transparent;
        text-decoration: none;
        font-size: 9px;
      }
      .newsareaheader {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #333333;
        background-color: transparent;
        font-weight: bold;
      }
      .news {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 10px;
        text-decoration: none;
      }
      .news:hover {
        text-decoration: underline;
      }
      INPUT.search {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #663300;
        background-color: #ffffff;
        border: 1px solid #663300;
      }
      INPUT.searchsubmit {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #7e6a4b;
        background-color: #FFC36B;
        border: 1px solid #663300;
      }
      INPUT.searchsubmit:hover {
        color: #ffffff;
        background-color: #FFA500;
        border: 1px solid #ffffff;
      }
    </xsl:comment>
    
</style>
</head>
<body bgcolor="#ffffff" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<!--top row: main navigation-->
<table cellspacing="0" cellpadding="0" border="0" width="770">
<tbody>
<tr bgcolor="#cc0066">
<td width="100%" height="54">
<table cellspacing="0" cellpadding="0" border="0" width="100%" bgcolor="{$oscombarcolor}">
<tbody>
<tr>
<td valign="bottom" class="sitelogoarea">
<a href="http://www.oscom.org/">
  <img src="{$imagesPrefix}/oscom-logo.png" width="329" height="54" alt="OSCOM - Open Source Content Management" border="0" />
</a>
</td>
<td valign="middle">
  <img src="{$imagesPrefix}/spacer.gif" width="100" height="1" border="0" />
</td>
<td valign="middle">
<!-- We can put topical banners here -->
<form method="get" action="{$searchURL}">
<input type="hidden" name="publication-id" value="all" />
<input type="text" name="queryString" class="search" size="15"/>&#160;<input type="submit" name="find" value="Go" class="searchsubmit" />
</form>
<!--
<img src="{$imagesPrefix}/spacer.gif" width="1" height="1" border="0" />
-->
</td>
</tr>
</tbody>
</table>
</td>
</tr>

<tr>
<td valign="top" width="100%" bgcolor="{$navbarcolor}" class="topnavi">

<xsl:apply-templates select="oscom_navigation" mode="top"/>
<!--
<xsl:apply-templates select="oscom_navigation"/>
-->

<!--
<table border="0" cellpadding="0" cellspacing="0">
<tbody>
<tr>

<td height="21" class="topnavigation" align="left">
  <a href="/index.html" class="navigationwhite">Home</a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>

<td height="21" class="topnavigation" align="left">
  <a href="/Conferences/" class="navigationwhite">Conferences</a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>


<td height="21" class="topnavigation" align="left">
  <a href="/lenya/oscom/matrix/index.html" class="navigationwhite">
    <span class="naviselected">CMS Matrix</span>
  </a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>



<td height="21" class="topnavigation" align="left">
  <a href="/Projects/" class="navigationwhite">Projects</a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>



<td height="21" class="topnavigation" align="left">
  <a href="/Mailing%20lists/" class="navigationwhite">Mailing lists</a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>


<td height="21" class="topnavigation" align="left">
  <a href="http://blog.oscom.org/" class="navigationwhite">Blog</a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>

<td height="21" class="topnavigation" align="left">
  <a href="/Organization/" class="navigationwhite">Organization</a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>

</tr>
</tbody>
</table>
-->


</td>
</tr>

<tr>
<td valign="top" width="100%" bgcolor="#ffa500"><img
src="{$imagesPrefix}/spacer.gif" width="760" height="1"
border="0" /></td>
</tr>
</tbody>
</table>



<table border="0" cellpadding="0" cellspacing="0" width="770">
<tbody>
<tr><!--middle column: main content-->
<td valign="top" width="540">
<table width="540" cellpadding="1" cellspacing="0" border="0">
<tbody>
<tr>
<td valign="top" align="left" colspan="2" class="content">
<!--CONTENT-->
<!-- @CONTENT@ -->
<xsl:call-template name="body"/>
</td>
</tr>
</tbody>
</table>
</td>
<!--right column: navigation-->
<td valign="top"><img src="{$imagesPrefix}/spacer.gif"
width="24" height="16" border="0" /></td>
<td valign="top" align="left" width="200" class="rightbar">
<!-- @NAVIGATION@ -->
<!--
<xsl:apply-templates select="oscom_navigation"/>
-->
<xsl:apply-templates select="related-content"/>

<p><br />
<br />
</p>

<p align="center">
  <a href="/Conferences/Cambridge/">
<!--
@ADD_BANNER@
-->
<img src="{$imagesPrefix}/oscom3.png" border="0" alt="OSCOM3, May 2003, Cambridge/Boston, MA" title="OSCOM3, May 2003, Cambridge/Boston, MA" width="152" height="35"/>
  </a>
</p>

<p>&#160;</p>
</td>
</tr>
</tbody>
</table>

<!--bottom row: footer-->
<table width="770" border="0" cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td class="footer" valign="top">&#169;2002-2003 OSCOM.
<!-- @UPDATING_DATE@ --><br />
 <!-- <a href="http://alpha.oscom.org/" class="breadcrumb">@BREADCRUMB_PATH@</a> --> <!--
<a href="http://alpha.oscom.org/" class="breadcrumb">Home</a>
 &gt; <a href="http://alpha.oscom.org/Conferences/" class="breadcrumb">Conferences</a>
 &gt; <a href="http://alpha.oscom.org/Conferences/Cambridge/" class="breadcrumb">Cambridge</a>
 &gt; <a href="http://alpha.oscom.org/Conferences/Cambridge/Proposals/" class="breadcrumb">Proposals</a>
-->
 </td>
<td align="right" valign="top" class="footer">
<!-- Admin interface link -->
<xsl:call-template name="admin-url">
<xsl:with-param name="prefix" select="'http://oscom.wyona.org:18080/lenya/oscom/authoring'"/>
</xsl:call-template>
<!-- /Admin interface link --></td>
</tr>
</tbody>
</table>
</body>
</html>
</xsl:template>




<xsl:template match="oscom_navigation" mode="top">
<table border="0" cellpadding="0" cellspacing="0">
<tbody>
<tr>
<xsl:apply-templates mode="top"/>
</tr>
</tbody>
</table>
</xsl:template>



<xsl:template match="leaf" mode="top">
<td height="21" class="topnavigation" align="left">
  <a href="{@href}" class="navigationwhite">
    <xsl:choose>
      <xsl:when test="@selected">
        <span class="naviselected"><xsl:value-of select="@name"/></span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@name"/>
      </xsl:otherwise>
    </xsl:choose>
  </a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>
</xsl:template>



<xsl:template match="branch" mode="top">
<td height="21" class="topnavigation" align="left">
  <a href="{@href}" class="navigationwhite">
    <xsl:choose>
      <xsl:when test="@selected">
        <span class="naviselected"><xsl:value-of select="@name"/></span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@name"/>
      </xsl:otherwise>
    </xsl:choose>
  </a>
</td>
<td height="21" width="19" align="left">
  <img src="{$imagesPrefix}/spacer.gif" width="19" height="14" />
</td>
</xsl:template>






<!--
<xsl:template name="body">
BODY
</xsl:template>

<xsl:template name="html-title">
OSCOM - Open Source Content Management
</xsl:template>

<xsl:template name="admin-url">
<xsl:param name="prefix"/>
<a class="breadcrumb"><xsl:attribute name="href"><xsl:value-of select="$prefix"/>/matrix/index.html</xsl:attribute>Apache Lenya</a>
</xsl:template>
-->




</xsl:stylesheet>
