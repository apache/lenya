<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:param name="lenya.usecase" select="'asset'"/>
  <xsl:param name="lenya.step"/>
  <xsl:param name="insert"/>
  
  <xsl:template match="/">
    <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
      <xsl:choose>
	<xsl:when test="$insert = 'true'">
	  <page:title>Insert Asset</page:title>
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
	  <div class="lenya-box-title">Insert a new Asset</div>
	</xsl:when>
	<xsl:otherwise>
	  <div class="lenya-box-title">Upload an Asset</div>
	</xsl:otherwise>
      </xsl:choose>
      <div class="lenya-box-body">  
	<form method="GET" action="">
	  <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	  <input type="hidden" name="lenya.step" value="create"/>
	  <table class="lenya-table-noborder">
	    <tr>
	      <td class="lenya-form-caption">Select File:</td><td><input class="lenya-form-element" type="file" name="properties.asset.file"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Identifier:</td><td><input class="lenya-form-element" type="text" name="properties.asset.identifier"/></td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td class="lenya-form-caption">Title:</td><td><input class="lenya-form-element" type="text" name="properties.asset.title"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Creator:</td><td><input class="lenya-form-element" type="text" name="properties.asset.creator"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Subject:</td><td><input class="lenya-form-element" type="text" name="properties.asset.subject"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Date:</td><td><input class="lenya-form-element" type="text" name="properties.asset.date" value="{/usecase:asset/usecase:date}" readonly="true"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Rights:</td><td><input class="lenya-form-element" type="text" name="properties.asset.rights"/></td>
	    </tr>
	    <tr>
	      <td/>
	      <td>
		<input type="submit" value="Submit"/>&#160;
		<input type="button" onClick="location.href='{/usecase:asset/referer}';" value="Cancel"/>
	      </td>
	    </tr>
	  </table>
	</form>
      </div>
    </div>
    
    <xsl:choose>
      <xsl:when test="$insert = 'true'">
	<div class="lenya-box">
	  <div class="lenya-box-title">Insert an existing Asset</div>
	  <div class="lenya-box-body">
	    <form method="GET"
	      action="">
	      <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	      <input type="hidden" name="lenya.step" value="create"/>
	      <table class="lenya-table-noborder">
		<tr>
		  <td class="lenya-form-caption">Asset:</td>
		  <td class="lenya-form-caption">
		    <select class="lenya-form-element">
		      <xsl:apply-templates select="usecase:assets/usecase:asset"/>
		    </select>
		  </td>
		</tr>
		<tr>
		  <td/>
		  <td>
		    <input type="submit" value="Submit"/>&#160;
		    <input type="button" onClick="location.href='{/usecase:asset/referer}';" value="Cancel"/>
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
      <option name="properties.asset.file"><xsl:value-of select="."/></option>
  </xsl:template>
  
  
</xsl:stylesheet>  
