<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="simple:simple-document" xmlns:simple="http://apache.org/cocoon/lenya/doctypes/simple-document/1.0" xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" xmlns:dc="http://purl.org/dc/elements/1.1/">
<node name="Dublin Core">
</node>
<node name="Title">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/simple:simple-document/lenya:meta/dc:title[@tagID='{lenya:meta/dc:title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="lenya:meta/dc:title" /></xsl:attribute></input></content>
</node>
<node name="Description">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/simple:simple-document/lenya:meta/dc:description[@tagID='{lenya:meta/dc:description/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="lenya:meta/dc:description" /></xsl:attribute></input></content>
</node>
</xsl:template>
 
</xsl:stylesheet>
