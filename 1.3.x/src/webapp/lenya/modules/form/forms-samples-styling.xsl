<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!--+ Include styling stylesheets, one for the widgets, the other one for the
      | page. As 'forms-advanced-field-styling.xsl' is a specialization of
      | 'forms-field-styling.xsl' the latter one is imported there. If you don't
      | want advanced styling of widgets, change it here!
      | See xsl:include as composition and xsl:import as extension/inheritance.
      +-->
  <xsl:include href="forms-page-styling.xsl"/>
  <xsl:include href="forms-advanced-field-styling.xsl"/>

  <xsl:template match="head">
    <head>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="forms-page"/>
      <xsl:apply-templates select="." mode="forms-field"/>
    </head>
  </xsl:template>

  <xsl:template match="body">
    <body>
      <!--+ !!! If template with mode 'forms-page' adds text or elements
          |        template with mode 'forms-field' can no longer add attributes!!!
          +-->
      <xsl:apply-templates select="." mode="forms-page"/>
      <xsl:apply-templates select="." mode="forms-field"/>
      <xsl:apply-templates/>
    </body>
  </xsl:template>

</xsl:stylesheet>
