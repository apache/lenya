<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="html-title">
Editor Matrix - OSCOM - Open Source Content Management
</xsl:template>

<xsl:template name="admin-url">
<xsl:param name="prefix"/>
<a class="breadcrumb"><xsl:attribute name="href"><xsl:value-of select="$prefix"/>/editor-matrix/index.html</xsl:attribute>Apache Lenya
</a>
</xsl:template>

<xsl:template name="body">
<font face="verdana">
<h3>TTW WYSIWYG Editors Overview</h3>

<font size="-1">
<p>
To be listed below the editor must be TTW ("through the web" - it works within a browser) and WYSIWYG ("what you see is what you get" - you don't see HTML). Some editors allow you to toggle between "view" and "source" modes - this is a bonus but not a prerequisite for this list.
</p>

<p>
If you want your Editor project being added to the list below, please send an email to
<a href="mailto:paul.browning@bristol.ac.uk">Paul Browning</a>.
For all other inquiries please use OSCOM's <a href="../mailing-lists.html">mailing lists</a>.
</p>

<p>
The "Remarks" are based on supporting Web pages - I haven't (yet) installed and used all of these editors so I can't vouch for this information.
</p>
</font>

 <h4>TTW WYSIWYG Editors</h4>
  <table cellspacing="0" cellpadding="0" width="450">
  <tr>
    <td bgcolor="#000000">&#160;</td>
    <td height="20" bgcolor="#000000"><font size="-2" color="#ffffff"><b>project</b></font></td>
    <td bgcolor="#000000">&#160;</td>
    <td bgcolor="#ffffff">&#160;</td>
    <td bgcolor="#000000">&#160;</td>
    <td bgcolor="#000000"><font size="-2" color="#ffffff"><b>license</b></font></td>
  </tr>
  <xsl:apply-templates select="system[@type='editor']">
    <xsl:sort select="system_name" data-type="text" order="ascending"/>
  </xsl:apply-templates>
  </table>


<!--
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
  <xsl:apply-templates select="system[@type='cms']">
    <xsl:sort select="system_name" data-type="text" order="ascending"/>
  </xsl:apply-templates>
  </table>
-->


 </font>
</xsl:template>

<xsl:template match="system">
  <tr>
   <td>&#160;</td>
   <td height="20"><font size="-1"><a href="{id}.html"><xsl:value-of select="system_name"/></a></font></td>
<!--
   <td height="20"><font size="-1"><a href="{main_url}"><xsl:value-of select="system_name"/></a></font></td>
-->
   <td>&#160;</td>
   <td>&#160;</td>
   <td>&#160;</td>
   <td><font size="-1"><xsl:value-of select="license/license_name"/></font></td>
<!--
   <td><font size="-1"><a><xsl:apply-templates select="license/license_url"/><xsl:value-of select="license/license_name"/></a></font></td>
-->
  </tr>
</xsl:template>

<xsl:template match="license_url">
<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>

<xsl:template match="related-content">
 <table cellpadding="0" cellspacing="0" border="0" width="150">
  <tr>
    <td bgcolor="{$tablecolor}">&#160;</td>
    <td bgcolor="{$tablecolor}">
      <p>
        <font face="verdana" color="white">
        Related Links
        </font>
      </p>
    </td>
  </tr>
  <tr>
    <td>&#160;</td>
<td>
<font face="verdana" size="-2">
<a target="_blank" href="http://www.bris.ac.uk/is/projects/cms/ttw/ttw.html">Other TTW WYSIWYG Editors</a>
<br/><br/>
</font>
</td>
  </tr>
 </table>
</xsl:template>
 
</xsl:stylesheet>  
