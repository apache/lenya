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
  >

  <xsl:import href="../util/waitScreen.xsl"/>

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
  <xsl:param name="cutdocumentid"/>
  
  <xsl:variable name="extension"><xsl:if test="$documentextension != ''">.</xsl:if><xsl:value-of select="$documentextension"/></xsl:variable>
  
  <xsl:template match="/">
    <html>
      <head>
        
        <link href="{$contextprefix}/lenya/css/default.css" rel="stylesheet" type="text/css"/>
        <!-- These three scripts define the tree, do not remove-->
        <script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/tree.js" type="text/javascript" />
        <script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/navtree.js" type="text/javascript" />
        <script type="text/javascript" >
          CONTEXT_PREFIX = "<xsl:value-of select="$contextprefix"/>";
          PUBLICATION_ID = "<xsl:value-of select="$publicationid"/>";
          CHOSEN_LANGUAGE = "<xsl:value-of select="$chosenlanguage"/>";
          DEFAULT_LANGUAGE = "<xsl:value-of select="$defaultlanguage"/>";
          IMAGE_PATH = "<xsl:value-of select="$contextprefix"/>/lenya/images/tree/";
          CUT_DOCUMENT_ID = "<xsl:value-of select="$cutdocumentid"/>";
          ALL_AREAS = "authoring,trash,archive"
          PIPELINE_PATH = '/authoring/info-sitetree/sitetree-fragment.xml'
          
          function buildTree() {
            var placeholder = document.getElementById('tree');
            var root = new NavRoot(document, placeholder);
            root.init(PUBLICATION_ID);
            root.render();
            root.loadInitialTree('<xsl:value-of select="$area"/>', '<xsl:value-of select="$documentid"/>');
          };
       
         </script>
         <xsl:call-template name="wait_script"/>   

      </head>

      <body>
        <div id="lenya-info-body">
          <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td valign="top" width="25%">
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
                    <div id="tree">
                      <script type="text/javascript">
                        buildTree();
                      </script>
                    </div>
                  </div>
                </div>
              </td>     
              <td valign="top" width="75%">
                <div id="lenya-info-content">
                  <xsl:copy-of select="*"/>
                </div>
              </td>
            </tr>
          </table>
        </div>
        <xsl:call-template name="wait_screen"/>   
      </body>
    </html>
  </xsl:template>

  <xsl:template name="activate">
    <xsl:param name="tablanguage"/>
    <xsl:variable name="docidwithoutlanguage"><xsl:value-of select="substring-before($documentid, '_')"/></xsl:variable>
    <xsl:attribute name="href"><xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/info-<xsl:value-of select="$area"/><xsl:value-of select="$documentid"/>_<xsl:value-of select="$tablanguage"/><xsl:value-of select="$extension"/>?lenya.usecase=info-overview&amp;lenya.step=showscreen</xsl:attribute>
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