<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="id"/>
<xsl:param name="authoring"/>

<xsl:variable name="prefix"><xsl:if test="$authoring">/wyona-cms/computerworld/authoring</xsl:if></xsl:variable>

<xsl:template match="span[@id = 'navigation']">
        <xsl:apply-templates select="/wyona/sitetree"/>
</xsl:template>

<xsl:template match="sitetree">
<table border="0" cellpadding="0" cellspacing="0" width="140">
	<tr>
		<td colspan="2"><img border="0" src="/img/layout/trans1x1.gif" width="1" height="10" /></td>
	</tr>
	<tr>
		<td colspan="2"><img border="0" src="/img/layout/aktuell.gif" width="140" height="18" alt="Aktuell" /></td>
	</tr>
<xsl:for-each select="block[@id = 'aktuell']/node">
	<xsl:variable name="url"><xsl:value-of select="@link" /></xsl:variable>	
	<tr>
		<td width="16" height="16" valign="middle" align="right"><a href=""><img border="0" src="/img/layout/arrow-black.gif" width="9" height="7" alt="»" /></a></td>
		<td width="124">
		<xsl:choose>
		<xsl:when test="@id = $id">
			<a href="{$prefix}{$url}" class="txt-s-red"><xsl:value-of select="name/line" /></a>
		</xsl:when>
		<xsl:otherwise>
			<a href="{$prefix}{$url}" class="txt-s-black"><xsl:value-of select="name/line" /></a>
		</xsl:otherwise>
		</xsl:choose>
		</td>
	</tr>
</xsl:for-each>
	<tr>
		<td colspan="2" height="20" valign="middle"><img border="0" src="/img/layout/line-nav140x1.gif" width="140" height="1" /></td>
	</tr>
	<tr>
		<td colspan="2"><img border="0" src="/img/layout/infos.gif" width="140" height="18" alt="Infos" /></td>
	</tr>
<xsl:for-each select="block[@id = 'infos']/node">
	<xsl:variable name="url"><xsl:value-of select="@link" /></xsl:variable>	
	<tr>
		<td width="16" height="16" valign="middle" align="right"><a href=""><img border="0" src="/img/layout/arrow-black.gif" width="9" height="7" alt="»" /></a></td>
		<td width="124">
		<xsl:choose>
		<xsl:when test="@id = $id">
			<a href="{$prefix}{$url}" class="txt-s-red"><xsl:value-of select="name/line" /></a>
		</xsl:when>
		<xsl:otherwise>
			<a href="{$prefix}{$url}" class="txt-s-black"><xsl:value-of select="name/line" /></a>
		</xsl:otherwise>
		</xsl:choose>
		</td>
	</tr>
</xsl:for-each>
	<tr>
		<td colspan="2" height="20" valign="middle"><img border="0" src="/img/layout/line-nav140x1.gif" width="140" height="1" /></td>
	</tr>
	<tr>
		<td colspan="2"><img border="0" src="/img/layout/verlag.gif" width="140" height="18" alt="Verlag" /></td>
	</tr>
<xsl:for-each select="block[@id = 'verlag']/node">
	<xsl:variable name="url"><xsl:value-of select="@link" /></xsl:variable>	
	<tr>
		<td width="16" height="16" valign="middle" align="right"><a href=""><img border="0" src="/img/layout/arrow-black.gif" width="9" height="7" alt="»" /></a></td>
		<td width="124">
		<xsl:choose>
		<xsl:when test="@id = $id">
			<a href="{$prefix}{$url}" class="txt-s-red"><xsl:value-of select="name/line" /></a>
		</xsl:when>
		<xsl:otherwise>
			<a href="{$prefix}{$url}" class="txt-s-black"><xsl:value-of select="name/line" /></a>
		</xsl:otherwise>
		</xsl:choose>
		</td>
	</tr>
</xsl:for-each>
	<tr>
		<td colspan="2" height="20" valign="middle"><img border="0" src="/img/layout/line-nav140x1.gif" width="140" height="1" /></td>
	</tr>
	<tr>
		<td colspan="2"><a href="magazine/"><img border="0" src="/img/layout/magazine.gif" width="140" height="18" alt="Magazine" /></a></td>
	</tr>
	<tr>
		<td colspan="2">

	<!-- call small preview xslt here. -->
        <xsl:apply-templates select="/wyona/small-preview"/>

		</td>
	</tr>
</table>
</xsl:template>
</xsl:stylesheet>

