<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
<html>
<body>
<head>
<link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
</head>
  <xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="parent-child">
<h1>New Document</h1>

<xsl:apply-templates select="exception"/>

<xsl:if test="not(exception)">
<p>
<form action="create">
<input type="hidden" name="parentid" value="{/parent-child/parentid}"/>
<input type="hidden" name="childname" value="Title"/>
<input type="hidden" name="childtype" value="branch"/>
<input type="hidden" name="doctype" value="{/parent-child/doctype}"/>
<table>
<!-- Could be displayed if wanted
  <tr>
    <td>Parent ID:</td><td><xsl:value-of select="/parent-child/parentid"/></td>
  </tr>
  <tr>
    <td>Document Type:</td><td><xsl:value-of select="/parent-child/doctype"/></td>
  </tr>
-->
  <tr>
    <td>ID:</td><td><input type="text" name="childid"/></td>
  </tr>
  <tr>
   <td colspan="2"><font size="2">(No whitespaces or slashes allowed since the ID is used in the URL.)</font></td>
  </tr>
  <tr>
    <td colspan="2">&#160;</td>
  </tr>
<!-- Usually not used
  <tr>
    <td>name:</td><td><input type="text" name="childname"/></td>
  </tr>
  <tr>
    <td>child type:</td>
    <td>
      <input type="radio" name="childtype" value="branch"><xsl:attribute name="checked"/></input>Branch
      <input type="radio" name="childtype" value="leaf"/>Leaf
    </td>
  </tr>
-->
</table>
<input type="submit" value="Create"/>&#160;<input type="button" onClick="location.href='{referer}';" value="Cancel"/>
</form>
</p>
</xsl:if>
</xsl:template>

<xsl:template match="exception">
<font color="red">EXCEPTION</font><br />
<p>
One of the following errors occured:
<ul>
  <li>The ID is not allowed to have whitespaces</li>
  <li>The ID is already in use</li>
</ul>
</p>
<p>Go <a href="{../referer}">back</a> to page.</p>
</xsl:template>
 
</xsl:stylesheet>  
