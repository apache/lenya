<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="uri"/>

<xsl:template match="/">
<html>
<head>
<meta http-equiv="Refresh" content="0; URL={$uri}"/>
</head>
<body>
<!--
Redirect to: <a href="{$uri}"><xsl:value-of select="$uri"/></a>
-->
</body>
</html>
</xsl:template>

</xsl:stylesheet>
