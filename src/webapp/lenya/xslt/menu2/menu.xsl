<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="bar">
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="/lenya/lenya/menu2/menu.css" />
    <script language="JavaScript" src="/lenya/lenya/menu2/menu.js"></script>
    <script language="JavaScript">

   <xsl:apply-templates select="tabs" />
   <xsl:apply-templates select="menus" />

</script>
  </head>
  <body class="authoring">
    <table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
      <tbody>
        <tr id="tabrow">
          <td id="tabs" valign="bottom"></td>
        </tr>
        <tr id="menurow" valign="top">
          <td>
            <div id="menubar"></div>
          </td>
        </tr>
        <tr id="contentrow">
          <td>
            <div><img src="/lenya/lenya/menu2/media/nix.gif" id="mask" /></div>
            <iframe id="page" name="page" src="{$context_prefix}{live_uri}" xsrc="{$context_prefix}{live_uri}"><xsl:apply-templates select="cmsbody" /></iframe>
          </td>
        </tr>
      </tbody>
    </table>
    <div id="tabTemplate" class="template"><div id="$name" class="tab$selected" onclick="lui._doClickTab(this.id, '$href');" href="$href" unselectable="on"><img src="$icon" height="16" align="absmiddle" />$name</div></div>
    <div id="menubuttonTemplate" class="template"><div class="menubuttoncontainer"><div id="$name" class="menubutton" onmouseover="this.className='menubuttonover';" onmouseout="this.className='menubutton';" onclick="lui._doClickMenuButton(this.id);" unselectable="on"><img src="/lenya/lenya/menu2/media/grau.gif" width="1" height="16" hspace="0" align="right" /><img src="$icon" height="16" align="absmiddle" />$name</div><br/><div id="menu_$name" class="menu"></div></div></div>
    <div id="menuitemTemplate" class="template"><div id="$name" class="menuitem" onmouseover="this.className='menuitemover';" onmouseout="this.className='menuitem';" onclick="lui._doClickMenuItem('$id', '$href');" unselectable="on" href="$href">$name</div></div>
  </body>
</html>
</xsl:template>

<xsl:template match="tabs">
       var tabDef = [
      <xsl:for-each select="tab">{ name:'<xsl:value-of select="@name" />', icon:'<xsl:value-of select="@src" />', href:'<xsl:value-of 
select="@href" />' }<xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>
	];
</xsl:template>

<xsl:template match="menus">
       var menubarDef = [
      <xsl:for-each select="menu">{ name:'<xsl:value-of select="@name" />', icon:'<xsl:value-of select="@src" />', href:'<xsl:value-of 
select="@href" />', items:[<xsl:apply-templates select="item" /><xsl:if test="position()!=last()">,</xsl:if>] }
<xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>
	];
</xsl:template>

<xsl:template match="item">
      { name:'<xsl:value-of select="@name" />', href:'<xsl:value-of select="@href" />'}
</xsl:template>

</xsl:stylesheet>

