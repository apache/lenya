<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<!--
<xsl:param name="xml"/>
<xsl:param name="xsd"/>
<xsl:param name="xslt"/>
-->

<xsl:param name="documentType"/>
<xsl:param name="documentUrl"/>
<xsl:param name="publicationId"/>
<xsl:param name="completeArea"/>
<xsl:param name="contextPrefix"/>
<xsl:param name="xopusContext"/>


<xsl:variable name="lenyaContext" select="concat($contextPrefix, '/', $publicationId, '/', $completeArea)"/>
<xsl:variable name="lenyaDocumentUrl" select="concat($lenyaContext, $documentUrl)"/>

<xsl:template match="/*">
<html>
  <head>
    <title>Load Xopus ...</title>
    <script language="JavaScript" src="/{$xopusContext}/xopus/xopus.js"/>
  </head>
  <body bgcolor="#FFFFFF">
    <div xopus="true" autostart="true">
      ...Xopus hasn't started yet...
      <xml>
        <pipeline
            xml="{$lenyaDocumentUrl}?lenya.usecase=xopus&amp;lenya.step=xml"
            xsd="{$lenyaDocumentUrl}?lenya.usecase=xopus&amp;lenya.step=xsd&amp;doctype={$documentType}">
            
          <view id="defaultView" default="true">
            <transform xsl="/{$xopusContext}/xopusPlugins/preparexinclude.xsl"/>
            <resolveXIncludes/>
            <transform xsl="{$lenyaDocumentUrl}?lenya.usecase=xopus&amp;lenya.step=xslt&amp;doctype={$documentType}">
              <param name="contextprefix"><xsl:value-of select="$contextPrefix"/></param>
              <param name="publicationid"><xsl:value-of select="$publicationId"/></param>
              <param name="completearea"><xsl:value-of select="$completeArea"/></param>
            </transform>
          </view>
          
          <!--
          <view id="step1">
            <transform xsl="/{$xopusContext}/xopusPlugins/preparexinclude.xsl"/>
            <transform xsl="/{$xopusContext}/xopus/media/treeview/tree.xsl"></transform>
          </view>
          <view id="step2">
            <transform xsl="/{$xopusContext}/xopusPlugins/preparexinclude.xsl"/>
            <resolveXIncludes/>
            <transform xsl="/{$xopusContext}/xopus/media/treeview/tree.xsl"></transform>
          </view>
          -->
          
          <view id="treeView">
            <transform xsl="/{$xopusContext}/xopus/media/treeview/tree.xsl"></transform>
          </view>
        </pipeline>
      </xml>
    </div>
  </body>
</html>
</xsl:template>

<!--
<xsl:template match="*[@context = 'lenya']/@src | *[@context = 'lenya']/@xml | *[@context = 'lenya']/@xsl | *[@context = 'lenya']/@xsd">
  <xsl:attribute name="{local-name(.)}"><xsl:value-of select="$lenyacontext"/><xsl:value-of select="."/></xsl:attribute>
</xsl:template>


<xsl:template match="*[@context = 'xopus']/@src | *[@context = 'xopus']/@xml | *[@context = 'xopus']/@xsl | *[@context = 'xopus']/@xsd">
  <xsl:attribute name="{local-name(.)}">/<xsl:value-of select="$xopuscontext"/><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
-->

<!--
<xsl:template match="head/script/@src">
  <xsl:attribute name="src"><xsl:value-of select="$xopuscontext"/>/xopus/xopus.js</xsl:attribute>
</xsl:template>

<xsl:template match="body/div/xml/pipeline/@xml">
  <xsl:attribute name="xml"><xsl:value-of select="$xml"/></xsl:attribute>
</xsl:template>

<xsl:template match="body/div/xml/pipeline/@xsd">
  <xsl:attribute name="xsd"><xsl:value-of select="$xsd"/></xsl:attribute>
</xsl:template>

<xsl:template match="body/div/xml/pipeline/view[@id='defaultView']/transform[last()]/@xsl">
  <xsl:attribute name="xsl"><xsl:value-of select="$xslt"/></xsl:attribute>
</xsl:template>

<xsl:template match="transform[@type='prepareinclude']">
  <transform xsl="/{$xopuscontext}/xopusPlugins/preparexinclude.xsl"/>
</xsl:template>


<xsl:template match="body/div/xml/pipeline/view[@id='defaultView']/transform">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
    <param name="contextprefix"><xsl:value-of select="$contextprefix"/></param>
    <param name="publicationid"><xsl:value-of select="$publicationid"/></param>
    <param name="completearea"><xsl:value-of select="$completearea"/></param>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
-->
</xsl:stylesheet>
