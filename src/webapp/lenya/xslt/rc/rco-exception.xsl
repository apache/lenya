<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rc="http://www.wyona.org/2002/rc"
  >


<xsl:output method="html"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="rc:exception">
<html>	
<body>
<head>
<link rel="stylesheet" type="text/css" href="/wyona-cms/wyona/default.css" />
</head>
  <xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="rc:file-reserved-checkout-exception">
  <h2>File Reserved Checkout Exception</h2>
  <p>
  Resource has already been checked out:
  </p>
  <table>
  <tr><td>User:</td><td><xsl:value-of select="rc:user"/></td></tr>
  <tr><td>Date:</td><td><xsl:value-of select="rc:date"/></td></tr>
  <tr><td>Filename:</td><td><xsl:value-of select="rc:filename"/></td></tr>
  </table>
</xsl:template>

<xsl:template match="rc:generic-exception">
  <h2>Generic Exception</h2>
  <p>
  Check the log files :-)
  </p>
  <table>
  <tr><td>Filename:</td><td><xsl:value-of select="rc:filename"/></td></tr>
  </table>
</xsl:template>

</xsl:stylesheet>
