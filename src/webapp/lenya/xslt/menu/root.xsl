<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:include href="menu.xsl"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:variable name="context_prefix" select="/lenya/menu/context_prefix"/>

<xsl:template match="lenya">

<html>   
<head>
<title>Apache Lenya - <xsl:value-of select="menu/url-info/publication-id"/> - <xsl:value-of select="menu/url-info/area"/> - <xsl:value-of select="menu/url-info/document-id"/></title>
<script type="text/javascript" src="/lenya/lenya/menu/menu.js" />
<link type="text/css" rel="stylesheet" href="/lenya/lenya/menu/menu.css" />
</head>

<body bgcolor="#ffffff" leftmargin="0" marginwidth="0" topmargin="0" marginheight="0">

  <div style="position:absolute;top:0px;left:0px;z-index:2">
      <xsl:apply-templates select="menu"/>
  </div>

  <div style="position:absolute;top:60px;left:0px;z-index:1">
<!--<iframe id="page" name="page" src="{$context_prefix}{live_uri}"> -->
     <xsl:apply-templates select="cmsbody"/>
<!--</iframe> -->
  </div>
<script type="text/javascript">
 initialize(); 
</script>
</body>
</html>
</xsl:template>
 
</xsl:stylesheet>  
