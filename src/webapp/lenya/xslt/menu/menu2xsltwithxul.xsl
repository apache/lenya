<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: menu2xsltwithxul.xsl,v 1.1 2004/09/01 21:59:46 michi Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
  >
  
<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="area"/>
<xsl:param name="documentid"/>
  
<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>

<xsl:output method="xml" indent="yes"/>
  
<xsl:template match="/">
  
  <xso:stylesheet version="1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xul="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul">
    <!--
    xmlns="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul"-->
<!--    exclude-result-prefixes="xhtml"-->
    
  <xso:output method="xml" indent="yes"/>

  <xsl:if test="$area != 'live'">
    
  <xso:template match="/">
      <!--xso:processing-instruction name="xml-stylesheet">
          href="chrome://global/skin/" type="text/css"
      </xso:processing-instruction-->
      <xso:processing-instruction name="xml-stylesheet">
          href="<xsl:value-of select="$contextprefix"/>/lenya/css/xulmenu.css" type="text/css"
      </xso:processing-instruction>
      
      <xso:apply-templates select="xhtml:html/xhtml:head/xhtml:link"/>
      
      <xul:window
          xmlns="http://www.w3.org/1999/xhtml"
          xmlns:xul="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul">
          
          
          <!-- TODO does not work !!! -->
          <!--xso:attribute name="title"><xsl:value-of select="concat('Apache Lenya - ', $publicationid, ' - ', $area, ' - ', $documentid, ' - ')"/><xso:value-of select="xhtml:html/xhtml:head/xhtml:title"/></xso:attribute-->
          
          <xul:script type="application/x-javascript">
              function loadURL(event) {
              	var url = event.target.getAttribute('value');
              	if (url) window.location = url;
              }
          </xul:script>
          <xsl:apply-templates select="xul:hbox[@id = 'lenya-menubar']"/>
          <xul:spacer flex="1" />
          <xul:box style="overflow: auto;" flex="100" id="lenya-content-box">
              <xul:vbox flex="1">
                  <html>
                  <body>
                  	<xso:apply-templates select="xhtml:html/xhtml:body/node()"/>
                  </body></html>
              </xul:vbox>
          </xul:box>
      </xul:window>
  </xso:template>
  
  <xso:template match="xhtml:link">
      <xso:processing-instruction name="xml-stylesheet">
          href="<xso:value-of select="@href"/>" type="text/css" 
      </xso:processing-instruction>
  </xso:template>
  
  </xsl:if>
    
  <xso:template match="xhtml:*">
    <xso:element>
      <xsl:attribute name="name">{local-name()}</xsl:attribute>
      <xso:apply-templates select="@*|node()"/>
    </xso:element>
  </xso:template>
  
  
  <xso:template match="@*|node()">
    <xso:copy>
      <xso:apply-templates select="@*|node()"/>
    </xso:copy>
  </xso:template>

  </xso:stylesheet>
  
</xsl:template>
  


<xsl:template match="xhtml:*">
  <xsl:element name="{local-name()}">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:element>
</xsl:template>

  
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
  

</xsl:stylesheet>
