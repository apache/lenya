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
  exclude-result-prefixes="lenya-info wf rc dc lenya-info"
  >
  
  <xsl:param name="contextprefix"/>
  <xsl:param name="lenya.usecase" select="'asset'"/>
  <xsl:param name="lenya.step"/>
  <xsl:param name="insert"/>
  <xsl:param name="insertimage"/> 
  <xsl:param name="insertTemplate"/>
  <xsl:param name="insertReplace"/>
 
  <xsl:param name="assetXPath"/>
  <xsl:param name="insertWhere"/>

  <xsl:param name="error"/>

  <xsl:param name="extensions" select="'doc dot rtf txt asc ascii xls xlw xlt ppt pot gif jpg png tif eps pct m3u kar mid smf mp3 swa mpg mpv mp4 mov bin sea hqx sit zip jmx jcl qz jbc jmt cfg pdf'"/>

  <xsl:template match="/lenya-info:info">
    <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
      <xsl:choose>
        <xsl:when test="$insert = 'true'">
          <xsl:choose>
            <xsl:when test="$insertimage = 'true'">
              <page:title><i18n:text>Insert Image</i18n:text></page:title>
            </xsl:when>
            <xsl:otherwise>
              <page:title><i18n:text>Insert Asset</i18n:text></page:title>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <page:title><i18n:text>New Asset</i18n:text></page:title>
        </xsl:otherwise>
      </xsl:choose>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="lenya-info:assets">
    
    <div class="lenya-box">
      <xsl:choose>
        <xsl:when test="$insert = 'true'">
          <xsl:choose>
            <xsl:when test="$insertimage = 'true'">
              <div class="lenya-box-title"><i18n:text>Insert a new Image</i18n:text></div>
            </xsl:when>
            <xsl:otherwise>
              <div class="lenya-box-title"><i18n:text>Insert a new Asset</i18n:text></div>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <div class="lenya-box-title"><i18n:text>Upload an Asset</i18n:text></div>
        </xsl:otherwise>
      </xsl:choose>
      <div class="lenya-box-body">
    <script type="text/javascript" src="{$contextprefix}/lenya/javascript/validation.js">&#160;</script>
    <script>
      var ext = '<xsl:value-of select="$extensions"/>';
    </script>  
        <form name="fileinput" action="{/lenya-info:info/lenya-info:assets/lenya-info:request-uri}" method="post" enctype="multipart/form-data" onsubmit="return check_upload(fileinput, ext)">
          <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
          <input type="hidden" name="lenya.event" value="edit"/>
          <xsl:choose>
            <xsl:when test="$insert = 'true'">
              <input type="hidden" name="lenya.step" value="upload-and-insert"/>
            </xsl:when>
            <xsl:otherwise>
              <input type="hidden" name="lenya.step" value="upload"/>
            </xsl:otherwise>
          </xsl:choose>
      <!-- some values appear twice, this is required for roundtripping. -->
          <input type="hidden" name="task-id" value="insert-asset"/>
          <input type="hidden" name="uploadtype" value="asset"/>
          <input type="hidden" name="properties.insert.asset.assetXPath" value="{$assetXPath}"/>
          <input type="hidden" name="assetXPath" value="{$assetXPath}"/>
          <input type="hidden" name="properties.insert.asset.insertWhere" value="{$insertWhere}"/>
          <input type="hidden" name="insertWhere" value="{$insertWhere}"/>
          <input type="hidden" name="properties.insert.asset.area" value="{/lenya-info:info/lenya-info:assets/lenya-info:area}"/>
          <input type="hidden" name="insert" value="{$insert}"/>
          <input type="hidden" name="insertimage" value="{$insertimage}"/>
          <input type="hidden" name="properties.insert.asset.document-id" value="{/lenya-info:info/lenya-info:assets/lenya-info:documentid}"/>
          <input type="hidden" name="properties.insert.asset.language" value="{/lenya-info:info/lenya-info:assets/lenya-info:language}"/>
          <input type="hidden" name="properties.asset.date" value="{/lenya-info:info/lenya-info:assets/lenya-info:date}"/>
          <input type="hidden" name="properties.insert.asset.insertTemplate" value="{$insertTemplate}"/>
          <input type="hidden" name="insertTemplate" value="{$insertTemplate}"/>
          <input type="hidden" name="properties.insert.asset.insertReplace" value="{$insertReplace}"/>
          <input type="hidden" name="insertReplace" value="{$insertReplace}"/>
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
              <td class="lenya-form-caption" style="vertical-align: top;">
                <i18n:translate>
                  <i18n:text key="select-object"/>
                  <i18n:param>
                    <i18n:text>
                      <xsl:choose>
                        <xsl:when test="$insertimage = 'true'">Image</xsl:when>
                            <xsl:otherwise>File</xsl:otherwise>
                      </xsl:choose>
                    </i18n:text>
                  </i18n:param>
                </i18n:translate>:
              </td>
              <td><input class="lenya-form-element" type="file" name="properties.asset.data"/><br/>(<i18n:text>No whitespace, no special characters</i18n:text>)</td>
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
            <tr><td>&#160;</td></tr>
            <xsl:if test="$insertimage = 'true'">
              <tr>
                <td class="lenya-form-caption"><i18n:text>Caption</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value=""/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Link</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.link"/><br/>External links have to start with 'http://', internal links have to start with '/'</td>
              </tr>
            </xsl:if>
            <tr><td>&#160;</td></tr>
            <tr>
              <td/>
              <td>
                <input i18n:attr="value" type="submit" value="Submit"/>&#160;
                <input i18n:attr="value" type="button" onClick="location.href='{/lenya-info:info/lenya-info:assets/lenya-info:request-uri}?lenya.usecase=checkin&amp;lenya.step=checkin&amp;backup=false';" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
    
    <xsl:choose>
      <xsl:when test="$insert = 'true'">
        <div class="lenya-box">
          <xsl:choose>
            <xsl:when test="$insertimage = 'true'">
              <div class="lenya-box-title"><i18n:text>Insert an existing Image</i18n:text></div>
            </xsl:when>
            <xsl:otherwise>
              <div class="lenya-box-title"><i18n:text>Insert an existing Asset</i18n:text></div>
            </xsl:otherwise>
          </xsl:choose>
          <div class="lenya-box-body">
            <form method="GET"
              action="">
              <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
              <input type="hidden" name="lenya.step" value="insert"/>
              <input type="hidden" name="lenya.event" value="edit"/>
              <input type="hidden" name="task-id" value="insert-asset"/>
              <input type="hidden" name="properties.insert.asset.assetXPath" value="{$assetXPath}"/>
              <input type="hidden" name="properties.insert.asset.insertWhere" value="{$insertWhere}"/>
              <input type="hidden" name="properties.insert.asset.insertTemplate" value="{$insertTemplate}"/>
              <input type="hidden" name="properties.insert.asset.insertReplace" value="{$insertReplace}"/>
              <input type="hidden" name="properties.insert.asset.area" value="{/lenya-info:info/lenya-info:assets/lenya-info:area}"/>
              <input type="hidden" name="properties.insert.asset.document-id" value="{/lenya-info:info/lenya-info:assets/lenya-info:documentid}"/>
              <input type="hidden" name="properties.insert.asset.language" value="{/lenya-info:info/lenya-info:assets/lenya-info:language}"/>

              <table class="lenya-table-noborder">
                <xsl:if test="not(lenya-info:asset)">
                  <tr><td colspan="5" class="lenya-form-caption"><i18n:text>No assets available</i18n:text></td></tr>
                </xsl:if>
                <tr>
                  <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td>
                  <td colspan="4">
                    <input id="assetTitle" class="lenya-form-element" type="text" name="properties.asset.title" value=""/>
                    <input type="hidden" id="assetSource" name="properties.asset.data" value=""/>
                    <input type="hidden" id="assetExtent" name="extent" value=""/>
                  </td>
                </tr>
                <xsl:if test="$insertimage = 'true'">
                  <tr>
                    <td class="lenya-form-caption"><i18n:text>Caption</i18n:text>:</td>
                    <td colspan="4">
                      <input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value="Default Caption"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="lenya-form-caption"><i18n:text>Link</i18n:text>:</td>
                    <td colspan="4">
                      <input class="lenya-form-element" type="text" name="properties.insert.asset.link"/>
                      <br/>
                      <xsl:text>External links have to start with 'http://', internal links have to start with '/'</xsl:text>
                    </td>
                  </tr>
                </xsl:if>
                <xsl:apply-templates select="lenya-info:asset"/>
                <tr>
                  <td/>
                  <td colspan="4">
                    <input i18n:attr="value" type="submit" value="Submit"/>&#160;
                    <input i18n:attr="value" type="button" onClick="location.href='{/lenya-info:info/lenya-info:assets/lenya-info:request-uri}?lenya.usecase=checkin&amp;lenya.step=checkin&amp;backup=false';" value="Cancel"/>
                  </td>
                </tr>
              </table>
      
              <!--
              <table class="lenya-table-noborder">
                <tr>
                  <td class="lenya-form-caption">
                  <xsl:choose>
                    <xsl:when test="$insertimage = 'true'"><i18n:text>Image</i18n:text>:</xsl:when>
                    <xsl:otherwise><i18n:text>File</i18n:text>:</xsl:otherwise></xsl:choose>
                  </td>
                  <td class="lenya-form-caption">
                    <xsl:apply-templates select="lenya-info:asset"/>
                  </td>
                </tr>
                <tr><td>&#160;</td></tr>
                <tr>
                  <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title" value=""/></td>
                </tr>
                <xsl:if test="$insertimage = 'true'">
                  <tr>
                    <td class="lenya-form-caption"><i18n:text>Caption</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value="Default Caption"/></td>
                  </tr>
                  <tr>
                    <td class="lenya-form-caption"><i18n:text>Link</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.link"/><br/>External links have to start with 'http://', internal links have to start with '/'</td>
                  </tr>
                </xsl:if>
                <tr><td>&#160;</td></tr>
                <tr>
                  <td/>
                  <td>
                    <input i18n:attr="value" type="submit" value="Submit"/>&#160;
                    <input i18n:attr="value" type="button" onClick="location.href='{/usecase:asset/usecase:request-uri}?lenya.usecase=checkin&amp;lenya.step=checkin&amp;backup=false';" value="Cancel"/>
                  </td>
                </tr>
              </table>
              -->
            </form>
          </div>
        </div>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

<xsl:template match="lenya-info:asset">
  <xsl:if test="$insertimage != 'true' or (contains(dc:source, '.jpg') or contains(dc:source, '.gif') or contains(dc:source, '.png') or contains(dc:source, '.swf'))">
  <tr>
    <td/>
    <td>
      <input type="radio" name="asset"
        onclick="document.getElementById('assetTitle').value = '{dc:title}';
                 document.getElementById('assetSource').value = '{dc:source}';
                 document.getElementById('assetExtent').value = '{dc:extent}';"/>
    </td>
    <td><xsl:value-of select="dc:title"/></td>
    <td><xsl:value-of select="dc:extent"/> KB</td>
    <td><xsl:value-of select="dc:date"/></td>
  </tr>
  </xsl:if>
</xsl:template>
  
  
</xsl:stylesheet>  
