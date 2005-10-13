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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0" xmlns:xhtml="http://www.w3.org/1999/xhtml" version="1.0">
	
  <xsl:param name="area"/>
  <xsl:param name="contextprefix"/>
  <xsl:param name="publicationid"/>
  <xsl:param name="documenturl"/>
  
  <xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
	  
    <xso:stylesheet xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml" xmlns:xul="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul" version="1.0">
		  
      <xso:output method="xml" indent="yes"/>
      <xsl:if test="$area != 'live'">
	      
        <xso:template match="/">
    	  <xso:processing-instruction name="xml-stylesheet">
            href="<xsl:value-of select="$contextprefix"/>/lenya/css/xulmenu.css" type="text/css"
          </xso:processing-instruction>
	  
	  <xso:apply-templates select="xhtml:html/xhtml:head/xhtml:link"/>
	  
	  <xul:window xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xul="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul" onload="fixTabSelection();fixTitle()">
		  
            <xso:attribute name="title">
              <xsl:value-of select="concat('Apache Lenya - ', $publicationid, ' - ', $area, ' - ', $documenturl, ' - ')"/>
            </xso:attribute>
       
            <xul:script type="application/x-javascript">
              function loadURL(event) {
              	var url = event.target.getAttribute('value');
              	if (url) window.location = url;
              }
            </xul:script>
	    
            <!-- Workaround for the tab' non working select attribute -->
            <xul:script type="application/x-javascript">
              function fixTabSelection() {
	      	var item = document.getElementById('lenya-xul-tabbox');
		var label = item.selectedTab.getAttribute('label').toLowerCase();
		while (label != '<xsl:value-of select="$area"/>' &amp;&amp; item.selectedTab.nextSibling!=null) {
		  item._tabs.advanceSelectedTab(0);
		  label = item.selectedTab.getAttribute('label').toLowerCase();
		}
	      }	      
            </xul:script>
	    
            <!-- Workaround for the windows' non working title attribute -->
            <xul:script type="application/x-javascript">
              function fixTitle() {
	  	document.title = '<xsl:value-of select="concat('Apache Lenya | ', $publicationid, ' | ', $area, ' | ', $documenturl, ' | ')"/>';    	
	      }	      
            </xul:script>
	    
            <xsl:apply-templates select="xul:*"/>
            <xul:spacer flex="1"/>
            <xul:box style="overflow: auto;" flex="100" id="lenya-content-box">
              <xul:vbox flex="1">
                <xhtml:html>
                  <xso:apply-templates select="xhtml:html/xhtml:body"/>
                </xhtml:html>
              </xul:vbox>
            </xul:box>
          </xul:window>
        </xso:template>
  
        <xso:template match="xhtml:link">
          <xso:processing-instruction name="xml-stylesheet">
            href="<xso:value-of select="@href"/>" type="<xso:value-of select="@type"/>"
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
