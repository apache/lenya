<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
<xsl:param name="context-prefix"/>
<xsl:param name="publication-id"/>
<xsl:param name="document-area"/>
<xsl:param name="complete-area"/>
<xsl:param name="document-url"/>

<xsl:variable name="image-prefix"><xsl:value-of select="$context-prefix"/>/lenya/menu/images</xsl:variable>
 
<xsl:template match="menu:menu">
    
	<div id="lenya-menubar">

    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="menu">
      <tr>
        <td background="{$image-prefix}/frame-bg_oben.gif" width="13" height="4">
          <img src="{$image-prefix}/frame-bg_oben.gif" width="13" height="4" /></td>
        <td background="{$image-prefix}/frame-bg_oben.gif" height="4">
          <img src="{$image-prefix}/frame-bg_oben.gif" height="4" /></td>
        <td background="{$image-prefix}/frame-bg_oben.gif" width="70%" height="4">
          <img src="{$image-prefix}/frame-bg_oben.gif" height="4" /></td>
        <td background="{$image-prefix}/frame-bg_oben.gif" width="101" height="4">
          <img src="{$image-prefix}/frame-bg_oben.gif" width="101" height="4" /></td>
      </tr>
      
      <tr>
        <td rowspan="2" valign="bottom" align="right" background="/lenya/lenya/menu/images/grau-bg.gif">
          <img src="{$image-prefix}/blau_anfang_oben.gif" />
        </td>
        <td background="{$image-prefix}/grau-bg2.gif">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">admin</xsl:with-param>
            <xsl:with-param name="tab-document-url">/index.html</xsl:with-param>
          </xsl:call-template>
          
          <xsl:variable name="tab-document-url">
            <xsl:choose>
              <xsl:when test="$complete-area = 'admin'">/index.html</xsl:when>
              <xsl:otherwise><xsl:value-of select="$document-url"/></xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          
          <xsl:variable name="info-area">
          	<xsl:text>info-</xsl:text>
          	<xsl:choose>
          		<xsl:when test="$document-area = 'admin'">authoring</xsl:when>
          		<xsl:otherwise><xsl:value-of select="$document-area"/></xsl:otherwise>
          	</xsl:choose>
          </xsl:variable>
          
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area" select="$info-area"/>
            <xsl:with-param name="tab-area-prefix">info</xsl:with-param>
            <xsl:with-param name="tab-document-url" select="$tab-document-url"/>
          </xsl:call-template>
          
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">authoring</xsl:with-param>
          </xsl:call-template>
          
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">live</xsl:with-param>
          </xsl:call-template>
          
        </td>
        
        <td valign="bottom" align="right" colspan="2" background="{$image-prefix}/grau-bg2.gif">
        	<div style="margin-right: 10px; color: #FFFFFF; font-size: 7pt; font-family: verdana, arial, sans-serif">
            <xsl:apply-templates select="workflow"/>&#160;&#160;User Id: <b><xsl:value-of select="current_username"/></b>&#160;&#160;|&#160;&#160;Server Time: <b><xsl:value-of select="server_time"/></b>
          </div>
          
        <div style="margin-top: 5px;"><img border="0" src="{$image-prefix}/lenya_oben_2.gif" /></div>
       </td>
      </tr>
      
      <tr>
        <td colspan="2" background="{$image-prefix}/unten.gif"><img border="0"
            src="{$image-prefix}/unten.gif" /></td>
        <td valign="top" rowspan="2"
          background="{$image-prefix}/grau-bg.gif"><img border="0"
            src="{$image-prefix}/lenya_unten.gif" /></td>
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
    <xsl:param name="tab-document-url" select="$document-url"/>
    
    <a id="{$tab-area-prefix}-tab" href="{$context-prefix}/{$publication-id}/{$tab-area}{$document-url}">
      <xsl:choose>
        <xsl:when test="starts-with($complete-area, $tab-area-prefix)">
          <img border="0" src="{$image-prefix}/{$tab-area-prefix}_active.gif" />
        </xsl:when>
        <xsl:otherwise>
           <img border="0" src="{$image-prefix}/{$tab-area-prefix}_inactive.gif" />
        </xsl:otherwise>
      </xsl:choose>
    </a>
  </xsl:template>
  
  
  <xsl:template match="menu:workflow">
    Workflow State: <b><xsl:value-of select="normalize-space(@state)"/></b>
    <xsl:text>&#160;&#160;|&#160;&#160;</xsl:text>
    <xsl:if test="normalize-space(@is-live) = 'false'">not&#160;</xsl:if>
    <xsl:text>live&#160;&#160;|</xsl:text>
  </xsl:template>
  
    
  <xsl:template match="menu:menu" mode="nav">
    <div id="nav{@label}" class="click" style="float:left; width: 100px; border-right: 1px solid #999999;">
      &#160;&#160;<xsl:value-of select="@name"/>
    </div>
  </xsl:template>
  
  
  <xsl:template match="menu:menu" mode="menu">
  	
    <div id="menu{@label}" class="menuOutline">
    	
      <div class="menuBg">
      	<xsl:apply-templates select="menu:block[not(@info = 'false') and starts-with($complete-area, 'info') or not(@authoring = 'false') and not(starts-with($complete-area, 'info'))]"/>
      </div>
      
    </div>
  </xsl:template>
  
  <!-- match blocks with not area='false' -->
  <xsl:template match="menu:block">
		<xsl:apply-templates select="menu:item[not(@info = 'false') and starts-with($complete-area, 'info') or not(@authoring = 'false') and not(starts-with($complete-area, 'info'))]"/>
		
		<xsl:if test="position() != last()">
			<div class="lenya-menubar-separator">
				<img src="{$image-prefix}/pixel.gif" height="1" alt="" />
			</div>
		</xsl:if>
  </xsl:template>
  	
  <!-- match items with not area='false' -->
  <xsl:template match="menu:item">
		<xsl:choose>
			<xsl:when test="@href">
				<a class="mI">
					<xsl:attribute name="href">
						<xsl:value-of select="@href"/>
						<xsl:apply-templates select="@*[local-name() != 'href']"/>
						<xsl:text/>
						<xsl:if test="starts-with($complete-area, 'info-')">
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