<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rc="http://www.wyona.org/2002/rc">


<xsl:output method="html"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="sitemap-path">
<!--
<p>
  DEBUG: Sitemap Path: <xsl:apply-templates/>
</p>
-->
</xsl:template>

<xsl:template match="rc:revisions/XPSRevisionControl">
<html>
<head>
<link rel="stylesheet" type="text/css" href="/lenya/wyona/default.css" />
</head>	
<body>
	
	<h2>Rollback to an earlier version</h2>
	<table border="0" cellpadding="2" cellspacing="1">
		<tr bgcolor="#aaaaaa">
		   <th></th>
		   <th></th>
		   <th>Checked in at</th>
		   <th>Checked in by</th>
		</tr>
		
		<xsl:for-each select="CheckIn">
			
			<xsl:choose>
				
				<xsl:when test="position()=1">
					<tr bgcolor="#bbbbbb">
						<td>Current version</td>
						<td>&#160;</td>
						<xsl:apply-templates select="Time"/>
						<xsl:apply-templates select="Identity"/>
					</tr>
				</xsl:when>

				<!-- Note, important: The timestamp we're inserting into the anchor
				     in each row is actually the one from the *previous* version, thus the
					 position()-1 calculation. This is because in order to roll back
					 to a given version, we need to reactivate the backup file which was
					 written *before* that version was checked in.
				 --> 

				<xsl:when test="position()>1">
					<xsl:variable name="timeIndex" select="position() - 1"/>
					<tr bgcolor="#bbbbbb">
						<td>
							<xsl:element name="a">
<!--
							<xsl:attribute name="href">rollback.xml?action=rollback&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/>&amp;rollback=<xsl:value-of select="../@id"/>&amp;repository=<xsl:value-of select="../@repository"/></xsl:attribute>Rollback to this version</xsl:element>
-->
							<xsl:attribute name="href"><xsl:value-of select="../@uriprefix"/>/rollback/<xsl:value-of select="../@id"/>?action=rollback&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/></xsl:attribute>Rollback to this version</xsl:element>
						</td>
						<td>
							<xsl:element name="a">
<!--
							<xsl:attribute name="href">rollback.xml?action=view&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/>&amp;rollback=<xsl:value-of select="../@id"/>&amp;repository=<xsl:value-of select="../@repository"/></xsl:attribute><xsl:attribute name="target">_blank</xsl:attribute>View</xsl:element>
-->
							<xsl:attribute name="href"><xsl:value-of select="../@uriprefix"/>/view/<xsl:value-of select="../@id"/>?action=view&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/></xsl:attribute><xsl:attribute name="target">_blank</xsl:attribute>View</xsl:element>
						</td>
						<xsl:apply-templates select="Time"/>
						<xsl:apply-templates select="Identity"/>
					</tr>
				</xsl:when>
			</xsl:choose>
		
		</xsl:for-each>
		
		<!--<xsl:apply-templates select="CheckIn"/>-->
	</table>
	</body>
</html>
</xsl:template>


<xsl:template match="CheckIn">
	<tr bgcolor="#bbbbbb">
		<td><xsl:element name="a">
			<xsl:attribute name="href">rollback.xml?rollback=<xsl:value-of select="../@id"/>&amp;rollbackTime=<xsl:value-of select="Time"/>&amp;repository=<xsl:value-of select="../@repository"/></xsl:attribute>Rollback to this version</xsl:element></td>
		<xsl:apply-templates/>
	</tr>
</xsl:template>

<xsl:template match="Time">
	<td align="right"><xsl:value-of select="@humanreadable"/></td>
</xsl:template>

<xsl:template match="Identity">
	<td><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="CheckOut"></xsl:template>


</xsl:stylesheet>








