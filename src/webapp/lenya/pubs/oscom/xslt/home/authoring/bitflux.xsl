<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">

<xsl:output encoding="iso-8859-1"/>

<xsl:template match="/">
    <head>
    </head>
    <body>
<table width="100%">
<tr>
<td bgcolor="orange">
<font color="#ffffff" face="verdana">
<font size="+2"><b>OSCOM</b></font><br />
<font size="0"><b>OPEN SOURCE CONTENT MANAGEMENT</b></font>
</font>
</td>
</tr>
</table>

<xsl:apply-templates select="home/about"/>
<xsl:apply-templates select="home/features/feature"/>


    </body>
  </xsl:template>

  <xsl:template match="about">
    <About contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </About>
  </xsl:template>

  <xsl:template match="feature">
    <Feature contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </Feature>
  </xsl:template>

  <xsl:template match="title">
    <Title contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </Title>
  </xsl:template>

<!--
  <xsl:template match="p">
    <Paragraph contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </Paragraph>
  </xsl:template>
-->


  <xsl:template match="*">
    <xsl:copy>
      <xsl:for-each select="@*">
        <xsl:copy/>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
