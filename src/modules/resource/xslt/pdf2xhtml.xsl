<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:pdf="http://apache.org/lenya/pdf/1.0"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:template match="/pdf:document">
    <html>
      <body>
        <p>
          <xsl:value-of select="."/>
        </p>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>