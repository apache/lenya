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

<!-- $Id: tab2menu.xsl,v 1.3 2004/03/13 12:42:18 gregor Exp $ -->

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
</xsl:template>

<xsl:template name="selected">
  <td><img src="{$root}skin/images/current/mainmenu-left.jpeg"/></td>
  <td background="{$root}skin/images/current/mainmenu-background.jpeg">
    &#160;<xsl:call-template name="base-selected"/>&#160;
  </td>
  <td><img src="{$root}skin/images/current/mainmenu-right.jpeg"/></td>
</xsl:template>

<xsl:template name="not-selected">
  <td><img src="{$root}skin/images/default/mainmenu-left.jpeg"/></td>
  <td background="{$root}skin/images/default/mainmenu-background.jpeg">
    &#160;<xsl:call-template name="base-not-selected"/>&#160;
  </td>
  <td><img src="{$root}skin/images/default/mainmenu-right.jpeg"/></td>
</xsl:template>

</xsl:stylesheet>
