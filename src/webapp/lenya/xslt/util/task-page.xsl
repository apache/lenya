<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:session="http://www.lenya.org/2002/session"
    >
 
  <xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:param name="title"/>
  <xsl:param name="action"/>  <!-- action URI after http://www.host.com/context/publication-id/ -->
  <xsl:param name="message"/>

  <xsl:variable name="separator" select="','"/>

  <xsl:variable name="context-prefix">
    <xsl:text/>
    <xsl:value-of select="/session:session/session:context"/>
    <xsl:text/>
  </xsl:variable>
  
  <xsl:variable name="publication-id">
    <xsl:text/>
    <xsl:value-of select="/session:session/session:publication-id"/>
    <xsl:text/>
  </xsl:variable>
  
  <xsl:variable name="uri-prefix">
    <xsl:text/>
    <xsl:value-of select="concat($context-prefix, '/', $publication-id)"/>
    <xsl:text/>
  </xsl:variable>

<!-- ============================================================= -->
<!-- root template -->
<!-- ============================================================= -->
<xsl:template match="/">
  <page>
    <title><xsl:value-of select="$title"/></title>
    <context><xsl:value-of select="$context-prefix"/></context>
    <publication-id><xsl:value-of select="$publication-id"/></publication-id>
    <body>
      <xsl:call-template name="body"/>
    </body>
  </page>
</xsl:template>

  <!-- ============================================================= -->
  <!-- create hidden inputs for all request parameters -->
  <!-- ============================================================= -->
  <xsl:template name="parameters-as-inputs">
    <xsl:for-each select="/session:session/session:parameters/session:parameter">
      <input type="hidden" name="{@name}" value="{@value}"/>
    </xsl:for-each>
  </xsl:template>
  
<!-- ============================================================= -->
<!-- html body template -->
<!-- ============================================================= -->
<xsl:template name="body">

  <form action="{$uri-prefix}/{$action}">

    <!-- forward arbitrary request parameters set within the menubar -->
    <xsl:call-template name="parameters-as-inputs"/>

    <!-- print message -->
    <p>
      <div class="menu"><xsl:value-of select="$message"/></div>
    </p>

    <input type="submit" value="YES"/>
    &#160;&#160;&#160;
    <input type="button" onClick="location.href='{session:referer}';" value="Cancel"/>
    
  </form>
</xsl:template>

</xsl:stylesheet>  
