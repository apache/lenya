<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
	      <page:title>Insert Image</page:title>
	    </xsl:when>
	    <xsl:otherwise>
	      <page:title>Insert Asset</page:title>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:when>
	<xsl:otherwise>
	  <page:title>Asset Upload</page:title>
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
	      <div class="lenya-box-title">Insert a new Image</div>
	    </xsl:when>
	    <xsl:otherwise>
	      <div class="lenya-box-title">Insert a new Asset</div>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:when>
	<xsl:otherwise>
	  <div class="lenya-box-title">Upload an Asset</div>
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
	      <td class="lenya-form-caption">Select 
	      <xsl:choose>
	        <xsl:when test="$insertimage = 'true'">Image</xsl:when>
	        <xsl:otherwise>File</xsl:otherwise>
	      </xsl:choose>
	      :</td><td><input class="lenya-form-element" type="file" name="properties.asset.data"/><br/>(No whitespace, no special characters)</td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td class="lenya-form-caption">Title:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Creator:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator" value="{/usecase:asset/usecase:creator}"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Rights:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights" value="All rights reserved."/></td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <xsl:if test="$insertimage = 'true'">
              <tr>
                <td class="lenya-form-caption">Caption:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value=""/></td>
              </tr>
	      <tr>
		<td class="lenya-form-caption">Link:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.link"/><br/>External links have to start with 'http://', internal links have to start with '/'</td>
	      </tr>
	    </xsl:if>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td/>
	      <td>
		<input type="submit" value="Submit"/>&#160;
		<input type="button" onClick="location.href='{/usecase:asset/usecase:request-uri}?lenya.usecase=checkin&amp;lenya.step=checkin&amp;backup=false';" value="Cancel"/>
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
	      <div class="lenya-box-title">Insert an existing Image</div>
	    </xsl:when>
	    <xsl:otherwise>
	      <div class="lenya-box-title">Insert an existing Asset</div>
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
		  <xsl:choose><xsl:when test="$insertimage = 'true'">Image:</xsl:when>
	    <xsl:otherwise>File:</xsl:otherwise></xsl:choose></td>
		  <td class="lenya-form-caption">
		    <select name="properties.asset.data" class="lenya-form-element">
		      <xsl:apply-templates select="usecase:assets/usecase:asset"/>
		    </select>
		  </td>
		</tr>
		<tr><td>&#160;</td></tr>
		<tr>
		  <td class="lenya-form-caption">Title:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title" value=""/></td>
		</tr>
		<xsl:if test="$insertimage = 'true'">
          <tr>
            <td class="lenya-form-caption">Caption:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value="Default Caption"/></td>
          </tr>
		  <tr>
		    <td class="lenya-form-caption">Link:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.link"/><br/>External links have to start with 'http://', internal links have to start with '/'</td>
		  </tr>
		</xsl:if>
		<tr><td>&#160;</td></tr>
		<tr>
		  <td/>
		  <td>
		    <input type="submit" value="Submit"/>&#160;
		    <input type="button" onClick="location.href='{/usecase:asset/usecase:request-uri}?lenya.usecase=checkin&amp;lenya.step=checkin&amp;backup=false';" value="Cancel"/>
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
