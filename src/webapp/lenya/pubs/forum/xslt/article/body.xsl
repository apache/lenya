<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="article">
<table id_xopus="body" xml_xopus="articles/{meta/id}/index.xml" xsl_xopus="Page/Article/Authoring/xopus.xsl" xsd_xopus="article.xsd">
  <tr>
    <td bgcolor="#000000">
      <font color="#ffffff"><xsl:apply-templates select="head/title"/></font>
    </td>
  </tr>
  <tr>
    <td>
      <xsl:apply-templates select="body/p"/>
    </td>
  </tr>
</table>
</xsl:template>
 
</xsl:stylesheet>  
