<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:t="http://www.q42.nl/t">

<xsl:output method="html" version="1.0" indent="yes"/>

<xsl:variable name="xopus_path">/xopus2</xsl:variable>

<xsl:template name="xopus_html_attribute">
</xsl:template>


<xsl:template name="xopus_top">
</xsl:template>



<xsl:template name="xopus_head">
<!--
	<script language="javascript" src="{$xopus_path}/xopus/xopus.js">;</script>
-->
<!--
	<script language="javascript" src="/software/xopus/xopus.js">;</script>
-->
	<script language="javascript" src="/lenya/xopus/xopus/xopus.js">;</script>
	<script language="javascript">
		//xopus_globs.WYONA_CMS_URL="<xsl:value-of select="$context_prefix" />";
		xopus_consts.WYONA_CMS_URL="<xsl:value-of select="$context_prefix" />/xopus/XopusInterface";
	</script>
</xsl:template>



<xsl:template name="xopus_body">
</xsl:template>





</xsl:stylesheet>

