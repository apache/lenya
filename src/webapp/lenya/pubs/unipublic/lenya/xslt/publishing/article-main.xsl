<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
<html>
<body>
  <xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="publish">
<!--<xsl:apply-templates/>-->
<!--Referer: <xsl:value-of select="referer"/><br />-->
<p>
<h1>Publish Article</h1>
<form action="publishArticle">
<input type="hidden" name="docid" value="{docid}"/>
<input type="hidden" name="docids" value="{docids}"/>
Do you really want to publish this document <b><xsl:value-of select="docid"/></b>?
<br />
<br />
<input type="submit" value="YES"/>
<!--<input type="submit" name="submit" value="cancel"/>-->
&#160;&#160;&#160;<a href="{referer}">CANCEL</a>
</form>
</p>
</xsl:template>
 
</xsl:stylesheet>  
