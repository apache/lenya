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

<!-- $Id: form-layout.xsl,v 1.19 2004/06/29 09:36:10 michi Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
>

<xsl:param name="contextPrefix" select="'/lenya'"/>
<xsl:param name="edit" select="'No node selected yet'"/>
<xsl:param name="wfevent" select="'null'"/>

<xsl:variable name="imagesPath"><xsl:value-of select="$contextPrefix"/>/lenya/images/editor</xsl:variable>

<xsl:include href="copy-mixed-content.xsl"/>

<xsl:template match="form">
<page:page>
<page:title>Edit Document</page:title>
<page:body>
  
<div class="lenya-box">
  <div class="lenya-box-title">Information</div>
  <div class="lenya-box-body">
  
<table class="lenya-table-noborder">
  <tr>
    <td class="lenya-entry-caption">Document:</td>
    <td><xsl:value-of select="docid"/></td>
  </tr>
  <tr>
    <td class="lenya-entry-caption">Form:</td>
    <td><xsl:value-of select="ftype"/></td>
  </tr>
  <tr>
    <td class="lenya-entry-caption">Node:</td>
    <td><xsl:value-of select="$edit"/></td>
  </tr>
  <tr>
    <td class="lenya-entry-caption">Workflow Event:</td>
  <xsl:choose>
  <xsl:when test="$wfevent = '' or $wfevent = 'null'">
    <td>No workflow event specified</td>
  </xsl:when>
  <xsl:otherwise>
    <td><xsl:value-of select="$wfevent"/></td>
  </xsl:otherwise>
  </xsl:choose>
  </tr>

  <xsl:if test="message">
    <tr>
      <td valign="top" class="lenya-entry-caption"><span class="lenya-error">Message:</span></td>
      <td>
<font color="red">
        <xsl:value-of select="message"/>
</font>
        <br/><br/>
        (Check log files for more details: lenya/WEB-INF/logs/*)
      </td>
    </tr>
  </xsl:if>

</table>

</div>
</div>

<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close&amp;form={ftype}">
  <xsl:choose>
    <xsl:when test="$wfevent = '' or $wfevent = 'null'">
      <xsl:comment>No workflow event</xsl:comment>
    </xsl:when>
    <xsl:otherwise>
      <input type="hidden" name="lenya.event" value="{$wfevent}"/>
    </xsl:otherwise>
  </xsl:choose>
  
<div class="lenya-box">
  <div class="lenya-box-title" style="text-align: right">
    <input type="submit" value="SAVE" name="save"/>&#160;<input type="submit" value="CANCEL" name="cancel"/>
  </div>
  <div class="lenya-box-body">
  
  <table class="lenya-table">
    <xsl:apply-templates mode="nodes"/>
  </table>

  </div>
  <div class="lenya-box-title" style="text-align: right">
    <input type="submit" value="SAVE" name="save"/>&#160;<input type="submit" value="CANCEL" name="cancel"/>
  </div>
</div>
</form>

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

</page:body>
</page:page>
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
      <td style="background-color: #BFBFA2"><input type="submit" value="INSERT BEFORE" name="insert-before"/></td>
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
      <td style="background-color: #BFBFA2"><input type="submit" value="INSERT AFTER" name="insert-after"/></td>
      <td colspan="2" style="background-color: #DCDBBF">
        <select name="&lt;xupdate:insert-after select=&quot;{@select}&quot;/&gt;" size="1">
            <option value="null">Choose element ...</option>
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
  <!-- TODO: what about "input" field ... -->
  <input type="hidden" name="namespaces"><xsl:attribute name="value"><xsl:apply-templates select="textarea" mode="namespaces" /></xsl:attribute></input>
  <xsl:apply-templates select="textarea"/>
  <xsl:copy-of select="input"/>
</xsl:when>
<xsl:otherwise>
  <p>
    <xsl:value-of select="input/@value"/>
    <xsl:copy-of select="textarea/node()"/>
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
<input type="image" src="{$imagesPath}/edit.png" name="edit[{.}]" value="{.}"/>
</xsl:template>

</xsl:stylesheet>  
