<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:param name="lenya.usecase" select="'asset'"/>
  <xsl:param name="lenya.step"/>
  <xsl:param name="insert"/>
  <xsl:param name="insertimage"/>
  
  <xsl:param name="assetXPath"/>
  <xsl:param name="insertWhere"/>

  <xsl:param name="error"/>

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
	<form action="{/usecase:asset/usecase:request-uri}" method="post" enctype="multipart/form-data">
	  <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	  <xsl:choose>
	    <xsl:when test="$insert = 'true'">
	      <input type="hidden" name="lenya.step" value="upload-and-insert"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <input type="hidden" name="lenya.step" value="upload"/>
	    </xsl:otherwise>
	  </xsl:choose>
	  <input type="hidden" name="task-id" value="insert-asset"/>
	  <input type="hidden" name="properties.insert.asset.assetXPath" value="{$assetXPath}"/>
	  <input type="hidden" name="properties.insert.asset.insertWhere" value="{$insertWhere}"/>
	  <input type="hidden" name="insert" value="{$insert}"/>
	  <input type="hidden" name="insertimage" value="{$insertimage}"/>
	  <input type="hidden" name="assetXPath" value="{$assetXPath}"/>
	  <input type="hidden" name="insertWhere" value="{$insertWhere}"/>
	  <input type="hidden" name="properties.insert.asset.document-id" value="{/usecase:asset/usecase:document-id}"/>
	  <input type="hidden" name="properties.insert.asset.language" value="{/usecase:asset/usecase:language}"/>
	  <xsl:choose>
	    <xsl:when test="$insertimage = 'true'">
	      <input type="hidden" name="properties.insert.asset.insertTemplate" value="insertImg.xml"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <input type="hidden" name="properties.insert.asset.insertTemplate" value="insertAsset.xml"/>
	    </xsl:otherwise>
	  </xsl:choose>
	  <table class="lenya-table-noborder">
	    <xsl:if test="$error = 'true'">
	      <tr>
		<td colspan="2" class="lenya-form-caption">
		  <span	class="lenya-form-message-error">The file name
		  of the file you are trying to upload contains characters
		  which are not allowed, such as spaces or umlauts.
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
	      <td class="lenya-form-caption">Subject:</td><td><input class="lenya-form-element" type="text" name="properties.asset.subject"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Date:</td><td><input
	      class="lenya-form-element" type="hidden" name="properties.asset.date" value="{/usecase:asset/usecase:date}"/><xsl:value-of select="/usecase:asset/usecase:date"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Rights:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights"/></td>
	    </tr>
	    <tr><td>&#160;</td></tr>
            <xsl:if test="$insert = 'true'">
              <tr>
                <td class="lenya-form-caption">Caption:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value="Default Caption"/></td>
              </tr>
            </xsl:if>
	    <xsl:if test="$insertimage = 'true'">
	      <tr>
		<td class="lenya-form-caption">Link:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.link"/></td>
	      </tr>
	    </xsl:if>
	    <tr><td>&#160;</td></tr>
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
	      <input type="hidden" name="task-id" value="insert-asset"/>
	      <input type="hidden" name="properties.insert.asset.assetXPath" value="{$assetXPath}"/>
	      <input type="hidden" name="properties.insert.asset.insertWhere" value="{$insertWhere}"/>
	      <xsl:choose>
		<xsl:when test="$insertimage = 'true'">
		  <input type="hidden" name="properties.insert.asset.insertTemplate" value="insertImg.xml"/>
		</xsl:when>
		<xsl:otherwise>
		  <input type="hidden" name="properties.insert.asset.insertTemplate" value="insertAsset.xml"/>
		</xsl:otherwise>
	      </xsl:choose>
	      <input type="hidden" name="properties.insert.asset.area" value="{/usecase:asset/usecase:area}"/>
	      <input type="hidden" name="properties.insert.asset.document-id" value="{/usecase:asset/usecase:document-id}"/>
	      <input type="hidden" name="properties.insert.asset.language" value="{/usecase:asset/usecase:language}"/>
	      <table class="lenya-table-noborder">
		<tr>
		  <td class="lenya-form-caption">Asset:</td>
		  <td class="lenya-form-caption">
		    <select name="properties.asset.data" class="lenya-form-element">
		      <xsl:apply-templates select="usecase:assets/usecase:asset"/>
		    </select>
		  </td>
		</tr>
		<tr><td>&#160;</td></tr>
		<tr>
		  <td class="lenya-form-caption">Caption:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.caption" value="Deault Caption"/></td>
		</tr>
		<xsl:if test="$insertimage = 'true'">
		  <tr>
		    <td class="lenya-form-caption">Link:</td><td><input class="lenya-form-element" type="text" name="properties.insert.asset.link"/></td>
		  </tr>
		</xsl:if>
		<tr><td>&#160;</td></tr>
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
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="usecase:assets/usecase:asset">
      <option><xsl:value-of select="."/></option>
  </xsl:template>
  
  
</xsl:stylesheet>  
