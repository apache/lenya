<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : redirect.xsl
    Created on : 20. Mai 2003, 12:11
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    >

<xsl:param name="url"/>

<xsl:template match="/">
  <html>
    <head>
      <meta http-equiv="Refresh" content="0;url={$url}"/>
    </head>
    <body>
    </body>
  </html>
</xsl:template>

</xsl:stylesheet> 
