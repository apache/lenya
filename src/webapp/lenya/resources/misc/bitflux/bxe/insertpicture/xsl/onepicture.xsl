<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="iso-8859-1" indent="no" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
<!-- sprachendefinition -->
<xsl:template match="/iba">
	<table>
		<xsl:for-each select="/iba/objects//Mediaobject">
			<tr>
            <td>
            	<input type="radio"/>
            </td>
			<td>
				<img src="/files/images/{Imageobject/ID}.{Imageobject/imagedata/fileref}"/>
			</td>
			<td>
				<xsl:value-of select="Imageobject/imagedata/width"/>x<xsl:value-of select="Imageobject/imagedata/height"/><br/>
				<xsl:value-of select="round(Imageobject/imagedata/filesize div 1024)"/>kB
			</td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>
<xsl:template match="br">
<xsl:copy-of select="."/>
</xsl:template>
</xsl:stylesheet>
