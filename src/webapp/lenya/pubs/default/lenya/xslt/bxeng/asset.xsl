<?xml version="1.0" encoding="UTF-8"?>

<!--
 $Id: asset.xsl,v 1.1 2004/02/04 20:31:16 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
    disable-output-escaping="yes"
    exclude-result-prefixes="lenya-info wf rc dc usecase"
    >

  <xsl:param name="lenya.usecase" select="'asset'"/>
  <xsl:param name="lenya.step"/>
  
  <xsl:param name="error"/>

  <xsl:param name="extensions" select="'pdf,doc,dot,rtf,txt,asc,ascii,xls,xlw,xlt,ppt,pot,gif,jpg,png,tif,eps,pct,m3u,kar,mid,smf,mp3,swa,mpg,mpv,mp4,mov,bin,sea,hqx,sit,zip,jmx,jcl,qz,jbc,jmt,cfg'"/>

  <xsl:template match="lenya-info:assets">
    <page:page>
      <page:title>Insert File</page:title>
      <page:body>
<script>
function insertAsset(src, size) {

  var title = document.forms["assetlibrary"].title.value;
  window.opener.bxe_insertContent('<asset xmlns="http://apache.org/cocoon/lenya/page-envelope/1.0" src="'+src+'" size="'+size+'" type="">'+title+'</asset>',window.opener.BXE_SELECTION,window.opener.BXE_SPLIT_IF_INLINE);
  window.close();
}

function check(fileinput) {
  var i = 0;
  var ext = '<xsl:value-of select="$extensions"/>';
  var delimiter = ',';
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
   alert("You tried to upload a file with an invalid extension. Valid extensions are <xsl:value-of select="$extensions"/>");
   return false;
}
</script>
<div class="lenya-box">
      <div class="lenya-box-title">Add to Asset Library</div>
	<form name="fileinput" action="{/usecase:asset/usecase:request-uri}" method="post" enctype="multipart/form-data" onsubmit="return check(fileinput)">
	  <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	  <input type="hidden" name="lenya.step" value="asset-upload"/>
	  <input type="hidden" name="task-id" value="insert-asset"/>
	  <input type="hidden" name="uploadtype" value="asset"/>
	  <input type="hidden" name="properties.insert.asset.document-id" value="{/usecase:asset/usecase:document-id}"/>
	  <input type="hidden" name="properties.insert.asset.language" value="{/usecase:asset/usecase:language}"/>
	  <table class="lenya-table-noborder">
	    <xsl:if test="$error = 'true'">
	      <tr>
		<td colspan="2" class="lenya-form-caption">
		  <span	class="lenya-form-message-error">The file name
		  of the file you are trying to upload either has no
		  extension, or contains characters which are not
		  allowed, such as spaces or umlauts. 
		  </span>
		</td>
	      </tr>
	    </xsl:if>
	    <tr>
	      <td class="lenya-form-caption">Select File:</td><td><input class="lenya-form-element" type="file" name="properties.asset.data"/><br/>(No whitespace, no special characters)</td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td class="lenya-form-caption">Title:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Creator:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator" value="{/usecase:asset/usecase:creator}"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Rights:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights"/></td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td/>
	      <td>
		<input type="submit" value="Submit" />&#160;
		<input type="button" onClick="location.href='{/usecase:asset/usecase:request-uri}';" value="Cancel"/>
	      </td>
	    </tr>
	  </table>
	</form>
</div>
<div class="lenya-box">
      <div class="lenya-box-title">Asset Library</div>
<form name="assetlibrary" action="">
<table>
<xsl:if test="not(lenya-info:asset)">
<tr><td colspan="4" class="lenya-form-caption">No Assets available</td></tr>
</xsl:if>
	    <tr>
	      <td class="lenya-form-caption" colspan="2">Title:</td><td colspan="2"><input class="lenya-form-element" type="text" name="title"/></td>
	    </tr>
	    <tr><td colspan="4">&#160;</td></tr>
<xsl:for-each select="lenya-info:asset">
<tr>
<td class="lenya-form-caption"><xsl:value-of select="dc:title"/></td>
<td class="lenya-form-caption"><xsl:value-of select="dc:extent"/> KB</td>
<td class="lenya-form-caption"><xsl:value-of select="dc:date"/></td>
<td class="lenya-form-caption"><a href="javascript:insertAsset('{dc:title}','{dc:extent}');">Insert</a></td>
</tr>
</xsl:for-each>
    </table>
</form>
</div>
      </page:body>
    </page:page>
  </xsl:template>

</xsl:stylesheet>  
