<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: image.xsl,v 1.1 2004/02/04 20:31:16 gregor Exp $
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0" 
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
    <xsl:param name="extensions" 
        select="'doc,dot,rtf,txt,asc,ascii,xls,xlw,xlt,ppt,pot,gif,jpg,png,tif,eps,pct,mu3,kar,mid,smf,mp3,swa,mpg,mpv,mp4,mov,bin,sea,hqx,sit,zip,jmx,jcl,qz,jbc,jmt,cfg'"/>
    <xsl:template match="lenya-info:assets">
        <page:page>
            <page:title>Insert Image</page:title>
            <page:body >
                <script> 
                   window.onload = insertCaption
                   function insertImage(src) { 
                      var link = document.forms["image"].link.value;
                      var caption = document.forms["image"].caption.value;
                      var title = document.forms["image"].title.value;
                      var content = '<object xmlns="'+window.opener.XHTMLNS+'" href="'+link+'" data="{lenya-info:documentnodeid}/'+src+'">'+caption+'</object>'; 
                      window.opener.bxe_insertContent(content,window.opener.bxe_ContextNode); 
                      window.close();
                   }

                   function insertCaption() { 
                    var selectionContent = window.opener.getSelection().getEditableRange().toString(); 
                    if (selectionContent.length != 0) { 
                      document.forms["image"].caption.value = selectionContent;
                    } 
                    focus(); 
                  } 

                  function check(fileinput) {
                    var i = 0;
                    var ext = '<xsl:value-of select="$extensions"/>';
                    var delimiter = ','; 
                    var thefile = fileinput["properties.asset.data"].value; 
                    var _tempArray = new Array();
                    _tempArray = ext.split(delimiter);
                    for(i in _tempArray) { 
                      if(thefile.indexOf('.' + _tempArray[i]) != -1) { // file has one of the accepted extensions. 
                           return true; 
                      } 
                     } // file does not have one of the accepted extensions. 
                     alert("You tried to upload a file with an invalid extension. Valid extensions are <xsl:value-of select="$extensions"/>"); 
                     return false;
                  } 
                </script>
                <div class="lenya-box">
                    <div class="lenya-box-title">Add to Asset Library</div>
                    <form name="fileinput" 
                        action="{/usecase:asset/usecase:request-uri}" 
                        method="post" enctype="multipart/form-data" 
                        onsubmit="return check(fileinput)">
                        <input type="hidden" name="lenya.usecase" 
                            value="{$lenya.usecase}"/>
                        <input type="hidden" name="lenya.step" 
                            value="image-upload"/>
                        <input type="hidden" name="uploadtype" value="asset"/>
                        <table class="lenya-table-noborder">
                            <xsl:if test="$error = 'true'">
                                <tr>
                                    <td colspan="2" class="lenya-form-caption">
                                        <span class="lenya-form-message-error">
                                            The file name of the file you are 
                                            trying to upload either has no 
                                            extension, or contains characters 
                                            which are not allowed, such as 
                                            spaces or umlauts. </span>
                                    </td>
                                </tr>
                            </xsl:if>
                            <tr>
                                <td class="lenya-form-caption">Select 
                                    Image:</td>
                                <td><input class="lenya-form-element" 
                                    type="file" name="properties.asset.data" 
                                    id="data"/><br/>(No whitespace, no special 
                                    characters)</td>
                            </tr>
                            <tr>
                                <td class="lenya-form-caption">Title:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="properties.asset.title"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="lenya-form-caption">Creator:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="properties.asset.creator" 
                                        value="{/usecase:asset/usecase:creator}"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="lenya-form-caption">Rights:</td>
                                <td>
                                    <input class="lenya-form-element" 
                                        type="text" 
                                        name="properties.asset.rights"
                                        value="All rights reserved."/>
                                </td>
                            </tr>
                            <tr>
                                <td>&#160;</td>
                            </tr>
                            <tr>
                                <td/>
                                <td> <input type="submit" 
                                    value="Submit"/>&#160; <input type="button" 
                                    onClick="location.href='{/usecase:asset/usecase:request-uri}';" 
                                    value="Cancel"/> </td>
                            </tr>
                        </table>
                    </form>
                </div>
                <div class="lenya-box">
                    <div class="lenya-box-title">Asset Library</div>
                    <form id="image">
                        <table class="lenya-table-noborder">
                            <xsl:choose>
                                <xsl:when test="not(lenya-info:asset)">
                                    <tr>
                                        <td colspan="5" 
                                            class="lenya-form-caption">
                                            <xsl:value-of select="dc:title"/>No 
                                            Images available</td>
                                    </tr>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:for-each select="lenya-info:asset">
                                        <xsl:choose>
                                            <xsl:when 
                                                test="contains(dc:title, 'jpg') or contains(dc:title, 'gif')">
                                                <tr>
                                                    <td 
                                                        class="lenya-form-caption">
                                                        <xsl:value-of 
                                                            select="dc:title"/>
                                                    </td>
                                                    <td 
                                                        class="lenya-form-caption">
                                                        <xsl:value-of 
                                                            select="dc:relation"/>
                                                    </td>
                                                    <td 
                                                        class="lenya-form-caption">
                                                         <xsl:value-of 
                                                        select="dc:extent"/> 
                                                        kB</td>
                                                    <td 
                                                        class="lenya-form-caption">
                                                        <xsl:value-of 
                                                            select="dc:date"/>
                                                    </td>
                                                    <td 
                                                        class="lenya-form-caption">
                                                        <a 
                                                            href="javascript:insertImage('{dc:title}');">
                                                             Insert</a>
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
                                                    select="dc:title"/>No 
                                                    Images available</td>
                                            </tr>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <tr>
                                                <td class="lenya-form-caption" 
                                                    colspan="5">&#160;</td>
                                            </tr>
                                            <tr>
                                                <td class="lenya-form-caption"> 
                                                    Title:</td>
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
                                                    Caption:</td>
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
                                                    Link:</td>
                                                <td colspan="4" 
                                                    class="lenya-form-caption"> 
                                                    <input 
                                                    class="lenya-form-element" 
                                                    type="text" 
                                                    name="link"/><br/>External 
                                                    links have to start with 
                                                    'http://', internal links 
                                                    have to start with '/'</td>
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