<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:param name="lenya.usecase" select="'asset'"/>
  <xsl:param name="lenya.step"/>

  <xsl:param name="error"/>

  <xsl:template match="/">
    <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
	  <page:title>Content Upload</page:title>
      <page:body>
	<xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="usecase:asset">
    
    <div class="lenya-box">
	  <div class="lenya-box-title">Upload Content</div>
      <div class="lenya-box-body">  
	<form action="{/usecase:asset/usecase:request-uri}" method="post" enctype="multipart/form-data">
	  <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	  <input type="hidden" name="uploadtype" value="content"/>
	  <input type="hidden" name="lenya.step" value="upload"/>
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
	    <tr>
	      <td/>
	      <td>
		<input type="submit" value="Submit"/>&#160;
		<input type="button" onClick="location.href='{/usecase:asset/usecase:request-uri}';" value="Cancel"/>
	      </td>
	    </tr>
	  </table>
	</form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>  
