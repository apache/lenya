<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: cmfs.xsl,v 1.12 2004/05/07 23:41:59 michi Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="system">
<node name="Project Name" select="/system/system_name[@tagID='{system_name/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/system_name[@tagID='{system_name/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="system_name" /></xsl:attribute></input></content>
</node>

<xsl:apply-templates select="editor"/>

<node name="Description" select="/system/description[@tagID='{description/@tagID}']">
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/system/description[@tagID='{description/@tagID}']&quot;&gt;" cols="40" rows="5">
      <xsl:copy-of select="description/node()"/>
<!--
      <xsl:apply-templates select="description/node()" mode="mixedcontent"/>
-->
    </textarea>
  </content>
</node>

<node name="Home" select="/system/main_url[@tagID='{main_url/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/main_url[@tagID='{main_url/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="main_url" /></xsl:attribute></input></content>
</node>

<node name="License Name" select="/system/license/license_name[@tagID='{license/license_name/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/license/license_name[@tagID='{license/license_name/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="license/license_name" /></xsl:attribute></input></content>
</node>

<node name="License URL" select="/system/license/license_url[@tagID='{license/license_url/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/license/license_url[@tagID='{license/license_url/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="license/license_url" /></xsl:attribute></input></content>
</node>


<xsl:if test="not(programming-language)">
<node name="Programming Language">
  <action><insert name="&lt;xupdate:insert-after select=&quot;/system/license&quot;&gt;&lt;xupdate:element name=&quot;programming-language&quot;&gt;Enter your religion&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/></action>
</node>
</xsl:if>
<xsl:apply-templates select="programming-language"/>


<xsl:if test="not(related-info)">
<node name="Related Information">
  <action><insert name="&lt;xupdate:insert-after select=&quot;/system/system_name&quot;&gt;&lt;xupdate:element name=&quot;related-info&quot;&gt;&lt;info-item&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;uri&gt;http://&lt;/uri&gt;&lt;/info-item&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/></action>
</node>
</xsl:if>
<xsl:apply-templates select="related-info"/>


<xsl:if test="not(features)">
<node name="Features">
  <action><insert name="&lt;xupdate:insert-after select=&quot;/system/system_name&quot;&gt;&lt;xupdate:element name=&quot;features&quot;&gt;&lt;feature&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;description&gt;New Description&lt;/description&gt;&lt;/feature&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/></action>
</node>
</xsl:if>
<xsl:apply-templates select="features"/>
</xsl:template>


<xsl:template match="editor">
<node name="Editor Name" select="/system/editor/name[@tagID='{name/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/editor/name[@tagID='{name/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="name" /></xsl:attribute></input></content>
</node>

<xsl:if test="not(email)">
<node name="Editor E-Mail">
  <action>
    <insert name="&lt;xupdate:append select=&quot;/system/editor&quot;&gt;&lt;xupdate:element name=&quot;email&quot;&gt;editor@apache.org&lt;/xupdate:element&gt;&lt;/xupdate:append&gt;"/>
  </action>
</node>
</xsl:if>
<xsl:apply-templates select="email"/>


<xsl:choose>
<xsl:when test="@homepage">
<node name="Editor Homepage" select="/system/editor[@tagID='{@tagID}']/@homepage">
  <action>
    <delete name="&lt;xupdate:remove select=&quot;/system/editor[@tagID='{@tagID}']/@homepage&quot;/&gt;"/>
  </action>
  <content>
    <input type="text" name="&lt;xupdate:update select=&quot;/system/editor[@tagID='{@tagID}']/@homepage&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="@homepage" /></xsl:attribute></input>
  </content>
</node>
</xsl:when>
<xsl:otherwise>
<!--
Append attribute
-->
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="email">
<node name="Editor E-Mail" select="/system/editor/email[@tagID='{@tagID}']">
  <action>
    <delete name="&lt;xupdate:remove select=&quot;/system/editor/email[@tagID='{@tagID}']&quot;/&gt;"/>
  </action>
  <content>
    <input type="text" name="&lt;xupdate:update select=&quot;/system/editor/email[@tagID='{@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute></input></content>
</node>
</xsl:template>



<xsl:template match="programming-language">
<node name="Programming Language" select="/system/programming-language[@tagID='{@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/programming-language[@tagID='{@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute></input></content>
</node>
</xsl:template>

<xsl:template match="related-info">
<node name="Related Information">
</node>
<xsl:apply-templates select="info-item"/>
</xsl:template>

<xsl:template match="info-item">
<node name="Info">
  <action><delete value="true" name="&lt;xupdate:remove select=&quot;/system/related-info/info-item[@tagID='{@tagID}']&quot;/&gt;"/></action>
</node>
<node name="Title" select="/system/related-info/info-item/title[@tagID='{title/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/related-info/info-item/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></content>
</node>
<node name="URL" select="/system/related-info/info-item/uri[@tagID='{uri/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/related-info/info-item/uri[@tagID='{uri/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="uri" /></xsl:attribute></input></content>
</node>
<node name="Info">
  <action><insert name="&lt;xupdate:insert-after select=&quot;/system/related-info/info-item[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;info-item&quot;&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;uri&gt;http://&lt;/uri&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/></action>
</node>
</xsl:template>


<xsl:template match="features">
<node name="Features">
</node>
<xsl:apply-templates select="feature"/>
</xsl:template>

<xsl:template match="feature">
<node name="Feature">
  <action><delete value="true" name="&lt;xupdate:remove select=&quot;/system/features/feature[@tagID='{@tagID}']&quot;/&gt;"/></action>
</node>
<node name="Feature Title" select="/system/features/feature/title[@tagID='{title/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/system/features/feature/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></content>
</node>
<node name="Feature Description" select="/system/features/feature/description[@tagID='{description/@tagID}']">
  <content><textarea name="&lt;xupdate:update select=&quot;/system/features/feature/description[@tagID='{description/@tagID}']&quot;&gt;" cols="40" rows="3"><xsl:value-of select="description" /></textarea></content>
</node>
<node name="Feature">
  <action><insert name="&lt;xupdate:insert-after select=&quot;/system/features/feature[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;feature&quot;&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;description&gt;New Description&lt;/description&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/></action>
</node>
</xsl:template>
 
</xsl:stylesheet>  
