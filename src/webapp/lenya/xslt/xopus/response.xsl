<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/">
    <response id="$reqId"
              status="ok"
              type="$reqType">
      <data id="$reqFile"
            type="$fileType">
        <xsl:copy-of select="."/>
      </data>
    </response>          
  </xsl:template>
</xsl:stylesheet>
