<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:variable name="mbarcolor">#000000</xsl:variable>

<xsl:template name="html-title">
CMS Matrix
</xsl:template>

<xsl:template name="admin-url">
<xsl:param name="prefix"/>
<a class="breadcrumb"><xsl:attribute name="href"><xsl:value-of select="$prefix"/>/matrix/index.html</xsl:attribute>Edit</a> - Apache Lenya
</xsl:template>

<xsl:template name="body">
<font face="verdana">
<h3>Content Management Frameworks/Systems Overview</h3>

<font size="-1">
<p>
The data of the CMF/S projects listed below are NOT maintained
by OSCOM itself, but rather by people from each project.
Please contact them directly in case the data might be outdated.
</p>

<p>
If you want your CMS/F project being added to the list below, please send an email to
<a href="mailto:michael.wechner@oscom.org?subject=OSCOM CMS Matrix">Michael Wechner</a>.
For all other inquiries please use OSCOM's <a href="/get-involved/mailing-lists/">mailing lists</a>.
</p>
</font>

 <h4>Content Management Frameworks</h4>
  <table cellspacing="0" cellpadding="0" width="450">
  <tr>
    <td bgcolor="{$mbarcolor}">&#160;</td>
    <td height="20" bgcolor="{$mbarcolor}"><font size="-2" color="#ffffff"><b>project</b></font></td>
    <td bgcolor="{$mbarcolor}">&#160;</td>
    <td bgcolor="#ffffff">&#160;</td>
    <td bgcolor="{$mbarcolor}">&#160;</td>
    <td bgcolor="{$mbarcolor}"><font size="-2" color="#ffffff"><b>license</b></font></td>
  </tr>
  <xsl:apply-templates select="system[@type='framework']">
    <xsl:sort select="system_name" data-type="text" order="ascending"/>
  </xsl:apply-templates>
  </table>


 <h4>Content Management Systems</h4>
  <table cellspacing="0" cellpadding="0" width="450">
  <tr>
    <td bgcolor="{$mbarcolor}">&#160;</td>
    <td height="20" bgcolor="{$mbarcolor}"><font size="-2" color="#ffffff"><b>project</b></font></td>
    <td bgcolor="{$mbarcolor}">&#160;</td>
    <td bgcolor="#ffffff">&#160;</td>
    <td bgcolor="{$mbarcolor}">&#160;</td>
    <td bgcolor="{$mbarcolor}"><font size="-2" color="#ffffff"><b>license</b></font></td>
  </tr>
  <xsl:apply-templates select="system[@type='cms']">
    <xsl:sort select="system_name" data-type="text" order="ascending"/>
  </xsl:apply-templates>
  </table>
 </font>
</xsl:template>

<xsl:template match="system">
  <tr>
   <td>&#160;</td>
   <td height="20"><font size="-1"><a href="{id}.html"><xsl:value-of select="system_name"/></a></font></td>
   <td>&#160;</td>
   <td>&#160;</td>
   <td>&#160;</td>
   <td><font size="-1"><xsl:value-of select="license/license_name"/></font></td>
  </tr>
</xsl:template>

<xsl:template match="license_url">
<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>

<xsl:template match="related-content">
 <table cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td>&#160;</td>
<td>
<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.cms-list.org">cms-list</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.cmsinfo.org">cmsinfo.org</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.cmswatch.com/ContentManagement/Products/">CMS Watch</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.contentmanager.eu.com/links/a4.htm">contentmanagement.eu.com</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.contentmanager.de/itguide/produktvergleich_cms_opensource.html">contentmanager.de</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.content-wire.com">Content-Wire</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.steptwo.com.au/cm/vendors/list/index.html">Step Two</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.la-grange.net/cms">La-Grange.Net</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.cmsreview.com">CMS Review</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.opensourcecms.com">OpensourceCMS</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.sydney.wilderness.org.au/docs/node.php?id=1">Wilderness Society</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.boomtchak.net/rubrique.php3?id_rubrique=48">Boomtchak CMS Outils</a>
</div>

<div class="nnbe" style="padding-left: 10px;">
<a class="nnbr" target="_blank" href="http://www.hartman-communicatie.nl/extra/tools.htm">Hartman Communicatie</a>
</div>

</td>
  </tr>
 </table>
</xsl:template>
 
</xsl:stylesheet>  
