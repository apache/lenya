<?xml version="1.0" encoding="UTF-8" ?>
<!--
##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################

XSL transformation from Kupu Library XML to HTML for the image library
drawer.

$Id: imagedrawer.xsl 4105 2004-04-21 23:56:13Z guido $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:tal="http://xml.zope.org/namespaces/tal" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://xml.zope.org/namespaces/i18n">
    <xsl:param name="drawertype">image</xsl:param>
    <xsl:param name="drawertitle">Image Drawer</xsl:param>
    <xsl:param name="showupload"></xsl:param>
    <xsl:param name="usecaptions"></xsl:param>
    <xsl:variable name="titlelength" select="20"/>
    <xsl:template match="/">
        <html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>
                    <xsl:value-of select="$drawertitle"/>
                </title>
                <link type="text/css" rel="stylesheet">
                    <xsl:attribute name="href">kupudrawerstyles.css </xsl:attribute>
                </link>
            </head>
            <body>
                <div style="width: 500px; border: solid black 1px; width: 100px">
                    <div id="kupu-librarydrawer">
                        <h1 style="padding: 0;float: left;">
                            <xsl:value-of select="$drawertitle"/>
                        </h1>
                        <div id="kupu-searchbox" style="text-align: right">
                            <form onsubmit="return false;">
                                <input id="kupu-searchbox-input"
                                    name="searchbox" value="search"
                                    style="font-style: italic"
                                    onclick="if (this.value == 'search') this.value = ''; this.style.fontStyle='normal';" onkeyup="if (event.keyCode == 13 ) drawertool.current_drawer.search();"/>
                            </form>
                        </div>
                        <div class="kupu-panels">
                            <table>
                                <tr class="kupu-panelsrow">
                                    <td id="kupu-librariespanel" class="panel">
                                        <div id="kupu-librariesitems" class="overflow">
                                        <xsl:apply-templates select="/libraries/library"/>
                                        </div>
                                    </td>
                                    <td id="kupu-resourcespanel" class="panel">
                                        <div id="kupu-resourceitems" class="overflow">
                                        <xsl:apply-templates select="/libraries/*[@selected]/items"/>
                                        </div>
                                    </td>
                                    <td id="kupu-propertiespanel" class="panel">
                                        <div id="kupu-properties" class="overflow">
                                        <xsl:choose>
                                        <xsl:when test="$drawertype='image'">
                                            <xsl:if test="//resource[@selected]">
                                                <xsl:apply-templates
                                                select="/libraries/*[@selected]//resource[@selected]" mode="image-properties"/>
                                            </xsl:if>
                                            <!-- use image upload template -->
                                            <xsl:if test="$showupload='yes'">
                                                <xsl:apply-templates select="/libraries/*[@selected]//uploadbutton" mode="image-upload"/>
                                            </xsl:if>
                                        </xsl:when>
                                        <xsl:when test="$drawertype='link'">
                                        <xsl:apply-templates
                                        select="/libraries/*[@selected]//resource[@selected]" mode="link-properties"/>
                                        </xsl:when>
                                        </xsl:choose>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div class="kupu-dialogbuttons">                            
                            <button type="button" onclick="drawertool.current_drawer.save();">Ok</button>
                            <button type="button" onclick="drawertool.closeDrawer();">Cancel</button>
                        </div>
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="library">
        <div onclick="drawertool.current_drawer.selectLibrary('{@id}');"
            class="kupu-libsource" title="{title}" style="">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <div>
                <xsl:apply-templates select="icon"/>
            </div>
            <span class="drawer-item-title">
                <xsl:value-of select="title"/>
            </span>
        </div>
    </xsl:template>
    <xsl:template match="items">
        <xsl:apply-templates select="collection|resource|uploadbutton" mode="currentpanel"/>
    </xsl:template>
    <xsl:template match="resource|collection" mode="currentpanel">
        <div id="{@id}" class="kupu-{local-name()}" title="{description}">
            <xsl:attribute name="onclick">
                <xsl:choose>
                    <xsl:when
                            test="local-name()='collection'">drawertool.current_drawer.selectCollection('<xsl:value-of select="@id"/>');</xsl:when>

                    <xsl:otherwise>drawertool.current_drawer.selectItem('<xsl:value-of select="@id"/>')</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="icon"/>
            <xsl:apply-templates select="(label|title)[1]"/>
        </div>
    </xsl:template>
    <xsl:template match="uploadbutton" mode="currentpanel">
        <div class="kupu-upload">
            <xsl:attribute name="onclick">
                drawertool.current_drawer.selectUpload();
            </xsl:attribute>
            <span class="drawer-item-title">Upload ...</span>
        </div>
    </xsl:template>
    <xsl:template match="icon">
        <img src="{.}" alt="{../title}">
            <xsl:attribute name="class">library-icon-<xsl:value-of select="local-name(..)"/>
            </xsl:attribute>
        </img>
    </xsl:template>
    <xsl:template match="label|title">
        <span class="drawer-item-title">
            <xsl:if test="../@selected">
                <xsl:attribute name="style">background-color: #C0C0C0</xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="string-length() &gt; $titlelength">
                    <xsl:value-of select="substring(., 0, $titlelength)"/>... </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </span>
    </xsl:template>
    <xsl:template match="resource|collection" mode="image-properties">
        <div>
            <xsl:value-of select="title"/>
        </div>
        <xsl:choose>
            <xsl:when test="width">
                <div>
                    <xsl:variable name="h" select="number(height) div 120"/>
                    <xsl:variable name="w" select="number(width) div 100"/>
                    <xsl:choose>
                        <xsl:when test="($h&gt;$w) and $h&gt;1">
                            <img src="{uri}" title="{title}" height="120"
                                width="{width div $h}" alt="{title}"/>
                        </xsl:when>
                        <xsl:when test="($w&gt;$h) and $w&gt;1">
                            <img src="{uri}" title="{title}"
                                height="{height div $w}" width="100" alt="{title}"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <img src="{uri}" title="{title}" height="{height}"
                                width="{width}" alt="{title}"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
            </xsl:when>
            <xsl:when test="preview">
                <tr>
                    <td>
                        <strong>Preview</strong>
                        <br/>
                        <img src="{preview}" title="{title}" height="{height}"
                            width="{width}" alt="{title}"/>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>
        <div>
            <xsl:value-of select="size"/>
            <xsl:if test="width"> (<xsl:value-of select="width"/> by
                    <xsl:value-of select="height"/>)</xsl:if>
        </div>
        <div>
            <xsl:value-of select="description"/>
        </div>
        <div>
            <form onsubmit="return false;">
                <strong>ALT-text</strong>
                <br/>
                <input type="text" id="image_alt" size="20" value="{title}"/>
                <br/>
                <input type="radio" name="image-align" id="image-align-left"
                    checked="checked" value="image-left"/>
                <label for="image-align-left">Left</label>
                <input type="radio" name="image-align" id="image-align-inline" value="image-inline"/>
                <label for="image-align-inline">Inline</label>
                <input type="radio" name="image-align" id="image-align-right" value="image-right"/>
                <label for="image-align-right">Right</label>
                <xsl:if test="$usecaptions='yes'">
                    <br/>
                    <input type="checkbox" name="image-caption"
                        id="image-caption" checked="checked"/>
                    <label for="image-caption">Caption</label>
                </xsl:if>
            </form>
        </div>
    </xsl:template>
    <xsl:template match="resource|collection" mode="link-properties">
        <form onsubmit="return false;">
            <table>
                <tr class="kupu-linkdrawer-title-row">
                    <td>
                        <strong>Title</strong>
                        <br/>
                        <xsl:value-of select="title"/>
                    </td>
                </tr>
                <tr class="kupu-linkdrawer-description-row">
                    <td>
                        <strong>Description</strong>
                        <br/>
                        <xsl:value-of select="description"/>
                    </td>
                </tr>
                <tr class="kupu-linkdrawer-name-row">
                    <td>
                        <strong>Name</strong>
                        <br/>
                        <input type="text" id="link_name" size="10"/>
                    </td>
                </tr>
                <tr class="kupu-linkdrawer-target-row">
                    <td>
                        <strong>Target</strong>
                        <br/>
                        <input type="text" id="link_target" value="_self" size="10"/>
                    </td>
                </tr>
            </table>
        </form>
    </xsl:template>
    
    <!-- image upload form -->
    <xsl:template match="uploadbutton" mode="image-upload">
        <div id="kupu-upload-instructions" i18n:translate="upload-instructions">
            Select an image from your computer and click ok to have it automatically uploaded to selected folder and inserted into the editor.
        </div><br/>
        <form name="kupu_upload_form" method="POST" action="" scrolling="off" target="kupu_upload_form_target"
              enctype="multipart/form-data" style="margin: 0; border: 0;">

            <span id="kupu-upload-to"><strong>Upload to: </strong> <xsl:value-of select="/libraries/*[@selected]/title"/> </span><br/>
            <input id="kupu-upload-file" type="file" name="node_prop_image" /><br/>
            <label>Title: 
                <input id="kupu-upload-title" type="text" name="node_prop_caption" size="23" value=""/>
            </label><br/>
            <input type="reset" i18n:translate="upload-resetform" value="Clear"/>

        </form>

        <iframe id="kupu-upload-form-target" name="kupu_upload_form_target"
                src="kupublank.html" scrolling="off" frameborder="0" width="0px" height="0px" display="None">
        </iframe>
    </xsl:template>
    
</xsl:stylesheet>
