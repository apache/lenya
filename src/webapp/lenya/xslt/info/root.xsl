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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0" 
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  >
  
  <xsl:param name="contextprefix"/>
  <xsl:param name="publicationid"/>
  <xsl:param name="area"/>
  <xsl:param name="tab"/>
  <xsl:param name="documentid"/>
  <xsl:param name="documentextension"/>
  <xsl:param name="documenturl"/>
  <xsl:param name="languages"/>
  <xsl:param name="chosenlanguage"/>
  <xsl:param name="defaultlanguage"/>
  
  <xsl:variable name="extension"><xsl:if test="$documentextension != ''">.</xsl:if><xsl:value-of select="$documentextension"/></xsl:variable>
  
<!-- Decide whether to load the sitetree incrementally. 
     true:  The sitetree.js will only contain the root node of the tree.  
            All other nodes will be loaded dynamically by tree.js when needed.
            Useful for large trees.
     false: The sitetree.js will contain the whole sitetree structure and tree.js 
            won't load anything dynamically. Useful for small trees or for browsers
            which don't support xmlhttp requests. 
-->
  <xsl:variable name="incremental-loading" select="'true'"/>

   <xsl:template match="/">
    <html>
      <head>
        
        <link href="{$contextprefix}/lenya/css/default.css" rel="stylesheet" type="text/css"/>
        
        <!-- These three scripts define the tree, do not remove-->
        <script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/ua.js"/>
        <script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/tree.js"/>
        <script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/sitetree.js?language={$chosenlanguage}&amp;incremental={$incremental-loading}"/>
      </head>

      <body>
        <div id="lenya-info-body">
          <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td valign="top" width="20%">
                <div id="lenya-info-treecanvas">
                  <!-- Build the tree. -->
                  
                  <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <xsl:call-template name="languagetabs">
                        <xsl:with-param name="tablanguages">
                          <xsl:value-of select="$languages"/>
                        </xsl:with-param>
                      </xsl:call-template>
                    </tr>
                  </table>
                  
                  <div id="lenya-info-tree">
                    <div style="display:none;">
                      <table border="0">
                        <tr>
                          <td>
                            <a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/" target="_blank">JavaScript Tree Menu</a>
                          </td>
                        </tr>
                      </table>
                    </div>
                    <xsl:variable name="language-suffix"><xsl:if test="$chosenlanguage != $defaultlanguage">_<xsl:value-of select="$chosenlanguage"/></xsl:if></xsl:variable>
                    <xsl:variable name="url">
                      <xsl:value-of select="concat($contextprefix, '/', $publicationid, '/info-', $area, $documentid, $language-suffix, $extension)"/>
                    </xsl:variable>
                      <script>
                      initializeDocument('<xsl:value-of select="$area"/>', '<xsl:value-of select="$documentid"/>');
                      <xsl:variable name="language-suffix"><xsl:if test="$chosenlanguage != $defaultlanguage">_<xsl:value-of select="$chosenlanguage"/></xsl:if></xsl:variable>
                      loadSynchPage('<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/info-<xsl:value-of select="$area"/><xsl:value-of select="$documentid"/><xsl:value-of select="$language-suffix"/><xsl:value-of select="$extension"/>');
                    </script>
                  </div>
                </div>
              </td>     
              <td valign="top" width="80%">
                <div id="lenya-info-content">
                  <!-- support both old-style tabs and new-style JX tabs -->
                  <xsl:choose>
                    <xsl:when test="/page:page">
                      <xsl:copy-of select="page:page/node()"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:copy-of select="*"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="activate">
    <xsl:param name="tablanguage"/>
    <xsl:variable name="docidwithoutlanguage"><xsl:value-of select="substring-before($documentid, '_')"/></xsl:variable>
    <xsl:attribute name="href"><xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/info-<xsl:value-of select="$area"/><xsl:value-of select="$documentid"/>_<xsl:value-of select="$tablanguage"/><xsl:value-of select="$extension"/>?lenya.usecase=tab.overview</xsl:attribute>
    <xsl:attribute name="class">lenya-tablink<xsl:choose><xsl:when test="$chosenlanguage = $tablanguage">-active</xsl:when><xsl:otherwise/></xsl:choose></xsl:attribute><xsl:value-of select="$tablanguage"/>
  </xsl:template>
  
  <xsl:template name="selecttab">
    <xsl:text>?lenya.usecase=info-</xsl:text>
    <xsl:choose>
      <xsl:when test="$tab"><xsl:value-of select="$tab"/></xsl:when>
      <xsl:otherwise>overview</xsl:otherwise>
    </xsl:choose>
    <xsl:text>&amp;lenya.step=showscreen</xsl:text>
  </xsl:template>

  <xsl:template name="languagetabs">
    <xsl:param name="tablanguages"/>
    <xsl:choose>
      <xsl:when test="not(contains($tablanguages,','))">
        <xsl:call-template name="languagetab">
          <xsl:with-param name="tablanguage">
            <xsl:value-of select="$tablanguages"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="head">
          <xsl:value-of select = "substring-before($tablanguages,',')" />
        </xsl:variable>
        <xsl:variable name="tail">
          <xsl:value-of select = "substring-after($tablanguages,',')" />
        </xsl:variable>
        <xsl:call-template name="languagetab">
          <xsl:with-param name="tablanguage"><xsl:value-of select="$head"/></xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="languagetabs">
          <xsl:with-param name="tablanguages"><xsl:value-of select="$tail"/></xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="languagetab">
    <xsl:param name="tablanguage"/>
    <td><a id="{$tablanguage}">
        <xsl:call-template name="activate">
          <xsl:with-param name="tablanguage"><xsl:value-of select="$tablanguage"/></xsl:with-param>
        </xsl:call-template>
      </a></td>
  </xsl:template>

</xsl:stylesheet> 