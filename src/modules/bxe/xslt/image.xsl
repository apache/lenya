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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0" 
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0" 
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns="http://www.w3.org/1999/xhtml" 
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0" 
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0" 
    xmlns:xhtml="http://www.w3.org/1999/xhtml" >
    <xsl:param name="lenya.usecase" select="'asset'"/>
    <xsl:param name="lenya.step"/>
    <xsl:variable name="noimages"/>
    <xsl:param name="error"/>

    <xsl:param name="extensions" select="'gif jpg png swf'"/>
    <xsl:param name="contextprefix"/>

    <xsl:template match="lenya-info:assets">
        <page:page>
            <page:title>
              <i18n:text key="lenya.imageupload.title"/>
            </page:title>
            <page:body >
                <script type="text/javascript" src="{$contextprefix}/lenya/javascript/validation.js">&#160;</script>
                <script type="text/javascript" src="{$contextprefix}/lenya/javascript/asset.js">&#160;</script>
                <script> 
                   window.onload = insertCaption
                   
                   ext = '<xsl:value-of select="$extensions"/>';
                   
                   function insertImage(src, type) { 
                      var nodeid = '<xsl:value-of select="lenya-info:documentnodeid"/>/';
                      var link = document.forms['image'].link.value;
                      var caption = document.forms['image'].caption.value;
                      var title = document.forms['image'].title.value;
                      <![CDATA[
                      var content = '<object xmlns="'+window.opener.XHTMLNS+'" href="'+link+'" title="'+title+'" type="'+type+'" data="'+nodeid + src+'">'+caption+'</object>'; 
                      ]]>
                      window.opener.bxe_insertContent(content,window.opener.bxe_ContextNode); 
                      window.close();
                   }

                   function insertCaption() { 
                    var selectionContent = window.opener.getSelection().getEditableRange().toString(); 
                    if (selectionContent.length != 0) { 
                      document.forms['image'].caption.value = selectionContent;
                    } 
                    focus(); 
                  } 
                </script>
                <div class="lenya-box">
                    <div class="lenya-box-title"><i18n:text key="lenya.assetupload.subtitle"/></div>
                    <form name="fileinput" id="fileinput"
                        action="" 
                        method="post" enctype="multipart/form-data" 
                        onsubmit="return check_upload(fileinput, ext)">
                        <input type="hidden" name="lenya.usecase" 
                            value="{$lenya.usecase}"/>
                        <input type="hidden" name="lenya.step" 
                            value="image-upload"/>
                        <input type="hidden" name="uploadtype" value="asset"/>
                        <input type="hidden" name="properties.asset.date" value="{/lenya-info:info/lenya-info:assets/lenya-info:date}"/>
                        <table class="lenya-table-noborder">
                            <xsl:if test="$error = 'true'">
                                <tr>
                                    <td colspan="2" class="lenya-form-caption">
                                        <span class="lenya-form-message-error">
                                          <i18n:text key="filename-format-exception"/>
                                        </span>
                                    </td>
                                </tr>
                            </xsl:if>
                            <tr>
                                <td class="lenya-form-caption"><i18n:text key="lenya.imageupload.selectimage.label"/>:</td>
                                <td><input class="lenya-form-element" 
                                    type="file" name="properties.asset.data" onchange="imagepreview(this)" 
                                    id="data"/><br/>(<i18n:text>No whitespace, no special characters</i18n:text>)</td>
                            </tr>
                            <tr>
                                <td class="lenya-form-caption"><i18n:text>Title</i18n:text>*:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="properties.asset.title"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="lenya-form-caption"><i18n:text>Creator</i18n:text>:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="properties.asset.creator" 
                                        value="{/lenya-info:info/lenya-info:assets/lenya-info:creator}"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="lenya-form-caption"><i18n:text>Rights</i18n:text>:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        i18n:attr="value"
                                        type="text" 
                                        name="properties.asset.rights"
                                        value="All rights reserved"/>
                                </td>
                            </tr>
                          <tr>
                            <td class="lenya-form-caption"><i18n:text>Preview</i18n:text>:</td><td><img src="" id="preview" style="visibility: hidden; height: 100px;"/></td>
                          </tr>
                            <tr>
                                <td>&#160;</td>
                            </tr>
                            <tr>
                                <td/>
                                <td> <input type="submit" 
                                    i18n:attr="value"
                                    value="Add" name="input-add"/>&#160; <input type="button" 
                                    i18n:attr="value"
                                    onClick="location.href='javascript:window.close();';" 
                                    value="Cancel" name="input-cancel"/> </td>
                            </tr>
                        </table>
                    </form>
                </div>
                <div class="lenya-box">
                    <div class="lenya-box-title"><i18n:text>Asset Library</i18n:text></div>
                    <form id="image">
                        <table class="lenya-table-noborder">
                            <xsl:choose>
                                <xsl:when test="not(lenya-info:asset)">
                                    <tr>
                                        <td colspan="5" 
                                            class="lenya-form-caption">
                                            <xsl:value-of select="dc:title"/><i18n:text key="lenya.imageupload.info.noimages"/></td>
                                    </tr>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:for-each select="lenya-info:asset">
                                        <xsl:choose>
                                            <xsl:when 
                                                test="dc:format = 'image/jpeg' or dc:format = 'image/gif' or  dc:format = 'image/png' or dc:format = 'application/x-shockwave-flash'">
                                                <tr>
                                                    <td>
                                                        <img src="{../lenya-info:documentnodeid}/{dc:source}"        
                                                          onclick="javascript:insertImage('{dc:source}', '{dc:format}');"
                                                          style="cursor: pointer; height: 32px; vertical-align: middle;"/>
                                                    </td>
                                                    <td 
                                                        colspan="2">
                                                        <xsl:value-of 
                                                            select="dc:title"/>
                                                    </td>
                                                    <td>
                                                         <xsl:value-of 
                                                        select="dc:extent"/> 
                                                        kB</td>
                                                    <td>
                                                        <xsl:value-of select="dc:date"/>
                                                    </td>
                                                </tr>
                                                <xsl:variable name="noimages">
                                                    false</xsl:variable>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:variable name="noimages">
                                                    true</xsl:variable>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:for-each>
                                    <xsl:choose>
                                        <xsl:when test="$noimages = 'true'">
                                            <tr>
                                                <td colspan="5" 
                                                    class="lenya-form-caption"> 
                                                    <xsl:value-of 
                                                    select="dc:title"/><i18n:text key="lenya.imageupload.info.noimages"/></td>
                                            </tr>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <tr>
                                                <td class="lenya-form-caption" 
                                                    colspan="5">&#160;</td>
                                            </tr>
                                            <tr>
                                                <td class="lenya-form-caption"> 
                                                    <i18n:text>Title</i18n:text>:</td>
                                                <td colspan="4" 
                                                    class="lenya-form-caption">
                                                    <input 
                                                        class="lenya-form-element" 
                                                        type="text" 
                                                        name="title"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lenya-form-caption"> 
                                                    <i18n:text>Caption</i18n:text>:</td>
                                                <td colspan="4" 
                                                    class="lenya-form-caption">
                                                    <input 
                                                        class="lenya-form-element" 
                                                        type="text" 
                                                        name="caption" 
                                                        />
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="lenya-form-caption"> 
                                                    <i18n:text>Link</i18n:text>:</td>
                                                <td colspan="4" 
                                                    class="lenya-form-caption"> 
                                                    <input 
                                                    class="lenya-form-element" 
                                                    type="text" 
                                                    name="link"/><br/><i18n:text key="lenya.imageupload.links.hint"/></td>
                                            </tr>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:otherwise>
                            </xsl:choose>
                        </table>
                    </form>
                </div>
            </page:body>
        </page:page>
    </xsl:template>
</xsl:stylesheet>