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
    
	<div id="lenya-menubar">
	  
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="lenya-menubar-menu">
      <tr>
        <td style="background-image: url('{$image-prefix}/frame-bg_oben.gif'); width: 13px; height: 4px;">
          <img src="{$image-prefix}/frame-bg_oben.gif" width="13" height="4" alt=""/></td>
        <td style="background-image: url('{$image-prefix}/frame-bg_oben.gif'); height: 4px;">
          <img src="{$image-prefix}/frame-bg_oben.gif" height="4" alt=""/></td>
        <td style="background-image: url('{$image-prefix}/frame-bg_oben.gif'); width: 70%; height: 4px;">
          <img src="{$image-prefix}/frame-bg_oben.gif" height="4" alt=""/></td>
        <td style="background-image: url('{$image-prefix}/frame-bg_oben.gif'); width: 101px; height: 4px;">
          <img src="{$image-prefix}/frame-bg_oben.gif" width="101" height="4" alt=""/></td>
      </tr>
      
      <tr>
        <td class="lenya-menubar-td-left" rowspan="2" valign="bottom" align="right"
            style="background-image: url('{$contextprefix}/lenya/menu/images/grau-bg.gif')">
          <img src="{$image-prefix}/blau_anfang_oben.gif" alt=""/>
        </td>
        <td style="background-image: url('{$image-prefix}/grau-bg2.gif'); white-space: nowrap;">

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
            <xsl:with-param name="target">external</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        </td>
        
        <td class="lenya-menubar-td-infoline" valign="bottom" align="right" colspan="2"
            style="background-image: url('{$image-prefix}/grau-bg2.gif')">
        	<div class="lenya-menubar-infoline">
        	  
        	  <xsl:if test="$workflowstate != ''">
        	    <xsl:call-template name="workflow"/>
        	  </xsl:if>
            &#160;<i18n:text>User</i18n:text>: <strong><xsl:value-of select="$userid"/></strong>&#160;&#160;|&#160;&#160;<i18n:text>Server Time</i18n:text>: <strong><xsl:value-of select="$servertime"/></strong>
          </div>
          
        <div style="margin-top: 5px;"><img style="border: 0px;" src="{$image-prefix}/lenya_oben_2.gif" alt=""/></div>
       </td>
      </tr>
      
      <tr>
        <td colspan="2" style="background-image: url('{$image-prefix}/unten.gif')"><img style="border: 0px"
            src="{$image-prefix}/unten.gif" alt=""/></td>
        <td valign="top" rowspan="2"
          style="background-image: url('{$image-prefix}/grau-bg.gif')"><img style="border: 0px"
            src="{$image-prefix}/lenya_unten.gif" alt=""/></td>
      </tr>
      
      <tr valign="top">
        <td valign="top" colspan="3">
          <div id="navTop">
            <div id="navTopBG">
            	<div style="height: 100%; padding: 3px 0px;">
						    <div style="float:left; width:12px; border-right: solid 1px #999999;">&#160;</div>
  	            <xsl:apply-templates select="menu:menus/menu:menu" mode="nav"/>&#160;
            	</div>
            </div>
          </div>
        </td>
      </tr>
    </table>
    
    <xsl:apply-templates select="menu:menus/menu:menu" mode="menu"/>
	</div>
	
</xsl:template>
  
  
  <xsl:template name="area-tab">
    <xsl:param name="tab-area"/>
    <xsl:param name="tab-area-prefix" select="$tab-area"/>
    <xsl:param name="target" select="'internal'"/>
    
    <xsl:variable name="tab-documenturl">
      <xsl:choose>
        <!-- index.html for link from/to admin area -->
        <xsl:when test="$tab-area = 'admin' or $documentarea = 'admin'">/index.html</xsl:when>
        <xsl:when test="starts-with($completearea, 'info') and $documentid = '/'">/index.html</xsl:when>
        <xsl:otherwise><xsl:value-of select="$documenturl"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <a id="{$tab-area-prefix}-tab"
       href="{$contextprefix}/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}"
       rel="{$target}">
       <!--
       target="{$target}"
       -->
      <xsl:choose>
        <xsl:when test="starts-with($completearea, $tab-area-prefix)">
          <img style="border: 0px" src="{$image-prefix}/{$tab-area-prefix}_active.gif" alt="active {$tab-area-prefix} tab"/>
        </xsl:when>
        <xsl:otherwise>
           <img style="border: 0px" src="{$image-prefix}/{$tab-area-prefix}_inactive.gif" alt="inactive {$tab-area-prefix} tab"/>
        </xsl:otherwise>
      </xsl:choose>
    </a>
  </xsl:template>
  
  
  <xsl:template name="workflow">
    <i18n:text>Workflow State</i18n:text>: <b class="lenya-menubar-highlight"><i18n:text><xsl:value-of select="$workflowstate"/></i18n:text></b>
    <xsl:text>&#160;&#160;|&#160;&#160;</xsl:text>
    <xsl:if test="$islive = 'false'"><i18n:text>not</i18n:text>&#160;</xsl:if>
    <i18n:text>live</i18n:text><xsl:text>&#160;&#160;|</xsl:text>
  </xsl:template>
  
    
  <xsl:template match="menu:menu" mode="nav">
    <div id="nav{@label}" class="click" style="float:left; width: 100px; border-right: 1px solid #999999;">
      &#160;&#160;<xsl:value-of select="@name"/>
    </div>
  </xsl:template>
  
  <xsl:template match="menu:menu[not(*)]" mode="nav">
    <div id="nav{@label}" class="click" style="float:left; width: 100px; border-right: 1px solid #999999;">
      &#160;&#160;<span class="lenya-menubar-menu-disabled"><xsl:value-of select="@name"/></span>
    </div>
  </xsl:template>
  
  
  <xsl:template match="menu:menu" mode="menu">
    <div id="menu{@label}" class="menuOutline">
      <div class="menuBg" style="">
      	<xsl:apply-templates select="menu:block[not(@info = 'false') and starts-with($completearea, 'info') or not(@*[local-name() = $completearea] = 'false') and not(starts-with($completearea, 'info'))]" mode="submenu"/>
      </div>
    </div>
  </xsl:template>
  
  
  <!-- match blocks with not area='false' -->
  <xsl:template match="menu:block" mode="submenu">
    <xsl:apply-templates select="menu:title"/>

    <!-- * is menu:item and menu:menu -->
    <xsl:apply-templates select="*[not(@info = 'false') and starts-with($completearea, 'info') or not(@*[local-name() = $completearea] = 'false') and not(starts-with($completearea, 'info'))]" mode="submenu"/>
		
    <xsl:if test="position() != last()">
      <div class="lenya-menubar-separator">
        <img src="{$image-prefix}/pixel.gif" height="1" alt="" />
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="menu:menu" mode="submenu">
    <span class="mI"><strong><xsl:value-of select="@name"/></strong></span>
    <xsl:apply-templates mode="submenu"/>
  </xsl:template>
  
  <xsl:template match="menu:title">
    <span class="mI"><strong><xsl:apply-templates/></strong></span>
  </xsl:template>
  	
  <!-- match items with not area='false' -->
  <xsl:template match="menu:item" mode="submenu">
		<xsl:choose>
			<xsl:when test="@href">
				<a class="mI">
					<xsl:attribute name="href">
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
					</xsl:attribute><xsl:value-of select="."/></a>
			</xsl:when>
			<xsl:otherwise>
				<span class="mI"><xsl:value-of select="."/></span>
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
