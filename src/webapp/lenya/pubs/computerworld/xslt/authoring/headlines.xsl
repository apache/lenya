<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="id"/>

<xsl:template match="span[@id = 'headlines']" >
    <!-- Process the headlines here... -->
    <xsl:apply-templates select="/wyona/articles" />
</xsl:template>


	<xsl:template match="articles">
		<xsl:for-each select="article">
			<xsl:variable name="href"><xsl:choose><xsl:when test="$id = '/news/'">../</xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:value-of select="@href"/></xsl:variable>
			<xsl:variable name="color"><xsl:choose><xsl:when test="position() mod 2 = 1">#EFEFE7</xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:variable>
			<tr bgcolor="{$color}">
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
						<xsl:value-of select="head/dateline/body.date/@norm"/>
					</span>
				</td>
			</tr>
			<tr bgcolor="{$color}">
				<td width="440" valign="middle" colspan="2">
					<!-- EVTL. BILD  WIDTH="50" HEIGHT="50" ALIGN="LEFT" -->
<xsl:apply-templates select="head/media[1]">
  <xsl:with-param name="href"><xsl:value-of select="$href"/></xsl:with-param>
</xsl:apply-templates>
					<!-- NEWS LAUFTEXT -->
					<span class="txt-s-black">
						<xsl:value-of select="head/abstract"/><br />
						<!-- WEITER BUTTON --><a href="{$href}" class="txt-link-red">
							<img border="0" src="/img/layout/arrow-red.gif" width="9" height="7" alt=""/>weiter</a>
					</span>
				</td>
			</tr>
			<tr bgcolor="{$color}" width="440" height="3">
				<td height="3" colspan="2">
					<img src="/img/layout/linecontent440x3.gif" width="440" height="3"/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>

<xsl:template match="media">
  <xsl:param name="href"/>
  <a href="{$href}">
  <img border="0" src="/img/news/{media-reference/@source}" width="50" height="50" align="left"/>
  </a>
</xsl:template>

</xsl:stylesheet>
