<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="span[@id = 'headlines']">
		<!-- Process the headlines here... -->
		<xsl:apply-templates select="/wyona/articles"/>
	</xsl:template>
	<xsl:template match="articles">
		<xsl:for-each select="article">
			<xsl:variable name="href"><xsl:value-of select="@href"/></xsl:variable>
			<tr bgcolor="#EFEFE7">
				<td height="30" width="320" valign="middle">
					<!-- NEWS TITEL -->
					<a href="{$href}" class="txt-l-black">
						<b>
							<xsl:value-of select="head/title"/>
						</b>
					</a>
				</td>
				<td height="30" width="120" valign="middle" align="right">
					<!-- DATUM -->
					<span class="txt-s-black">
						<xsl:value-of select="head/dateline/story.date/@norm"/>
					</span>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<td width="440" valign="middle" colspan="2">
					<!-- EVTL. BILD  WIDTH="50" HEIGHT="50" ALIGN="LEFT" -->
					<a href="">
						<img border="0" src="/img/categories/{head/media/media-reference/@source}" width="50" height="50" align="left"/>
					</a>
					<!-- NEWS LAUFTEXT -->
					<span class="txt-s-black">
						<xsl:value-of select="head/abstract"/><br />
						<!-- WEITER BUTTON --> <a href="{$href}" class="txt-link-red">
							<img border="0" src="/img/layout/arrow-red.gif" width="9" height="7" alt=""/>weiter</a>
					</span>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7" width="440" height="3">
				<td height="3" colspan="2">
					<img src="/img/layout/linecontent440x3.gif" width="440" height="3"/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
