<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
    >

<xsl:template match="menu">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="menu">
<tr>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="13" height="4">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="13" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" width="200" height="4">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="200" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" width="286">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="286" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" width="97">
<img src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="97" height="4" /></td>
<td background="/lenya/lenya/menu/images/frame-bg_oben.gif" height="4" width="4"><img
src="/lenya/lenya/menu/images/frame-bg_oben.gif" width="4" height="4" /></td>
</tr>

<tr>
<td rowspan="2" valign="top" align="right" background="/lenya/lenya/menu/images/grau-bg.gif">
  <img src="/lenya/lenya/menu/images/blau_anfang_oben.gif" />
</td>
<td background="/lenya/lenya/menu/images/grau-bg2.gif">
  <a href="{url-info/context-prefix}/{url-info/publication-id}/admin/index.html">
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
  
  <a href="{url-info/context-prefix}/{url-info/publication-id}/info{$document-url}">
   <xsl:choose><xsl:when test="url-info/area = 'info'">
    <img border="0" src="/lenya/lenya/menu/images/info_active.gif" />
    </xsl:when><xsl:otherwise>
     <img border="0" src="/lenya/lenya/menu/images/info_inactive.gif" />
    </xsl:otherwise></xsl:choose>
  </a>
  <a href="{url-info/context-prefix}/{url-info/publication-id}/authoring{$document-url}">
   <xsl:choose><xsl:when test="url-info/area = 'authoring'">
    <img border="0" src="/lenya/lenya/menu/images/authoring_active.gif" />
    </xsl:when><xsl:otherwise>
     <img border="0" src="/lenya/lenya/menu/images/authoring_inactive.gif" />
    </xsl:otherwise></xsl:choose>
  </a>
  <a target="_blank" href="{url-info/context-prefix}/{url-info/publication-id}/live{$document-url}">
    <img border="0" src="/lenya/lenya/menu/images/live_inactive.gif" />
  </a>
</td>

<td align="right" colspan="2" background="/lenya/lenya/menu/images/grau-bg2.gif">
<font color="#ffffff" size="-2" face="verdana">
  <xsl:apply-templates select="workflow-state"/>
  User Id: <b><xsl:value-of select="current_username"/></b> | Server Time: <b><xsl:value-of select="server_time"/></b> &#160;&#160;&#160;
</font>
</td>
<td background="/lenya/lenya/menu/images/grau-bg.gif" height="4" width="2"><img
src="/lenya/lenya/menu/images/grau-bg.gif" width="2" height="4" /></td>
</tr>

<tr>
<td colspan="3" background="/lenya/lenya/menu/images/unten.gif"><img border="0"
src="/lenya/lenya/menu/images/unten.gif" /></td>
<td valign="top" rowspan="2" colspan="2" 
background="/lenya/lenya/menu/images/grau-bg.gif"><img border="0"
src="/lenya/lenya/menu/images/lenya_unten.gif" /></td>
</tr>

<tr valign="top">
  <td width="13" background="/lenya/lenya/menu/images/menu-bg.gif">
    <img border="0" src="/lenya/lenya/menu/images/menu_bg_anfang2.gif" />
  </td>
  <td colspan="3" valign="top" background="/lenya/lenya/menu/images/menu-bg.gif">
  <div id="navTop">
  <div id="navTopBG">
    <xsl:apply-templates select="menus/menu" mode="nav"/>
  </div>
  </div>
  </td>
</tr>
</table>

<xsl:apply-templates select="menus/menu" mode="menu"/>
</xsl:template>

<xsl:template match="workflow-state">
  Workflow State: <b><xsl:apply-templates/></b> |
</xsl:template>


<xsl:template match="menu" mode="nav">
  <div style="float:left; width:1px"><img src="/lenya/lenya/menu/images/grau.gif" width="1" height="21" /></div>

<div style="float:left; width:10px">&#160;</div>

<div id="nav{@label}" class="click" style="float:left; width:46px">
<font size="-1" face="verdana"><b>&#160;<xsl:value-of select="@name"/></b></font>
</div>

  <div style="float:left; width:46px">&#160;</div>
<div style="float:left; width:1px"><img src="/lenya/lenya/menu/images/grau.gif" width="1" height="21" /></div>
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
                	</xsl:attribute><xsl:value-of select="."/></a>
              </xsl:when>
              <xsl:otherwise>
                <span class="mI"><xsl:value-of select="."/></span>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>

    <xsl:if test="position() != last()">
      <div style="height: 2px; background-color: #EEEEEE; background-repeat: repeat; background-attachment: scroll; background-image: url('/lenya/lenya/menu/images/dotted.gif'); background-position: 0% 50%">
      <div style="background:#EEEEEE"><img src="/lenya/lenya/menu/images/pixel.gif" height="1" alt="" />
      </div>
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