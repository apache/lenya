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

<!-- $Id: tab2menu.xsl,v 1.5 2004/03/13 12:42:11 gregor Exp $ -->
    
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
    <td width="5" valign="top" style="background-image: url({$root}skin/images/tab-left-selected.png)">
      <img src="{$root}skin/images/tab-corner-left-selected.png" alt=""/>
    </td>
    <td class="tab" valign="bottom">
      <div class="tab-selected">
        <xsl:call-template name="base-selected"/>
      </div>
    </td>
    <td width="5" valign="top" style="background-image: url({$root}skin/images/tab-right-selected.png)">
      <img src="{$root}skin/images/tab-corner-right-selected.png" alt=""/>
    </td>
  </xsl:template>

  <xsl:template name="not-selected">
    <td class="tab" valign="bottom">
      <div class="tab-separator">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td height="5" width="5" valign="top" style="background-image: url({$root}skin/images/tab-left.png)">
              <img src="{$root}skin/images/tab-corner-left.png" alt=""/>
            </td>
            <td class="tab" valign="bottom" rowspan="2">
              <div class="tab-not-selected-shadow">
                <div class="tab-not-selected">
                <xsl:call-template name="base-not-selected"/>
                </div>
              </div>              
            </td>
            <td height="5" width="5" valign="top" style="background-image: url({$root}skin/images/tab-right.png)">
              <img src="{$root}skin/images/tab-corner-right.png" alt=""/>
            </td>
          </tr>
          <tr>
            <td valign="bottom" style="background-image: url({$root}skin/images/tab-left.png)">
              <div class="tab-not-selected-shadow-left">
                <img src="{$root}skin/images/spacer.gif" width="4" alt=""/>
              </div>
            </td>
            <td valign="bottom" style="background-image: url({$root}skin/images/tab-right.png)">
              <div class="tab-not-selected-shadow-right">
                <img src="{$root}skin/images/spacer.gif" width="4" alt=""/>
              </div>
            </td>
          </tr>
        </table>
      </div>
    </td>
  </xsl:template>

  <!-- override for homepage: always first tab selected -->
  <xsl:template match="tab">
    <xsl:choose>
      <xsl:when test="@dir = $longest-dir or @href = $longest-dir
                      or $path='index' and @dir = /tabs/tab[1]/@dir">
        <xsl:call-template name="selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="not-selected"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Called from 'not-selected' -->
  <xsl:template name="base-not-selected">
    <a>
      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="$path = 'index'">
            <xsl:call-template name="unselected-tab-href">
              <xsl:with-param name="tab" select="."/>
              <xsl:with-param name="path" select="$path"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="unselected-tab-href">
              <xsl:with-param name="tab" select="."/>
              <xsl:with-param name="path" select="$path"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <font face="Arial, Helvetica, Sans-serif">
        <xsl:value-of select="@label"/>
      </font>
    </a>
  </xsl:template>

</xsl:stylesheet>
