<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="span[@id = 'preview']">
		<!-- Insert magazine preview here... -->
		<xsl:apply-templates select="/wyona/preview"/>
	</xsl:template>
	<xsl:template match="preview">
		<table border="0" cellpadding="0" cellspacing="0" width="440">
			<tr>
				<td width="440" height="5" colspan="2">
					<img src="/img/layout/trans1x1.gif" width="1" height="5"/>
				</td>
			</tr>
			<tr bgcolor="#000000">
				<td width="310" height="21" valign="middle">
					<span class="txt-m-white">
						<b>
							<i>jetzt am Kiosk...</i>
						</b>
					</span>
				</td>
				<td width="130" height="21" valign="middle" align="right">
					<span class="txt-s-white">
						<b>
							<xsl:value-of select="date"/> Nr. <xsl:value-of select="edition"/>
						</b>
					</span>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<!-- TITEL -->
				<td width="310" valign="middle" height="21">
					<span class="txt-s-black">
						<b>
							<xsl:value-of select="item[1]/title"/>
						</b>
					</span>
				</td>
				<td width="130" rowspan="8" valign="top" align="right">
					<!-- TITELBILD AKTUELLE AUSGABE BORDER="1"-->
					<br/>
					<a href="index.html?usecase=uploadimage&amp;step=showscreen&amp;documentid=preview.xml&amp;xpath=/preview/edition">
						<img src="/images/wyona/cms/util/reddot.gif" alt="Insert Image" border="0"/>
					</a>
					<br/>
					<img border="1" src="/img/{media/media-reference/@source}" width="128" height="187" alt="{media/media-reference/@alternate-text}"/>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<!-- LEAD -->
				<td width="310" valign="middle">
					<span class="txt-s-black">
						<xsl:apply-templates select="item[1]/p"/>
					</span>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<td width="310" valign="middle" height="3" style="background-image:url(/img/layout/linecontent440x3.gif)">
					<img src="/img/layout/trans1x1.gif" width="1" height="3"/>
				</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<!-- TITEL -->
				<td width="310" valign="middle" height="21">
					<span class="txt-s-black">
						<b>
							<xsl:value-of select="item[2]/title"/>
						</b>
					</span>
				</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<!-- LEAD -->
				<td width="310" valign="middle">
					<span class="txt-s-black">
						<xsl:apply-templates select="item[2]/p"/>
					</span>
				</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td width="310" valign="middle" height="3" style="background-image:url(/img/layout/linecontent440x3.gif)">
					<img src="/img/layout/trans1x1.gif" width="1" height="3"/>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<!-- TITEL -->
				<td width="310" valign="middle" height="21">
					<span class="txt-s-black">
						<b>
							<xsl:value-of select="item[3]/title"/>
						</b>
					</span>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<!-- LEAD -->
				<td width="310" valign="middle">
					<span class="txt-s-black">
						<xsl:apply-templates select="item[3]/p"/>
					</span>
				</td>
			</tr>
			<tr bgcolor="#EFEFE7">
				<td width="440" valign="middle" height="3" colspan="2" style="background-image:url(/img/layout/linecontent440x3.gif)">
					<img src="/img/layout/trans1x1.gif" width="1" height="3"/>
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
