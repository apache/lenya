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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xul="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul"      
  >
  
<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="documentarea"/>
<xsl:param name="completearea"/>
<xsl:param name="documenturl"/>
<xsl:param name="documentid"/>
<xsl:param name="userid"/>
<xsl:param name="servertime"/>
<xsl:param name="workflowstate"/>
<xsl:param name="islive"/>

<xsl:variable name="image-prefix"><xsl:value-of select="$contextprefix"/>/lenya/menu/images</xsl:variable>
 
<xsl:template match="menu:menu">
        
	<xul:hbox id="lenya-menubar" style="background-image: url({$contextprefix}/lenya/menu/images/grau-bg2.gif);">
	   <xul:vbox flex="100">
        <xul:image src="{$image-prefix}/frame-bg_oben.gif" width="100%" height="4" />

	    <xul:hbox flex="100">
	   	<xul:image src="{$image-prefix}/blau_anfang_oben.gif"/>
	   	
	   	<xul:vbox flex="100">
	   	<xul:hbox>			    

        <!-- ADMIN TAB -->
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'admin']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">admin</xsl:with-param>
          </xsl:call-template>
        </xsl:if>          

        <!-- INFO/SITE TAB -->
        <xsl:variable name="info-area">
          <xsl:text>info-</xsl:text>
          <xsl:choose>
            <xsl:when test="$documentarea = 'admin'">authoring</xsl:when>
            <xsl:otherwise><xsl:value-of select="$documentarea"/></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
          
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'info']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area" select="$info-area"/>
            <xsl:with-param name="tab-area-prefix">info</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <!-- AUTHORING TAB -->
        <xsl:call-template name="area-tab">
          <xsl:with-param name="tab-area">authoring</xsl:with-param>
        </xsl:call-template>
          
        <!-- STAGING TAB -->
        <xsl:if test="menu:tabs/menu:tab[@label = 'staging']/@show = 'true'">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">staging</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <!-- LIVE TAB -->
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'live']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">live</xsl:with-param>
            <xsl:with-param name="target">_blank</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
        
        <xul:image src="{$contextprefix}/lenya/menu/images/grau-bg2.gif" flex="100"/>
        
	   	</xul:hbox>
	   	    <xul:image width="100%" src="{$image-prefix}/unten.gif" />
	  </xul:vbox>

    </xul:hbox>
    <xul:menubar oncommand="loadURL(event);" grippyhidden="true" id="lenya-xul-menubar" flex="100" 
        style="background-image: url({$contextprefix}/lenya/menu/images/menu-bg_2.gif); font-family: verdana, helvetica, sans-serif; font-size: 10pt;">
	    <xsl:apply-templates select="menu:menus/menu:menu" mode="menu"/>
	</xul:menubar>
   </xul:vbox>   
   <!--xul:vbox>
	<xul:image src="{$image-prefix}/lenya_unten.gif" />	
   </xul:vbox-->
 </xul:hbox>
	
