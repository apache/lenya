<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xlink="http://www.w3.org/2002/XLink"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.0"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    >

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory"/>
</xsl:template>                                                                                                                             
<xsl:template match="dir:directory" >
<lenya:publications>
  <xsl:for-each select="dir:directory">
    <lenya:publication pid="{@name}">
      <lenya:publication xlink:href="{@name}/publication.xml" xlink:show="embed"/>
    </lenya:publication>
  </xsl:for-each>
</lenya:publications>
</xsl:template>

</xsl:stylesheet>
