<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="projectid"/>

<xsl:template match="/">
<html>
<body>
Edit Document <xsl:value-of select="$projectid"/>
<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close">
<table>
<tr>
  <td>Project Name</td><td><input type="text" name="system_name" size="40"><xsl:attribute name="value"><xsl:value-of select="/system/system_name" /></xsl:attribute></input></td>
</tr>
<tr>
  <td valign="top">Description</td><td><textarea name="description" cols="40" rows="5"><xsl:value-of select="/system/description" /></textarea></td>
</tr>
<!--
<xsl:apply-templates select="/system"/>
-->
<tr>
  <td colspan="2" align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
</table>
</form>
</body>
</html>
</xsl:template>
 
</xsl:stylesheet>  