</xsl:template>
  
  
  <xsl:template name="area-tab">
    <xsl:param name="tab-area"/>
    <xsl:param name="tab-area-prefix" select="$tab-area"/>
    <xsl:param name="target" select="'_self'"/>
    
    <xsl:variable name="tab-documenturl">
      <xsl:choose>
        <!-- index.html for link from/to admin area -->
        <xsl:when test="$tab-area = 'admin' or $documentarea = 'admin'">/index.html</xsl:when>
        <xsl:when test="starts-with($completearea, 'info') and $documentid = '/'">/index.html</xsl:when>
        <xsl:otherwise><xsl:value-of select="$documenturl"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="selected">
          <xsl:choose>
            <xsl:when test="starts-with($completearea, $tab-area-prefix)">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
          </xsl:choose>        
    </xsl:variable>
    
    <xsl:variable name="tab-image-url">
          <xsl:choose>
            <xsl:when test="starts-with($completearea, $tab-area-prefix)"><xsl:value-of select="concat($image-prefix, '/', $tab-area-prefix, '_active.gif')"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="concat($image-prefix, '/', $tab-area-prefix, '_inactive.gif')"/></xsl:otherwise>
          </xsl:choose>        
    </xsl:variable>

    
    <xsl:variable name="tab-label">
          <xsl:choose>
            <xsl:when test="$tab-area = 'info-authoring'">site</xsl:when>
            <xsl:otherwise><xsl:value-of select="$tab-area"/></xsl:otherwise>
          </xsl:choose>        
    </xsl:variable>
    
    
    <!--xul:tab label="{$tab-label}" 
        onclick="window.location = '{$contextprefix}/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}';" 
        id="lenya-xul-{$tab-area-prefix}-tab"
        image="{$tab-image-url}"
        selected="{$selected}"/-->
        
    <xul:image label="" 
        onclick="window.location = '{$contextprefix}/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}';" 
        id="lenya-xul-{$tab-area-prefix}-tab"
        src="{$tab-image-url}"/>
        
        
  </xsl:template>
  
  
  <xsl:template name="workflow">
    <i18n:text>Workflow State</i18n:text>: <b class="lenya-menubar-highlight"><i18n:text><xsl:value-of select="$workflowstate"/></i18n:text></b>
    <xsl:text>&#160;&#160;|&#160;&#160;</xsl:text>
    <xsl:if test="$islive = 'false'"><i18n:text>not</i18n:text>&#160;</xsl:if>
    <i18n:text>live</i18n:text><xsl:text>&#160;&#160;|</xsl:text>
  </xsl:template>  
  
  <xsl:template match="menu:menu" mode="menu">
    <xsl:if test="menu:block">
      <xul:menu label="{@name}">
     	 <xul:menupopup style="background-image: url({$contextprefix}/lenya/menu/images/bottombg.gif);">
           <xsl:apply-templates select="menu:block[not(@info = 'false') and starts-with($completearea, 'info') or not(@*[local-name() = $completearea] = 'false') and not(starts-with($completearea, 'info'))]" mode="menu"/>
       </xul:menupopup>
      </xul:menu>
    </xsl:if>
  </xsl:template>
  
  
  <!-- match blocks with not area='false' -->
  <xsl:template match="menu:block" mode="menu">
    <xsl:apply-templates mode="menu"/>

    <!--
    <xsl:apply-templates select="menu:item[not(@info = 'false') and starts-with($completearea, 'info') or not(@*[local-name() = $completearea] = 'false') and not(starts-with($completearea, 'info'))]"/>
    -->

    <xsl:if test="position() != last()">
      <xul:menuseparator/>
    </xsl:if>
  </xsl:template>
  	
  <!-- match items with not area='false' -->
  <xsl:template match="menu:item" mode="menu">
		<xsl:choose>
			<xsl:when test="@href">
			  <xul:menuitem label="{.}">
					<xsl:attribute name="value">
						<xsl:value-of select="@href"/>
						<xsl:apply-templates select="@*[local-name() != 'href']"/>
						<xsl:text/>
						<xsl:if test="starts-with($completearea, 'info-')">
							<xsl:choose>
								<xsl:when test="contains(@href, '?')"><xsl:text>&amp;</xsl:text></xsl:when>
								<xsl:otherwise><xsl:text>?</xsl:text></xsl:otherwise>
							</xsl:choose>
							 <xsl:text>lenya.area=info</xsl:text>
						</xsl:if>
					</xsl:attribute>
		      </xul:menuitem>
			</xsl:when>
			<xsl:otherwise>
			    <xul:menuitem label="{.}" disabled="true"/>
			</xsl:otherwise>
		</xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="menu:item/@uc:usecase">
    <xsl:text/>
    <xsl:choose>
      <xsl:when test="contains(../@href, '?')">&amp;</xsl:when>
      <xsl:otherwise>?</xsl:otherwise>
    </xsl:choose>
    <xsl:text/>lenya.usecase=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
  <xsl:template match="menu:item/@uc:step">
    <xsl:text/>&amp;lenya.step=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
  <xsl:template match="menu:item/@*[not(namespace-uri() = 'http://apache.org/cocoon/lenya/usecase/1.0')]"><xsl:copy-of select="."/></xsl:template>
  
  
</xsl:stylesheet>
