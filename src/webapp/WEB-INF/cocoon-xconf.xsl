<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : cocoon-xconf.xsl
    Created on : 6. März 2003, 11:39
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">


<xsl:template match="markup-languages/xsp-language/target-language[@name = 'java']">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <xsl:comment>Lenya Logicsheets</xsl:comment>
    
    <builtin-logicsheet>
      <parameter name="prefix" value="xsp-lenya"/>
      <parameter name="uri" value="http://www.lenya.org/xsp/lenya/1.0"/>
      <parameter name="href" value="resource://org/lenya/cms/cocoon/logicsheets/util.xsl"/>
    </builtin-logicsheet>

    <builtin-logicsheet>
      <parameter name="prefix" value="xsp-hello"/>
      <parameter name="uri" value="http://www.lenya.org/xsp/hello/1.0"/>
      <parameter name="href" value="resource://org/lenya/cms/cocoon/logicsheets/helloworld.xsl"/>
    </builtin-logicsheet>
    
    <xsl:comment>/ Lenya Logicsheets</xsl:comment>
    
  </xsl:copy>
</xsl:template>
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
    
</xsl:stylesheet> 
