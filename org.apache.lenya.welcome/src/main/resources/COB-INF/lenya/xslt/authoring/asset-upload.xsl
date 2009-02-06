<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
    exclude-result-prefixes="lenya-info wf rc dc usecase page i18n"
    >

<xsl:param name="lenya.usecase" select="'asset'"/>
<xsl:param name="lenya.step"/>
<xsl:param name="error"/>
<xsl:param name="extensions" select="'doc dot rtf txt asc ascii xls xlw xlt ppt pot gif jpg png tif eps pct m3u kar mid smf mp3 swa mpg mpv mp4 mov bin sea hqx sit zip jmx jcl qz jbc jmt cfg pdf'"/>

  <xsl:param name="contextprefix"/>

<xsl:template match="/lenya-info:info">
  <page:page>
    <page:title><i18n:text key="lenya.assetupload.subtitle"/></page:title>
    <page:body>
      <xsl:apply-templates select="lenya-info:assets"/>
    </page:body>
  </page:page>
</xsl:template>


<xsl:template match="lenya-info:assets">
  <xsl:call-template name="pre-body"/>
  <xsl:call-template name="upload-form"/>
  <xsl:call-template name="library-form"/>
</xsl:template>


<!--
Override this template to add scripts etc.
-->
<xsl:template name="pre-body"/>


<xsl:template name="upload-form">
  <div class="lenya-box">
    <div class="lenya-box-title"><i18n:text key="lenya.assetupload.subtitle"/></div>
    <div class="lenya-box-body">
      <form name="fileinput" id="fileinput" action="" method="post" enctype="multipart/form-data" onsubmit="return check_upload(fileinput, ext)">
        <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
        <input type="hidden" name="lenya.step" value="asset-upload"/>
        <input type="hidden" name="task-id" value="insert-asset"/>
        <input type="hidden" name="uploadtype" value="asset"/>
        <input type="hidden" name="properties.asset.date" value="{/lenya-info:info/lenya-info:assets/lenya-info:date}"/>
        <input type="hidden" name="properties.insert.asset.document-uuid" value="{/lenya-info:info/lenya-info:assets/lenya-info:document-uuid}"/>
        <input type="hidden" name="properties.insert.asset.language" value="{/lenya-info:info/lenya-info:assets/lenya-info:language}"/>
        <table class="lenya-table-noborder">
          <xsl:if test="$error = 'true'">
            <tr>
              <td colspan="2" class="lenya-form-caption">
                <span class="lenya-form-message-error"><i18n key="filename-format-exception"/></span>
              </td>
            </tr>
          </xsl:if>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Select File</i18n:text>:</td><td><input class="lenya-form-element" type="file" name="properties.asset.data" 
                onchange="imagepreview(this)"/><br/>(<i18n:text>No whitespace, no special characters</i18n:text>)</td>
          </tr>
          <tr><td>&#160;</td></tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Title</i18n:text>*:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
          </tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Creator</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator" value="{/lenya-info:info/lenya-info:assets/lenya-info:creator}"/></td>
          </tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Rights</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights" value="All rights reserved."/></td>
          </tr>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Preview</i18n:text>:</td><td><img src="" id="preview" style="visibility: hidden; height: 100px;"/></td>
          </tr>
          <tr><td>&#160;</td></tr>
          <tr>
            <td/>
            <td>
              <input i18n:attr="value" type="submit" value="Add" name="input-add"/>&#160;
              <input i18n:attr="value" type="button" onClick="location.href='javascript:window.close();';" value="Cancel" name="input-cancel"/>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</xsl:template>


<xsl:template name="library-form">
  <div class="lenya-box">
    <div class="lenya-box-title"><i18n:text>Asset Library</i18n:text></div>
    <div class="lenya-box-body">
      <form name="assetlibrary" id="assetlibrary" action="">
        <table class="lenya-table-noborder">
          <xsl:if test="not(lenya-info:asset)">
            <tr><td colspan="5" class="lenya-form-caption"><i18n:text>No assets available</i18n:text></td></tr>
          </xsl:if>
          <tr>
            <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td>
            <td colspan="4">
              <input id="assetTitle" class="lenya-form-element" type="text" name="properties.insert.asset.title" value=""/>
              <input type="hidden" id="assetSource" name="properties.asset.data" value=""/>
              <input type="hidden" id="assetExtent" name="extent" value=""/>
            </td>
          </tr>
          <xsl:apply-templates select="lenya-info:asset"/>
          <tr>
            <td/>
            <td colspan="4">
              <xsl:call-template name="library-buttons"/>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</xsl:template>


<xsl:template match="lenya-info:asset">
  <tr>
    <td/>
    <td>
      <input type="radio" name="asset"
        onclick="document.getElementById('assetTitle').value = '{dc:title}';
                 document.getElementById('assetSource').value = '{dc:source}';
                 document.getElementById('assetExtent').value = '{dc:extent}';"/>
    </td>
    <td>
        <xsl:if test="dc:format = 'image/jpeg' or dc:format = 'image/gif' or  dc:format = 'image/png'">
            <img src="{../lenya-info:documentnodeid}/{dc:source}" style="height: 32px; vertical-align: middle;"/>&#160;
        </xsl:if>
        <xsl:value-of select="dc:title"/>
    </td>
    <td><xsl:value-of select="dc:extent"/> KB</td>
    <td><xsl:value-of select="dc:date"/></td>
  </tr>
</xsl:template>


<xsl:template name="library-buttons"/>


</xsl:stylesheet>
