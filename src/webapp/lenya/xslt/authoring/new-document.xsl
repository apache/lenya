<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml"
	>
 
<xsl:output version="1.0" indent="yes"/>

<xsl:template match="/">
<page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
<page:title>Create Document</page:title>
<page:body>
  <xsl:apply-templates/>
</page:body>
</page:page>
</xsl:template>

<xsl:template match="parent-child">
<h1>New Document</h1>

<xsl:apply-templates select="exception"/>

<xsl:if test="not(exception)">
<p>
<form action="{/parent-child/referer}">
<input type="hidden" name="lenya.usecase" value="create"/>
<input type="hidden" name="lenya.step" value="create"/>
<input type="hidden" name="parentid" value="{/parent-child/parentid}"/>
<input type="hidden" name="childname" value="Title"/>
<input type="hidden" name="childtype" value="branch"/>
<input type="hidden" name="doctype" value="{/parent-child/doctype}"/>
<table>
  <tr>
    <td>ID:</td><td><input type="text" name="childid"/></td>
  </tr>
  <tr>
   <td colspan="2"><font size="2">(No whitespaces or slashes allowed since the ID is used in the URL.)</font></td>
  </tr>
  <tr>
    <td colspan="2">&#160;</td>
  </tr>
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
