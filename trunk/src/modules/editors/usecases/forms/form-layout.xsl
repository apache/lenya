<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: form-layout.xsl 155267 2005-02-24 22:41:27Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

<xsl:param name="nodeid" select="''"/>

<xsl:param name="contextPrefix" select="'/lenya'"/>
<xsl:param name="edit" select="'No node selected yet'"/>
<xsl:param name="wfevent" select="'null'"/>

<xsl:variable name="imagesPath"><xsl:value-of select="$contextPrefix"/>/lenya/images/editor</xsl:variable>

<xsl:include href="copy-mixed-content.xsl"/>
  
  
  <xsl:template name="buttons">
    <input type="submit" value="Save" name="submit" i18n:attr="value"/>
    <xsl:text> </xsl:text>
    <input type="submit" value="Cancel" name="cancel" i18n:attr="value"/>
  </xsl:template>


<xsl:template match="form">
<div>
<div class="lenya-box">
  <div class="lenya-box-title" style="text-align: right">
    <input type="hidden" name="namespaces">
      <xsl:attribute name="value">
        <xsl:for-each select="//namespace">
          <xsl:text>xmlns:</xsl:text>
          <xsl:value-of select="@prefix"/>="<xsl:value-of select="@uri"/>"
          <xsl:text> </xsl:text>
        </xsl:for-each>
        <xsl:apply-templates select="//*" mode="namespaces" />
      </xsl:attribute>
    </input>
    <xsl:call-template name="buttons"/>
  </div>
  <div class="lenya-box-body">
  
  <table class="lenya-table">
    <xsl:apply-templates mode="nodes"/>
  </table>

  </div>
  <div class="lenya-box-title" style="text-align: right">
    <xsl:call-template name="buttons"/>
  </div>
</div>

<div class="lenya-box">
  <div class="lenya-box-title"><a href="http://www.w3.org/TR/REC-xml#syntax">Predefined Entities</a></div>
  <div class="lenya-box-body">
<ul>
<li>&amp;lt; instead of &lt; (left angle bracket <strong>must</strong> be escaped)</li>
<li>&amp;amp; instead of &amp; (ampersand <strong>must</strong> be escaped)</li>
<li>&amp;gt; instead of > (right angle bracket)</li>
<li>&amp;apos; instead of ' (single-quote)</li>
<li>&amp;quot; instead of " (double-quote)</li>
</ul>
</div>
</div>

 <xsl:variable name="currentTagID">
    <xsl:value-of select="substring-before(substring-after($edit, &quot;@tagID='&quot;), &quot;'&quot;)"/>
  </xsl:variable>
  <xsl:if test="$currentTagID != ''">
    <script type="text/javascript">

      function addLoadEvent(func) {
        var oldonload = window.onload;
        if (typeof window.onload != 'function') {
          window.onload = func;
        } else {
          window.onload = function() {
            oldonload();
            func();
          }
        }
      }

     addLoadEvent(goAnchor);

      function goAnchor() {
         document.location.hash = '<xsl:value-of select="$currentTagID"/>';
         window.scrollBy(0, -150);
      }
    </script>
  </xsl:if>

</div>
</xsl:template>

<xsl:template match="namespace" mode="nodes">
  <input type="hidden" name="namespace.{@prefix}" value="{@uri}"/>
</xsl:template>

<xsl:template match="node" mode="nodes">
<tr>
  <td valign="top" style="background-color: #BFBFA2"><xsl:apply-templates select="action"/><xsl:if test="not(action)">&#160;</xsl:if><xsl:apply-templates select="@select"/></td>
  <xsl:choose>
    <xsl:when test="content">
      <td valign="top" style="background-color:#DCDBBF"><xsl:apply-templates select="@name"/></td>
      <td valign="top"><xsl:apply-templates select="content"/></td>
    </xsl:when>
    <xsl:otherwise>
      <td colspan="2" valign="top" style="background-color:#DCDBBF"><xsl:apply-templates select="@name"/></td>
    </xsl:otherwise>
  </xsl:choose>
</tr>
</xsl:template>

<xsl:template match="insert-before" mode="nodes">
    <tr>
      <td style="background-color: #BFBFA2"><input type="submit" value="Insert before" name="insert-before" i18n:attr="value"/></td>
      <td colspan="2" style="background-color: #DCDBBF">
        <select name="&lt;xupdate:insert-before select=&quot;{@select}&quot;/&gt;" size="1">
            <option value="null">Choose element ...</option>
          <xsl:for-each select="element">
            <option value="{@xupdate}"><xsl:value-of select="@name"/></option>
          </xsl:for-each>
        </select>
      </td>
    </tr>
</xsl:template>

<xsl:template match="insert-after" mode="nodes">
    <tr>
      <td style="background-color: #BFBFA2"><input type="submit" value="Insert" name="insert-after" i18n:attr="value"/></td>
      <td colspan="2" style="background-color: #DCDBBF">
        <select name="&lt;xupdate:insert-after select=&quot;{@select}&quot;/&gt;" size="1">
          <option value="null"><i18n:text>Choose element ...</i18n:text></option>
          <xsl:for-each select="element">
            <option value="{@xupdate}"><xsl:value-of select="@name"/></option>
          </xsl:for-each>
        </select>
      </td>
    </tr>
</xsl:template>

<xsl:template match="node()" mode="nodes" priority="-1">
</xsl:template>

<xsl:template match="action">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="content">
<xsl:choose>
<xsl:when test="$edit = ../@select">
  <xsl:apply-templates select="textarea"/>
  <xsl:copy-of select="input"/>
</xsl:when>
<xsl:otherwise>
  <p>
    <xsl:choose>
      <xsl:when test="(../@name='Object')">
        <img src="{$nodeid}/{input/@value}"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="input/@value"/>
        <xsl:copy-of select="textarea/node()"/>
      </xsl:otherwise>
    </xsl:choose>
  </p>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="textarea">
<xsl:copy>
  <xsl:copy-of select="@*"/>
  <xsl:apply-templates mode="mixedcontent"/>
</xsl:copy>
</xsl:template>

<xsl:template match="insert">
<!--
<input type="submit" name="{@name}" value="INSERT"/>
-->
<xsl:text> </xsl:text>
<input type="image" src="{$imagesPath}/add.png" name="{@name}" value="LENYA"/>
</xsl:template>

<xsl:template match="delete">
<xsl:text> </xsl:text>
<input type="image" src="{$imagesPath}/delete.png" name="{@name}" value="true"/>
</xsl:template>

<xsl:template match="@select">
<xsl:text> </xsl:text>
<!-- FIXME: Internet Explorer does not send the value of input type equals image. Mozilla does. -->

<xsl:variable name="tagID">
    <xsl:value-of select="substring-before(substring-after(., &quot;@tagID='&quot;), &quot;'&quot;)"/>
</xsl:variable>
<xsl:value-of select="@tagID"/>
<a name="{$tagID}"/>
<input type="image" src="{$imagesPath}/edit.png" name="edit[{.}]" value="{.}"/>
</xsl:template>

</xsl:stylesheet>  
