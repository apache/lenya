<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="requestURI" select="'No requestURI'"/>
<xsl:param name="contextPath" select="'No contextPath'"/>

<xsl:template match="/">
<html>
<body>
404
<br/><xsl:value-of select="$requestURI"/>
<br/><xsl:value-of select="$contextPath"/>
</body>
</html>
</xsl:template>
 
</xsl:stylesheet>  
