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

<!-- $Id: entry.xsl 42702 2004-03-13 12:34:18Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:import href="../../../../../../usecases/edit/forms/form.xsl"/>

<xsl:template match="echo:entry">
<node name="Title" select="/echo:entry/echo:title[@tagID='{echo:title/@tagID}']">
<!-- FIXME: In the case of text input field, < and > need to be replaced by &lt; and &gt;
  <content><input type="text" name="&lt;xupdate:update select=&quot;/echo:entry/echo:title[@tagID='{echo:title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:apply-templates select="echo:title/node()" mode="mixed"/></xsl:attribute></input></content>
-->
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/echo:entry/echo:title[@tagID='{echo:title/@tagID}']&quot;&gt;" cols="40" rows="1">
      <xsl:copy-of select="echo:title/node()"/>
<!--
      <xsl:apply-templates select="echo:title/node()" mode="mixedcontent"/>
-->
    </textarea>
  </content>
</node>

<xsl:if test="not(echo:summary)">
<node name="Summary">
  <action><insert name="&lt;xupdate:insert-after select=&quot;/echo:entry/echo:title[@tagID='{echo:title/@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;echo:summary&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;New summary&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;"/></action>
</node>
</xsl:if>

<xsl:apply-templates select="echo:summary"/>
<xsl:apply-templates select="echo:content"/>

<node name="Content Block (application/xhtml+xml)">
  <action><insert name="&lt;xupdate:append select=&quot;/echo:entry&quot;&gt;&lt;xupdate:element name=&quot;echo:content&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;&lt;xupdate:attribute name=&quot;type&quot;&gt;application/xhtml+xml&lt;/xupdate:attribute&gt;&lt;p&gt;Append new content&lt;/p&gt;&lt;/xupdate:element&gt;&lt;/xupdate:append&gt;"/></action>
</node>
<!--
<node name="Content (text/plain as CDATA)">
  <action><insert name="&lt;xupdate:append select=&quot;/echo:entry&quot;&gt;&lt;xupdate:element name=&quot;echo:content&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;&lt;xupdate:attribute name=&quot;type&quot;&gt;text/plain&lt;/xupdate:attribute&gt;New CDATA content&lt;/xupdate:element&gt;&lt;/xupdate:append&gt;"/></action>
</node>
-->
</xsl:template>

<xsl:template match="echo:summary">
<node name="Summary" select="/echo:entry/echo:summary[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/echo:entry/echo:summary[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/echo:entry/echo:summary[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="5">
      <xsl:copy-of select="node()"/>
<!--
      <xsl:apply-templates mode="mixedcontent"/>
-->
    </textarea>
  </content>
</node>
</xsl:template>


<xsl:template match="echo:content[@type='text/plain']">
<node name="Content (text/plain as CDATA)">
  <action><insert name="&lt;xupdate:insert-before select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;echo:content&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;&lt;xupdate:attribute name=&quot;type&quot;&gt;text/plain&lt;/xupdate:attribute&gt;New content&lt;/xupdate:element&gt;&lt;/xupdate:insert-before&gt;"/></action>
</node>
<node name="Content (text/plain as CDATA)">
  <action><delete name="&lt;xupdate:remove select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content><textarea name="&lt;xupdate:update select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;&gt;&lt;![CDATA[" cols="40" rows="5"><xsl:value-of select="."/></textarea></content>
</node>
</xsl:template>


<xsl:template match="echo:content[@type='application/xhtml+xml']">
<node name="Content Block (application/xhtml+xml)">
  <action><insert name="&lt;xupdate:insert-before select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;echo:content&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;&lt;xupdate:attribute name=&quot;type&quot;&gt;application/xhtml+xml&lt;/xupdate:attribute&gt;&lt;p&gt;Insert before new content&lt;/p&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-before&gt;"/></action>
</node>
<node name="Content Block (application/xhtml+xml)" select="/echo:entry/echo:content[@tagID='{@tagID}']">
  <action><delete name="&lt;xupdate:remove select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;/&gt;"/></action>
  <content>
    <textarea name="&lt;xupdate:update select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="5">
      <xsl:copy-of select="node()"/>
<!--
      <xsl:apply-templates mode="mixedcontent"/>
-->
    </textarea>
  </content>
</node>
</xsl:template>


<xsl:template match="echo:content">
<node name="Content (Either no @type attribute or no xsl:template with such a @type attribute!)">
<content><xsl:apply-templates/></content>
</node>
</xsl:template>
 
</xsl:stylesheet>  
