<?xml version="1.0" encoding="UTF-8"?>
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

<!--
 $Id: link.xsl,v 1.12 2004/08/25 14:48:22 andreas Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"     
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
    
<xsl:template match="/">
    <page:page>
      <page:title>Insert Link</page:title>
      <page:body>
      <script type="text/javascript" src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/ua.js">&#160;</script>
      <script type="text/javascript" src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/tree.js">&#160;</script>
      <script type="text/javascript" src="{$contextprefix}/{$publicationid}/{$area}/{$documenturl}?lenya.usecase=bxeng&amp;lenya.step=link-tree&amp;language={$chosenlanguage}">&#160;</script>
      <script> 
          var url;
          window.onload = insertText

          function insertText() { 
            var selectionContent = window.opener.getSelection().getEditableRange().toString(); 
            if (selectionContent.length != 0) { 
                document.forms["link"].text.value = selectionContent;
            } 
            focus(); 
          } 

          function setLink(src) { 
            url = src;
            document.forms["link"].url.value = url;
          }
          
          function insertLink() { 
          var text = document.forms["link"].text.value;
          var title = document.forms["link"].title.value;
          var prefix ='<xsl:value-of select="$contextprefix"/>' + '/<xsl:value-of select="$publicationid"/>' + '/<xsl:value-of select="$area"/>';
          var url = document.forms["link"].url.value;
          if (url.charAt(0) == "/") {
           // prepend hostname etc for internal links
           url = prefix + url;
          }
          <![CDATA[
          var content = '<a xmlns="'+window.opener.XHTMLNS+'" href="'+url+'" title="'+title+'">'+text+'</a>'; 
          ]]>
          window.opener.bxe_insertContent(content,window.opener.bxe_ContextNode); 
          window.close();
          }
      </script>
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
                                      <a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/">JavaScript Tree Menu</a>
                              </td>
                      </tr>
              </table>
      </div>
      <script type="text/javascript">
         initializeDocument();
      </script>
    </div>
</div>
</td>
<td>
 <form action="" name="link" onsubmit="insertLink()">
                        <table class="lenya-table-noborder">
                                <tr>
                                <td colspan="2" class="lenya-form-caption">You can either click on a node in the tree for an internal link or enter a link in the URL field. </td>
                                </tr>
                            <tr>
                                <td colspan="2">&#160;</td>
                            </tr>
                                <tr>
                                <td class="lenya-form-caption">URL:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="url"/>
                                </td>
                            </tr>
                                 <tr>
                                <td class="lenya-form-caption">Title:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="title"/>
                                </td>
                            </tr>
                                 <tr>
                                <td class="lenya-form-caption">Link text:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="text"/>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">&#160;</td>
                            </tr>
                            <tr>
                                <td/>
                                <td> <input type="submit" 
                                    value="Insert"/>
                                </td>
                            </tr>
                        </table>
 </form>   
</td>
</tr></table>
      </page:body>
    </page:page>
</xsl:template>


<xsl:template name="selecttab">
  <xsl:text>?lenya.usecase=</xsl:text>
  <xsl:choose>
    <xsl:when test="$tab"><xsl:value-of select="$tab"/></xsl:when>
    <xsl:otherwise>bxeng</xsl:otherwise>
  </xsl:choose>
  <xsl:text>&amp;lenya.step=link-show</xsl:text>
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
      <xsl:variable name="head" select="substring-before($tablanguages,',')" />
      <xsl:variable name="tail" select="substring-after($tablanguages,',')" />
      <xsl:call-template name="languagetab">
        <xsl:with-param name="tablanguage" select="$head"/>
      </xsl:call-template>
      <xsl:call-template name="languagetabs">
        <xsl:with-param name="tablanguages" select="$tail"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="languagetab">
  <xsl:param name="tablanguage"/>
  <td><a id="{$tablanguage}">
      <xsl:call-template name="activate">
        <xsl:with-param name="tablanguage" select="$tablanguage"/>
      </xsl:call-template>
    </a></td>
</xsl:template>


<xsl:template name="activate">
  <xsl:param name="tablanguage"/>
  <xsl:attribute name="href"><xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/><xsl:value-of select="$documentid"/>_<xsl:value-of select="$tablanguage"/><xsl:value-of select="$extension"/>?lenya.usecase=bxeng&amp;lenya.step=link-show</xsl:attribute>
  <xsl:attribute name="class">lenya-tablink<xsl:choose><xsl:when test="$chosenlanguage = $tablanguage">-active</xsl:when><xsl:otherwise/></xsl:choose></xsl:attribute><xsl:value-of select="$tablanguage"/>
</xsl:template>

</xsl:stylesheet> 