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

$Id: tab2menu.xsl,v 1.2 2003/05/08 12:03:48 andreas Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/tab2menu.xsl"/>

  <xsl:template match="tabs">
    <div class="tab">
      <table cellspacing="0" cellpadding="0" border="0" summary="tab bar">
        <tr>
          <xsl:call-template name="base-tabs"/>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template name="pre-separator">
    <td valign="bottom" width="10">
      <div class="tab-separator">
        <img src="{$root}skin/images/spacer.gif" width="10" alt=""/>
      </div>
    </td>
  </xsl:template>

  <xsl:template name="post-separator">
  </xsl:template>

  <xsl:template name="separator">
  </xsl:template>

  <xsl:template name="selected">
    <td width="5" valign="top" background="{$root}skin/images/tab-left-selected.png">
      <img src="{$root}skin/images/tab-corner-left-selected.png"/>
    </td>
    <td class="tab" valign="bottom">
      <div class="tab-selected">
        <nobr><xsl:call-template name="base-selected"/></nobr>
      </div>
    </td>
    <td width="5" valign="top" background="{$root}skin/images/tab-right-selected.png">
      <img src="{$root}skin/images/tab-corner-right-selected.png"/>
    </td>
  </xsl:template>

  <xsl:template name="not-selected">
    <td class="tab" valign="bottom">
      <div class="tab-separator">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td height="5" width="5" valign="top" background="{$root}skin/images/tab-left.png">
              <img src="{$root}skin/images/tab-corner-left.png"/>
            </td>
            <td class="tab" valign="bottom" rowspan="2">
              <div class="tab-not-selected-shadow">
                <div class="tab-not-selected">
                <nobr><xsl:call-template name="base-not-selected"/></nobr>
                </div>
              </div>              
            </td>
            <td height="5" width="5" valign="top" background="{$root}skin/images/tab-right.png">
              <img src="{$root}skin/images/tab-corner-right.png"/>
            </td>
          </tr>
          <tr>
            <td valign="bottom" background="{$root}skin/images/tab-left.png">
              <div class="tab-not-selected-shadow-left">
                <img src="{$root}skin/images/spacer.gif" width="4" />
              </div>
            </td>
            <td valign="bottom" background="{$root}skin/images/tab-right.png">
              <div class="tab-not-selected-shadow-right">
                <img src="{$root}skin/images/spacer.gif" width="4" />
              </div>
            </td>
          </tr>
        </table>
      </div>
    </td>
  </xsl:template>

</xsl:stylesheet>
