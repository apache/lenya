<?xml version="1.0"?>

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsp="http://apache.org/xsp"
  xmlns:xsp-hello="http://www.wyona.org/xsp/hello/1.0"
  version="1.0"
>

<xsl:template match="xsp-hello:world">
  <xsp:logic>
    Date xsp_hello_server_time=new Date();
  </xsp:logic>

  <hello><xsp:expr>xsp_hello_server_time</xsp:expr></hello>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
 <xsl:copy>
  <xsl:apply-templates select="@*|node()"/>
 </xsl:copy>
</xsl:template>

</xsl:stylesheet>
