<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="docid"/>

<xsl:template match="/">
<html>
<body>
<p>
Edit Document <b><xsl:value-of select="$docid"/></b>
</p>

<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close">
<table border="1">
<tr>
  <td>&#160;</td><td>Project Name</td><td><input type="text" name="element./system/system_name[{/system/system_name/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="/system/system_name" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td valign="top">Description</td><td><textarea name="element./system/description[{/system/description/@tagID}]" cols="40" rows="5"><xsl:value-of select="/system/description" /></textarea></td>
</tr>
<xsl:apply-templates select="/system/features/feature"/>
<tr>
  <td colspan="2" align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
</table>
</form>
</body>
</html>
</xsl:template>


<xsl:template match="feature">
<tr>
  <td><input type="image" src="/lenya/lenya/images/delete.gif" name="delete" value="element./system/features/feature[{@tagID}]"/></td><td colspan="2">Feature</td>
</tr>
<tr>
  <td>&#160;</td><td>Feature Title</td><td><input type="text" name="element./system/features/feature/title[{title/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td valign="top">Feature Description</td><td><textarea name="element./system/features/feature/description[{description/@tagID}]" cols="40" rows="3"><xsl:value-of select="description" /></textarea></td>
</tr>
</xsl:template>
 
</xsl:stylesheet>  
