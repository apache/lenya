<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet is used to create the ssi-include for the homepage
	of the University of Zurich that displays the first headline of Unipublic
	-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="ISO-8859-1" indent="yes"/>
	
	<xsl:template match="/">
		<!-- Select the first article -->
		<xsl:apply-templates select="Articles/Article[position()=1]"/>
	</xsl:template>
	
	<xsl:template match="Article">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<a href="http://www.unipublic.unizh.ch/">
						<img src="http://www.unipublic.unizh.ch/{@href}/{body.head/media/media-reference/@source}" alt="teaser-image" height="60" width="80" border="0"/>
					</a>
				</td>
				<td>&#160;</td>
				<td>
					<a href="http://www.unipublic.unizh.ch/">
						<span class="tsr-title">
							<xsl:value-of select="body.head/hedline/hl1"/>
						</span>
					</a>
				</td>
				<td>&#160;</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
