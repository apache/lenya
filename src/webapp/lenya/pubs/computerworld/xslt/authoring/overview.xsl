<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="../../../../../../stylesheets/cms/Page/root-dhtml.xsl"/>
	<xsl:template match="@*|*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="wyona/cmsbody/articles">
		<html>
			<head>
				<title/>
			</head>
			<body>
				<table>
					<tbody>
						<tr bgcolor="#EFEFE7">
							<td>Article</td>
							<td>Published on</td>
						</tr>
						<xsl:for-each select="article">
						<xsl:sort select="head/dateline/story.date/@millis" order="descending"/>
							<xsl:variable name="href">
								<xsl:value-of select="@href"/>
							</xsl:variable>
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
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
