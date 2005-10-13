<?xml version="1.0"?>
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:i18n="http://apache.org/cocoon/i18n/2.1" xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0" xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0" xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xul="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul" version="1.0">
	
  <xsl:param name="contextprefix"/>
  <xsl:param name="publicationid"/>
  <xsl:param name="area"/>
  <xsl:param name="documentarea"/>
  <xsl:param name="completearea"/>
  <xsl:param name="documenturl"/>
  <xsl:param name="documentid"/>
  <xsl:param name="userid"/>
  <xsl:param name="servertime"/>
  <xsl:param name="workflowstate"/>
  <xsl:param name="islive"/>
  <xsl:param name="usecase"/>
  
  <xsl:variable name="image-prefix"><xsl:value-of select="$contextprefix"/>/lenya/menu/images</xsl:variable>
	  
  <xsl:template match="menu:menu">
    <xul:spacer style="height: 0px"/>
	  
    <xul:tabbox id="lenya-xul-tabbox">
      <xul:tabs>
        <xsl:for-each select="menu:tabs/menu:tab[@show='true']">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area" select="@label"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:if test="not(menu:tabs/menu:tab[@label='admin']/@show='false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">admin</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="not(menu:tabs/menu:tab[@label='info']/@show='false')">
        <!--<xsl:if test="not(menu:tabs/menu:tab[@label='site']/@show='false') and $area='admin'">-->
          <xsl:call-template name="site-area-tab">
          </xsl:call-template>
        </xsl:if>
        <xsl:call-template name="area-tab">
          <xsl:with-param name="tab-area">authoring</xsl:with-param>
        </xsl:call-template>
        <xsl:if test="not(menu:tabs/menu:tab[@label='live']/@show='false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">live</xsl:with-param>
            <xsl:with-param name="target">_blank</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
        <xul:box flex="350">
          <xul:vbox flex="1"/>
          <xul:box>
            <xul:description id="lenya-info">
              <xsl:attribute name="value"><xsl:call-template name="workflow"/>User: <xsl:value-of select="$userid"/> | ServerTime: <xsl:value-of select="$servertime"/></xsl:attribute>
            </xul:description>
          </xul:box>
        </xul:box>
      </xul:tabs>
    </xul:tabbox>
    
    <xul:vbox flex="1" id="lenya-xul-menubar">
	<xul:toolbox>
		<xul:menubar id="menubar">
		<xsl:apply-templates select="menu:menus/menu:menu" mode="menu"/>
		<xul:spacer flex="1"/>
		<xul:box style="width: 8px; height: 22px;"/>
		<xul:image id="statusImage" 
		src="{$image-prefix}/apache-lenya_logo_black.png" 
		style="width: 146px; height: 23px;"
		onmouseover="setWindowCursorTmp('pointer');"
		onmouseout="restoreWindowCursor();"	
		onclick="window.open('http://lenya.org');"
		tooltiptext="Visit lenya.org"
		/>
		</xul:menubar>	
	</xul:toolbox>
</xul:vbox>
    
  </xsl:template>
  
  <xsl:template name="area-tab">
    <xsl:param name="tab-area"/>
    <xsl:param name="tab-area-prefix" select="$tab-area"/>
    <xsl:param name="target" select="'_self'"/>
    <xsl:variable name="tab-documenturl">
      <xsl:choose>
        <xsl:when test="$tab-area = 'admin'">/index.html</xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$documenturl"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="selected">
      <xsl:choose>
        <xsl:when test="$area=$tab-area">true</xsl:when>
        <xsl:otherwise>false</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="tab-label">
      <xsl:value-of select="translate($tab-area,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:variable>
    <xul:tab label="{$tab-label}" onclick="window.location = '{$contextprefix}/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}';" id="lenya-xul-{$tab-area-prefix}-tab" selected="{$selected}"/>
  </xsl:template>
  
  <xsl:template name="site-area-tab">
    <xsl:param name="tab-area-prefix" select="authoring"/>
    <xsl:param name="target" select="'_self'"/>

    <xsl:variable name="tab-documenturl">
      <xsl:value-of select="$documenturl"/>
    </xsl:variable>

    <xsl:variable name="selected">true</xsl:variable>

    <xsl:variable name="tab-label">SITE</xsl:variable>

    <xul:tab label="{$tab-label}" onclick="window.location = '{$contextprefix}/{$publicationid}/authoring{normalize-space($tab-documenturl)}?lenya.usecase=tab.overview';" id="lenya-xul-{$tab-area-prefix}-tab" selected="{$selected}"/>
  </xsl:template>
  



  <xsl:template name="workflow">
    <i18n:text>Workflow State</i18n:text>: <b class="lenya-menubar-highlight"><i18n:text><xsl:value-of select="$workflowstate"/></i18n:text></b>
    
    (<xsl:if test="$islive = 'false'">
      <i18n:text>not</i18n:text>&#xA0;</xsl:if><i18n:text>live</i18n:text>)
   
   <xsl:text>|</xsl:text>
  </xsl:template>

<!--
  <xsl:template name="workflow">
    <li id="info-state"><i18n:text>Workflow State</i18n:text>: <span id="workflow-state"><i18n:text><xsl:value-of select="$workflowstate"/></i18n:text></span></li>

    <li id="info-live">
      <xsl:choose>
        <xsl:when test="$islive = 'false'">
          <i18n:text>not live</i18n:text>
        </xsl:when>
        <xsl:otherwise>
          <i18n:text>live</i18n:text>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>
-->
  
  <xsl:template match="menu:menu" mode="menu">
    <xul:menu label="{@name}">
      <xul:menupopup>
        <xsl:apply-templates select="menu:block[not(@*[local-name()=$area]='false')]" mode="menu"/>
      </xul:menupopup>
    </xul:menu>
  </xsl:template>
  
  <xsl:template match="menu:block" mode="menu">
    <xsl:apply-templates select="menu:item" mode="menu"/>
    <xsl:if test="position() != last()">
      <xul:menuseparator/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="menu:item" mode="menu">
    <xsl:choose>
      <xsl:when test="@href">
        <xul:menuitem>
          <xsl:attribute name="oncommand">
	    window.location = '<xsl:value-of select="@href"/><xsl:apply-templates select="@*[local-name()!='href']"/>'
          </xsl:attribute>
          <xsl:attribute name="label">
            <xsl:value-of select="text()"/>
          </xsl:attribute>
          <xsl:apply-templates select="menu:message"/>
        </xul:menuitem>
      </xsl:when>
      <xsl:otherwise>
        <xul:menuitem disabled="true">
          <xsl:attribute name="label">
            <xsl:value-of select="text()"/>
          </xsl:attribute>
          <xsl:apply-templates select="menu:message"/>
        </xul:menuitem>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="menu:message">
    <xsl:attribute name="tooltiptext">
      <xsl:value-of select="text()"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="menu:item/@uc:usecase"><xsl:text/><xsl:choose><xsl:when test="contains(../@href, '?')">&amp;</xsl:when><xsl:otherwise>?</xsl:otherwise></xsl:choose><xsl:text/>lenya.usecase=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
  <xsl:template match="menu:item/@wf:event"/>
    <xsl:template match="menu:item/@uc:step"><xsl:text/>&amp;lenya.step=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
</xsl:stylesheet>
