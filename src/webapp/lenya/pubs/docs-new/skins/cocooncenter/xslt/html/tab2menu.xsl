<?xml version="1.0"?>
<!--
This stylesheet generates 'tabs' at the top left of the Forrest skin.  Tabs are
visual indicators that a certain subsection of the URI space is being browsed.
For example, if we had tabs with paths:

Tab1:  ''
Tab2:  'community'
Tab3:  'community/howto'
Tab4:  'community/howto/xmlform/index.html'

Then if the current path was 'community/howto/foo', Tab3 would be highlighted.
The rule is: the tab with the longest path that forms a prefix of the current
path is enabled.

The output of this stylesheet is HTML of the form:
    <div class="tab">
      ...
    </div>

which is then merged by site2xhtml.xsl

$Id: tab2menu.xsl,v 1.1 2003/04/09 23:28:24 andreas Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/tab2menu.xsl"/>

  <!--
  <xsl:template match="tabs">
    <div class="tab">
      <table cellspacing="0" cellpadding="0" border="0" summary="tab bar">
        <tr>
          <xsl:call-template name="base-tabs"/>
        </tr>
      </table>
    </div>
  </xsl:template>
  -->
  
<xsl:template match="tabs">
  <div class="tab">
  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td><img src="{$root}skin/images/trans1x1.gif" border="0" width="20" height="1"/></td>
      <!--<xsl:apply-templates select="nav:tab"/>-->
      <xsl:call-template name="base-tabs"/>
    </tr>
  </table>
  </div>
</xsl:template>
  

  <xsl:template name="pre-separator">
    <xsl:call-template name="separator"/>
  </xsl:template>

  <xsl:template name="post-separator">
  </xsl:template>

  <xsl:template name="separator">
  <!--
    <td width="8">
      <img src="{$root}skin/images/spacer.gif" width="8" height="8" alt=""/>
    </td>
  -->
  </xsl:template>

  <xsl:template name="selected">
    <!--
    <td valign="bottom">
      <table cellspacing="0" cellpadding="0" border="0" summary="selected tab">
        <tr>
          <td class="top-left" height="22"></td>
          <td bgcolor="#a5b6c6" valign="middle" height="22">
            <span class="tab">
              <b>
                <xsl:call-template name="base-selected"/>
              </b>
            </span>
          </td>
          <td class="top-right" height="22"></td>
        </tr>
      </table>
    </td>
    -->
      <td><img src="{$root}skin/images/current/mainmenu-left.jpeg"/></td>
      <td background="{$root}skin/images/current/mainmenu-background.jpeg">
        &#160;<xsl:call-template name="base-selected"/>&#160;
      </td>
      <td><img src="{$root}skin/images/current/mainmenu-right.jpeg"/></td>
  </xsl:template>

  <xsl:template name="not-selected">
    <!--
    <td valign="bottom">
      <table cellspacing="0" cellpadding="0" border="0" summary="non selected tab">
        <tr>
          <td class="top-left-tab" height="18"></td>
          <td bgcolor="#cedfef" valign="middle" height="18">
            <span class="tab">
              <xsl:call-template name="base-not-selected"/>
            </span>              
          </td>
          <td class="top-right-tab" height="18"></td>
        </tr>
        <tr>
          <td height="1" colspan="3">
          </td>
        </tr>     
      </table>
    </td>
    -->
      <td><img src="{$root}skin/images/default/mainmenu-left.jpeg"/></td>
      <td background="{$root}skin/images/default/mainmenu-background.jpeg">
        &#160;<xsl:call-template name="base-not-selected"/>&#160;
      </td>
      <td><img src="{$root}skin/images/default/mainmenu-right.jpeg"/></td>
  </xsl:template>

</xsl:stylesheet>
