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
<!--
	<script language="javascript" src="/levi/xopus/xopus.js">;</script>
-->
	<script language="javascript">
		//xopus_globs.LENYA_CMS_URL="<xsl:value-of select="$context_prefix" />";
 
                // Xopus 2.0.0.1
		//xopus_consts.LENYA_CMS_URL="<xsl:value-of select="$context_prefix" />/xopus/XopusInterface";

                // Xopus 2.0.0.8
		xopus_globs.WYONA_CMS_URL="<xsl:value-of select="$context_prefix" />/xopus/XopusInterface";
		xopus_globs.WYONA_CMS_EXIT_URL="/lenya/oscom/authoring/matrix/cocoon.html";
	</script>
</xsl:template>



<xsl:template name="xopus_body">
</xsl:template>





</xsl:stylesheet>

