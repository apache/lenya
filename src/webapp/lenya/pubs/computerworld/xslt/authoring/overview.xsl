<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="../../../../../../stylesheets/cms/menu/root.xsl"/>
	<xsl:template match="@*|*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="lenya/cmsbody/articles">
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
						<xsl:copy>
							<xsl:for-each select="article">
								<xsl:choose>
									<xsl:when test="head/dateline/story.date/@millis">
										<!-- do nothing -->
									</xsl:when>
									<xsl:otherwise>
										<!--copy first the articles which weren't already published-->
									<xsl:variable name="href">
										<xsl:value-of select="@href"/>
									</xsl:variable>
									<tr bgcolor="#EFEFE7">
										<td height="30" width="320" valign="middle">
											<a href="{$href}" class="txt-l-black">
												<b>
													<xsl:value-of select="head/title"/>
												</b>
											</a>
										</td>
										<td height="30" width="120" valign="middle" align="right">
											<span class="txt-s-black">
											</span>
										</td>
									</tr>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
							<xsl:for-each select="article">
								<!--copy and sort by the publish date (millis) the articles which were already published-->
								<xsl:sort select="head/dateline/story.date/@millis" data-type="number" order="descending"/>
								<xsl:if test="head/dateline/story.date/@millis">
									<xsl:variable name="href">
										<xsl:value-of select="@href"/>
									</xsl:variable>
									<tr bgcolor="#EFEFE7">
										<td height="30" width="320" valign="middle">
											<a href="{$href}" class="txt-l-black">
												<b>
													<xsl:value-of select="head/title"/>
												</b>
											</a>
										</td>
										<td height="30" width="120" valign="middle" align="right">
											<span class="txt-s-black">
												<xsl:value-of select="head/dateline/story.date/@norm"/>
											</span>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</xsl:copy>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
