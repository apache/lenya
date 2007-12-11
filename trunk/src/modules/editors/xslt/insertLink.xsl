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

<!--
  $Id: link.xsl 555656 2007-07-12 15:17:47Z nettings $
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"     
  >
  
  <xsl:param name="publicationId"/>
  <xsl:param name="area"/>
  <xsl:param name="documentPath"/>
  <xsl:param name="documentLanguage"/>
  <xsl:param name="documentExtension"/>
  <xsl:param name="defaultLanguage"/>
  <xsl:param name="languages"/>
  <xsl:param name="areaBasePath"/>
  
  <xsl:variable name="extension">
    <xsl:if test="$documentExtension != ''">
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:value-of select="$documentExtension"/>
  </xsl:variable>
  
  <xsl:template match="/">
    <page:page>
      <page:title><i18n:text>insertLink.heading</i18n:text></page:title>
      <page:body style="width:auto;">

        <script type="text/javascript" src="/modules/sitetree/javascript/tree.js">&#160;</script>
        <script type="text/javascript" src="/modules/sitetree/javascript/lenyatree.js">&#160;</script>
        <script type="text/javascript" src="/modules/sitetree/javascript/navtree.js">&#160;</script>
        <script type="text/javascript" src="/modules/editors/javascript/org.apache.lenya.editors.js">&#160;</script>
        <script type="text/javascript" src="/modules/editors/javascript/insertLink.js">&#160;</script>
        <script type="text/javascript">
          AREA = "<xsl:value-of select="$area"/>";
          DOCUMENT_ID = "<xsl:value-of select="$documentPath"/>";
          PUBLICATION_ID = "<xsl:value-of select="$publicationId"/>";
          CHOSEN_LANGUAGE = "<xsl:value-of select="$documentLanguage"/>";
          DEFAULT_LANGUAGE = "<xsl:value-of select="$defaultLanguage"/>";
          IMAGE_PATH = "/lenya/images/tree/";
          CUT_DOCUMENT_ID = '';
          ALL_AREAS = "authoring";
          PIPELINE_PATH = '/sitetree-fragment.xml';
          AREA_BASE_PATH = "<xsl:value-of select="$areaBasePath"/>";
        </script>

        <div id="lenya-info-treecanvas" style="width: 250px;">
          <div class="lenya-tabs">
            <xsl:call-template name="languageTabs">
              <xsl:with-param name="languages" select="$languages"/>
            </xsl:call-template>
          </div>
          <div id="lenya-info-tree">
            <div id="tree">&#160;
            </div>
          </div>
        </div>
        <div class="lenya-box" style="margin-left: 260px;">
          <div class="lenya-box-title"><i18n:text>insertLink.heading</i18n:text></div>
          <div class="lenya-box-body">

            <form name="insertLink">

              <table class="lenya-table-noborder">
                <tr>
                  <td colspan="2" class="lenya-form-caption">
                    <i18n:text>insertLink.clickTreeOrType</i18n:text>
                  </td>
                </tr>
                <tr>
                  <td colspan="2">&#160;</td>
                </tr>
                <tr>
                  <td class="lenya-form-caption">
                    <i18n:text>insertLink.URL</i18n:text>:
                  </td>
                  <td>
                    <input class="lenya-form-element" type="text" name="url"/>
                  </td>
                </tr>
                <tr>
                  <td class="lenya-form-caption">
                    <i18n:text>insertLink.title</i18n:text>:
                  </td>
                  <td>
                    <input class="lenya-form-element" type="text" name="title"/>
                  </td>
                </tr>
                <tr>
                  <td class="lenya-form-caption">
                    <i18n:text>insertLink.text</i18n:text>:</td>
                  <td>
                    <input class="lenya-form-element" type="text" name="text"/>
                  </td>
                </tr>
                <tr>
                  <td colspan="2">&#160;</td>
                </tr>
                <tr>
                  <td colspan="2">
                    <input 
                      i18n:attr="value" 
                      type="submit" 
                      value="insertLink.submit" 
                      onclick="org.apache.lenya.editors.handleFormSubmit('insertLink')"
                    />
                    &#160;
                    <input 
                      i18n:attr="value"
                      type="submit"
                      value="Cancel" 
                      name="cancel"
                      onclick="window.close()"
                    />
                    &#160;
                    <input 
                      i18n:attr="value" 
                      type="button" 
                      value="insertAsset.createResource" 
                      onclick="location.href='?lenya.usecase=editors.createResource&amp;doctype=resource&amp;lenya.exitUsecase=editors.insertLink'"
                    />
                  </td>
                </tr>
              </table>
            </form>
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template name="languageTabs">
    <xsl:param name="languages"/>
    <xsl:choose>
      <xsl:when test="not(contains($languages,','))">
        <xsl:call-template name="languageTab">
          <xsl:with-param name="language">
            <xsl:value-of select="$languages"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="head" select="substring-before($languages,',')" />
        <xsl:variable name="tail" select="substring-after($languages,',')" />
        <xsl:call-template name="languageTab">
          <xsl:with-param name="language" select="$head"/>
        </xsl:call-template>
        <xsl:call-template name="languageTabs">
          <xsl:with-param name="languages" select="$tail"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="languageTab">
    <xsl:param name="language"/>
    <a id="{$language}">
      <xsl:attribute name="href">
        <xsl:text>/</xsl:text>
        <xsl:value-of select="$publicationId"/>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="$area"/>
        <xsl:value-of select="$documentPath"/>
        <xsl:text>_</xsl:text>
        <xsl:value-of select="$language"/>
        <xsl:value-of select="$extension"/>
        <xsl:text>?lenya.usecase=editors.insertLink</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:text>lenya-tablink</xsl:text>
        <xsl:if test="$documentLanguage = $language">
          <xsl:text>-active</xsl:text>
        </xsl:if>
      </xsl:attribute>
      <xsl:value-of select="$language"/>
    </a>
  </xsl:template>

</xsl:stylesheet>
