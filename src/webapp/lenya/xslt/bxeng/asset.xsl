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

<!-- $Id: asset.xsl,v 1.10 2004/04/15 11:33:28 gregor Exp $ -->

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
    disable-output-escaping="yes"
    exclude-result-prefixes="lenya-info wf rc dc usecase page i18n"
    >

  <xsl:param name="lenya.usecase" select="'asset'"/>
  <xsl:param name="lenya.step"/>
  
  <xsl:param name="error"/>

  <xsl:param name="extensions" select="'doc dot rtf txt asc ascii xls xlw xlt ppt pot gif jpg png tif eps pct m3u kar mid smf mp3 swa mpg mpv mp4 mov bin sea hqx sit zip jmx jcl qz jbc jmt cfg pdf'"/>

  <xsl:template match="lenya-info:assets">
    <page:page>
      <page:title><i18n:text>Insert Asset</i18n:text></page:title>
      <page:body>
<script>
function insertAsset(src, size) {

  var title = document.forms["assetlibrary"].title.value;
  <![CDATA[
  window.opener.bxe_insertContent('<asset xmlns="http://apache.org/cocoon/lenya/page-envelope/1.0" src="'+src+'" size="'+size+'" type="">'+title+'</asset>',window.opener.BXE_SELECTION,window.opener.BXE_SPLIT_IF_INLINE);
  ]]>
  window.close();
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
     return true;
     }
  }
   // file does not have one of the accepted extensions.
   alert("<i18n:translate><i18n:text key="upload-with-invalid-extension"/><i18n:param>:\n\n<xsl:value-of select="$extensions"/></i18n:param></i18n:translate>");
   return false;
}
</script>
<div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Add to Asset Library</i18n:text></div>
	<form name="fileinput" action="" method="post" enctype="multipart/form-data" onsubmit="return check(fileinput)">
	  <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	  <input type="hidden" name="lenya.step" value="asset-upload"/>
	  <input type="hidden" name="task-id" value="insert-asset"/>
	  <input type="hidden" name="uploadtype" value="asset"/>
      <input type="hidden" name="properties.asset.date" value="{/lenya-info:info/lenya-info:assets/lenya-info:date}"/>
	  <input type="hidden" name="properties.insert.asset.document-id" value="{/lenya-info:info/lenya-info:assets/lenya-info:document-id}"/>
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
	      <td class="lenya-form-caption"><i18n:text>Select File</i18n:text>:</td><td><input class="lenya-form-element" type="file" name="properties.asset.data"/><br/>(<i18n:text>No whitespace, no special characters</i18n:text>)</td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Title</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Creator</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator" value="{/lenya-info:info/lenya-info:assets/lenya-info:creator}"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Rights</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights" value="All rights reserved."/></td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td/>
	      <td>
		<input i18n:attr="value" type="submit" value="Submit" />&#160;
		<input i18n:attr="value" type="button" onClick="location.href='javascript:window.close();';" value="Cancel"/>
	      </td>
	    </tr>
	  </table>
	</form>
</div>
<div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Asset Library</i18n:text></div>
<form name="assetlibrary" action="">
<table class="lenya-table-noborder">
<xsl:if test="not(lenya-info:asset)">
<tr><td colspan="5" class="lenya-form-caption"><i18n:text>No assets available</i18n:text></td></tr>
</xsl:if>
	    <tr>
	      <td class="lenya-form-caption" colspan="2"><i18n:text>Title</i18n:text>:</td><td colspan="2"><input class="lenya-form-element" type="text" name="title"/></td>
	    </tr>
	    <tr><td colspan="5">&#160;</td></tr>
<xsl:for-each select="lenya-info:asset">
<tr>
<td colspan="2"><xsl:value-of select="dc:title"/></td>
<td><xsl:value-of select="dc:extent"/> KB</td>
<td><xsl:value-of select="dc:date"/></td>
<td><a href="javascript:insertAsset('{dc:title}','{dc:extent}');"><i18n:text>Insert</i18n:text></a></td>
</tr>
</xsl:for-each>
    </table>
</form>
</div>
      </page:body>
    </page:page>
  </xsl:template>

</xsl:stylesheet>  
