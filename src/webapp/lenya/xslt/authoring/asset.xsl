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

<!-- $Id: asset.xsl,v 1.38 2004/03/24 12:08:07 roku Exp $ -->

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
  exclude-result-prefixes="lenya-info wf rc dc usecase"
  >
  
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

  <xsl:template match="/">
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
	  <page:title><i18n:text>Asset Upload</i18n:text></page:title>
	</xsl:otherwise>
      </xsl:choose>
      <page:body>
	<xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="usecase:asset">
    
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
<script>
function validContent(formField,fieldLabel)
{
	var result = true;
	
	if (formField.value.match("[ ]*"))
	{
		alert('Filenames cannot contain spaces.');
		formField.focus();
		result = false;
	}
	
	return result;
}

function check(fileinput) {
  var i = 0;
  var ext = '<xsl:value-of select="$extensions"/>';
  var delimiter = ' ';
  var thefile = fileinput["properties.asset.data"].value;
  var _tempArray = new Array();
  _tempArray = ext.split(delimiter);
  for(i in _tempArray)
  {
    if(thefile.indexOf('.' + _tempArray[i]) != -1)
    {
     // file has one of the accepted extensions.
     // now check for spaces in the filename
     return true;
     }
  }
   // file does not have one of the accepted extensions.
   alert("You tried to upload a file with an invalid extension. Valid extensions are:\n\n<xsl:value-of select="$extensions"/>");
   return false;
}
</script>  
	<form name="fileinput" action="{/usecase:asset/usecase:request-uri}" method="post" enctype="multipart/form-data" onsubmit="return check(fileinput)">
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
	  <input type="hidden" name="properties.insert.asset.area" value="{/usecase:asset/usecase:area}"/>
	  <input type="hidden" name="insert" value="{$insert}"/>
	  <input type="hidden" name="insertimage" value="{$insertimage}"/>
	  <input type="hidden" name="properties.insert.asset.document-id" value="{/usecase:asset/usecase:document-id}"/>
	  <input type="hidden" name="properties.insert.asset.language" value="{/usecase:asset/usecase:language}"/>
      <input type="hidden" name="properties.asset.date" value="{/usecase:asset/usecase:date}"/>
      <input type="hidden" name="properties.insert.asset.insertTemplate" value="{$insertTemplate}"/>
      <input type="hidden" name="insertTemplate" value="{$insertTemplate}"/>
      <input type="hidden" name="properties.insert.asset.insertReplace" value="{$insertReplace}"/>
      <input type="hidden" name="insertReplace" value="{$insertReplace}"/>
	  <table class="lenya-table-noborder">
	    <xsl:if test="$error = 'true'">
	      <tr>
		<td colspan="2" class="lenya-form-caption">
		  <span	class="lenya-form-message-error">The file name
		  of the file you are trying to upload contains characters which are not
		  allowed, such as spaces or umlauts. 
		  </span>
		</td>
	      </tr>
	    </xsl:if>
	    <tr>
	      <td class="lenya-form-caption" style="vertical-align: top;"><i18n:text>Select</i18n:text>&#160;
	      <xsl:choose>
	        <xsl:when test="$insertimage = 'true'"><i18n:text>Image</i18n:text></xsl:when>
	        <xsl:otherwise><i18n:text>File</i18n:text></xsl:otherwise>
	      </xsl:choose>:
	      </td>
	      <td><input class="lenya-form-element" type="file" name="properties.asset.data"/><br/>(No whitespace, no special characters)</td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Creator</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator" value="{/usecase:asset/usecase:creator}"/></td>
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
		<input i18n:attr="value" type="button" onClick="location.href='{/usecase:asset/usecase:request-uri}?lenya.usecase=checkin&amp;lenya.step=checkin&amp;backup=false';" value="Cancel"/>
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
	      <input type="hidden" name="properties.insert.asset.area" value="{/usecase:asset/usecase:area}"/>
	      <input type="hidden" name="properties.insert.asset.document-id" value="{/usecase:asset/usecase:document-id}"/>
	      <input type="hidden" name="properties.insert.asset.language" value="{/usecase:asset/usecase:language}"/>
	      <table class="lenya-table-noborder">
		<tr>
		  <td class="lenya-form-caption">
		  <xsl:choose><xsl:when test="$insertimage = 'true'"><i18n:text>Image</i18n:text>:</xsl:when>
	    <xsl:otherwise><i18n:text>File</i18n:text>:</xsl:otherwise></xsl:choose></td>
		  <td class="lenya-form-caption">
		    <select name="properties.asset.data" class="lenya-form-element">
		      <xsl:apply-templates select="usecase:assets/usecase:asset"/>
		    </select>
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
	    </form>
	  </div>
	</div>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="usecase:assets/usecase:asset">
      <xsl:choose>
          <!-- filter non-images for image upload -->
          <xsl:when test="$insertimage = 'true'">
            <xsl:if test="contains(., 'jpg') or contains(., 'gif')">
              <option><xsl:value-of select="."/></option>
            </xsl:if>             
          </xsl:when>
          <xsl:otherwise>
            <option><xsl:value-of select="."/></option>  
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  
  
</xsl:stylesheet>  
