<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="files"/>

	<xsl:include href="../../../../../../xslt/menu/root.xsl"/>
	
	<xsl:template match="lenya/cmsbody">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:include href="../today.xsl"/>
	<xsl:include href="../navigation.xsl"/>
	<xsl:include href="small-preview.xsl"/>
	
	<xsl:template match="span[@id = 'content']">
		<!-- Process the headlines here... -->
		<xsl:apply-templates select="/lenya/files"/>
	</xsl:template>
	
	<!-- Replace Page title -->
<xsl:template match="head/title">
   <title>IT-<xsl:value-of select="$files"/></title>
</xsl:template>

	
	<xsl:template match="files">
			<!-- MOEGLICHER ORT FUER RECTANGLE BANNER -->
			<table border="0" cellpadding="2" cellspacing="0" width="440">
				<tr>
					<td width="440" height="5" colspan="2">
						<img src="/img/layout/trans1x1.gif" width="1" height="5"/>
					</td>
				</tr>
				<tr bgcolor="#000000">
					<td width="440" height="21" valign="middle">
						<span class="txt-m-white">
							<b>
								<i>IT-<xsl:value-of select="$files"/></i>
							</b>
						</span>
					</td>
				</tr>
				<xsl:for-each select="file">
					<xsl:variable name="url">
						<xsl:value-of select="@href"/>
					</xsl:variable>
					<tr>
						<td valign="middle" height="25">
							<a href="{$url}" target="_blank" class="txt-l-red">
								<img border="0" src="/img/layout/arrow-red-big.gif" width="16" height="16" alt="&#187;"/>
								<b>
									<xsl:value-of select="title"/>
								</b>
							</a>
						</td>
					</tr>
				</xsl:for-each>
				<tr>
					<td height="3" width="440" style="background-image:url(/img/layout/lines/linecontent440x3.gif)"><img border="0" src="/img/layout/trans1x1.gif" width="1" height="1"/></td>
				</tr>
				<tr>
					<td height="600" width="440"><img border="0" src="/img/layout/trans1x1.gif" width="1" height="600"/></td>
				</tr>
			</table>
	</xsl:template>
	<xsl:template match="@*|*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
