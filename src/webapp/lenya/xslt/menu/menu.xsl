<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
  >
  
<xsl:param name="infoarea" select="''"/>
  
  <xsl:template match="menu">
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="menu">
      <tr>
        <td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="13" height="4">
          <img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="13" height="4" /></td>
          
        <td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4">
          <img src="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" /></td>
          
        <td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="70%" height="4">
          <img src="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" /></td>
          
        <td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="101" height="4">
          <img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="101" height="4" /></td>
          
      </tr>
      
      <tr>
        <td rowspan="2" valign="bottom" align="right" background="/lenya/lenya/menu/images/grau-bg.gif">
          <img src="/lenya/lenya/menu/images/blau_anfang_oben.gif" />
        </td>
        <td background="/lenya/lenya/menu/images/grau-bg2.gif">
          <a id="admin-tab" href="{url-info/context-prefix}/{url-info/publication-id}/admin/index.html">
            <xsl:choose><xsl:when test="url-info/area = 'admin'">
                <img border="0" src="/lenya/lenya/menu/images/admin_active.gif" />
              </xsl:when><xsl:otherwise>
                <img border="0" src="/lenya/lenya/menu/images/admin_inactive.gif" />
              </xsl:otherwise></xsl:choose>
          </a>
          
          <xsl:variable name="document-url">
            <xsl:choose>
              <xsl:when test="url-info/area = 'admin'">/index.html</xsl:when>
              <xsl:otherwise><xsl:value-of select="url-info/document-url"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          
          <a id="info-tab" href="{url-info/context-prefix}/{url-info/publication-id}/info-{url-info/area}{$document-url}?lenya.usecase=info-overview&amp;lenya.step=showscreen">
            <xsl:choose><xsl:when test="$infoarea = 'true'">
                <img border="0" src="/lenya/lenya/menu/images/info_active.gif" />
              </xsl:when><xsl:otherwise>
                <img border="0" src="/lenya/lenya/menu/images/info_inactive.gif" />
              </xsl:otherwise></xsl:choose>
          </a>
          <a id="authoring-tab" href="{url-info/context-prefix}/{url-info/publication-id}/authoring{$document-url}">
            <xsl:choose><xsl:when test="url-info/area = 'authoring' and not($infoarea = 'true')">
                <img border="0" src="/lenya/lenya/menu/images/authoring_active.gif" />
              </xsl:when><xsl:otherwise>
                <img border="0" src="/lenya/lenya/menu/images/authoring_inactive.gif" />
              </xsl:otherwise></xsl:choose>
          </a>
          <a id="live-tab" target="_blank" href="{url-info/context-prefix}/{url-info/publication-id}/live{$document-url}">
            <img border="0" src="/lenya/lenya/menu/images/live_inactive.gif" />
          </a>
        </td>
        
        <td valign="bottom" align="right" colspan="2" background="/lenya/lenya/menu/images/grau-bg2.gif">
        	<div style="margin-right: 10px; color: #FFFFFF; font-size: 7pt; font-family: verdana, arial, sans-serif">
            <xsl:apply-templates select="workflow"/>&#160;&#160;User Id: <b><xsl:value-of select="current_username"/></b>&#160;&#160;|&#160;&#160;Server Time: <b><xsl:value-of select="server_time"/></b>
          </div>
         <!-- 
        </td>
        <td background="/lenya/lenya/menu/images/grau-bg.gif" height="4" width="2" valign="bottom">
        -->
        <div style="margin-top: 5px;"><img border="0" src="/lenya/lenya/menu/images/lenya_oben_2.gif" /></div>
       </td>
      </tr>
      
      <tr>
        <td colspan="2" background="/lenya/lenya/menu/images/unten.gif"><img border="0"
            src="/lenya/lenya/menu/images/unten.gif" /></td>
        <td valign="top" rowspan="2"
          background="/lenya/lenya/menu/images/grau-bg.gif"><img border="0"
            src="/lenya/lenya/menu/images/lenya_unten.gif" /></td>
      </tr>
      
      <tr valign="top">
        <td valign="top" colspan="3">
          <div id="navTop">
            <div id="navTopBG">
            	<div style="height: 100%; padding: 3px 0px;">
						    <div style="float:left; width:12px; border-right: solid 1px #999999;">&#160;</div>
  	            <xsl:apply-templates select="menus/menu" mode="nav"/>&#160;
            	</div>
            </div>
          </div>
        </td>
      </tr>
    </table>
    
    <xsl:apply-templates select="menus/menu" mode="menu"/>
  </xsl:template>
  
  
  <xsl:template match="workflow">
    Workflow State: <b><xsl:value-of select="normalize-space(@state)"/></b>
    <xsl:text>&#160;&#160;|&#160;&#160;</xsl:text>
    <xsl:if test="normalize-space(@is-live) = 'false'">not&#160;</xsl:if>
    <xsl:text>live&#160;&#160;|</xsl:text>
  </xsl:template>
  
    
  <xsl:template match="menu" mode="nav">
    <div id="nav{@label}" class="click" style="float:left; width: 100px; border-right: 1px solid #999999;">
      &#160;&#160;<xsl:value-of select="@name"/>
    </div>
  </xsl:template>
  
  
  <xsl:template match="menu" mode="menu">
  	
    <div id="menu{@label}" class="menuOutline">
    	
      <div class="menuBg">
        <xsl:for-each select="block">
          <xsl:for-each select="item">
            <xsl:choose>
              <xsl:when test="@href">
                <a class="mI">
                  <xsl:attribute name="href">
                    <xsl:value-of select="@href"/>
                    <xsl:apply-templates select="@*[local-name() != 'href']"/>
                    <xsl:text/>
                    <xsl:if test="$infoarea = 'true'">
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
          </xsl:for-each>
          
          <xsl:if test="position() != last()">
            <div class="lenya-menubar-separator">
              <img src="/lenya/lenya/menu/images/pixel.gif" height="1" alt="" />
            </div>
          </xsl:if>
        </xsl:for-each>
      </div>
      
    </div>
  </xsl:template>
  
  
  <xsl:template match="item/@uc:usecase">
    <xsl:text/>
    <xsl:choose>
      <xsl:when test="contains(../@href, '?')">&amp;</xsl:when>
      <xsl:otherwise>?</xsl:otherwise>
    </xsl:choose>
    <xsl:text/>lenya.usecase=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
  <xsl:template match="item/@uc:step">
    <xsl:text/>&amp;lenya.step=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
  <xsl:template match="item/@*[not(namespace-uri() = 'http://apache.org/cocoon/lenya/usecase/1.0')]"><xsl:copy-of select="."/></xsl:template>
  
  
</xsl:stylesheet>