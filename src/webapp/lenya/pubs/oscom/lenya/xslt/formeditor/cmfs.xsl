<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="system">
<tr>
  <td>&#160;</td><td>Project Name</td><td><input type="text" name="element./system/system_name[{system_name/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="system_name" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td valign="top">Description</td><td><textarea name="element./system/description[{description/@tagID}]" cols="40" rows="5"><xsl:value-of select="description" /></textarea></td>
</tr>
<xsl:apply-templates select="features/feature"/>
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
<tr>
  <!-- FIXME: Add parent and children, e.g. feature/title feature/description (see XUpdate) -->
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="insert" value="sibling./system/features/feature[{@tagID}]element./system/features/feature"/></td><td colspan="2">Feature</td>
</tr>
</xsl:template>
 
</xsl:stylesheet>  
