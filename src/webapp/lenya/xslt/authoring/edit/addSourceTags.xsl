<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="source"/>

<xsl:template match="/">
  <open>
  <source:write xmlns:source="http://apache.org/cocoon/source/1.0">
    <source:source><xsl:value-of select="$source"/></source:source>
    <source:fragment>
      <xsl:copy-of select="."/>
    </source:fragment>
  </source:write>
    <content>
      <xsl:copy-of select="."/>
    </content>
  </open>
</xsl:template>

</xsl:stylesheet>
