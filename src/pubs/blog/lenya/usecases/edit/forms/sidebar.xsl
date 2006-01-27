<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id: sidebar.xsl 42702 2004-03-13 12:34:18Z gregor $ -->
                                                                
<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="../../../../../../usecases/edit/forms/form.xsl"/>

<xsl:template match="sidebar">
 <xsl:apply-templates select="block"/>

  <node name="Add Block">
    <action><insert name="&lt;xupdate:append select=&quot;/sidebar&quot;&gt;&lt;xupdate:element name=&quot;block&quot;&gt;&lt;title&gt;New title&lt;/title&gt;&lt;content&gt;New content&lt;/content&gt;&lt;/xupdate:element&gt;&lt;/xupdate:append&gt;"/></action>
  </node>
</xsl:template>

<xsl:template match="block">
  <node name="Add Block">
    <action><insert name="&lt;xupdate:insert-before select=&quot;/sidebar/block[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;block&quot;&gt;&lt;title&gt;New title&lt;/title&gt;&lt;content&gt;New content&lt;/content&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-before&gt;"/></action>
  </node>
  <node name="Delete Block">
    <action><delete name="&lt;xupdate:remove select=&quot;/sidebar/block[@tagID='{@tagID}']&quot;/&gt;"/></action>
  </node>
  <node name="Title" select="/sidebar/block/title[@tagID='{title/@tagID}']">
    <content type="plain"><input type="text" name="&lt;xupdate:update select=&quot;/sidebar/block/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title"/></xsl:attribute></input></content>
  </node>
  <node name="Content" select="/sidebar/block/content[@tagID='{content/@tagID}']">
    <content type="mixed">
      <textarea name="&lt;xupdate:update select=&quot;/sidebar/block/content[@tagID='{content/@tagID}']&quot;&gt;" cols="40" rows="3">
        <xsl:copy-of select="content/node()"/>
      </textarea>
    </content>
 </node> 
</xsl:template>
 
</xsl:stylesheet>
