<?xml version="1.0"?>

<!--
 $Id: rename-label.xsl,v 1.4 2004/02/23 08:37:07 roku Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:param name="requesturi"/>
  <xsl:param name="area"/>
  <xsl:param name="language"/>
  <xsl:param name="label"/>
  <xsl:param name="documentid"/>
  <xsl:param name="taskid"/>
  <xsl:param name="lenya.event"/>

  <xsl:template match="/">
    <page:page>
      <page:title><i18n:text>Edit Navigation Title</i18n:text></page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title"><i18n:text>Edit Navigation Title</i18n:text></div>
	  <div class="lenya-box-body">
	    <form method="get">
	      <xsl:attribute name="action"></xsl:attribute>
	      <input type="hidden" name="task-id" value="{$taskid}"/>
	      <input type="hidden" name="properties.rename.label.document-id" value="{$documentid}"/>
	      <input type="hidden" name="properties.rename.label.language" value="{$language}"/>
	      <input type="hidden" name="properties.rename.label.area" value="{$area}"/>
	      <input type="hidden" name="lenya.usecase" value="rename-label"/>
	      <input type="hidden" name="lenya.step" value="rename-label"/>
	      <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
	      
	      <table class="lenya-table-noborder">
		<tr>
		  <td class="lenya-entry-caption"><i18n:text>New Navigation Title</i18n:text>:</td>
		  <td><input type="text" class="lenya-form-element" name="properties.rename.label.label-name" value="{$label}"/></td>
		</tr>
		<tr>
		  <td/>
		  <td>
		    <br/>
		    <input i18n:attr="value" type="submit" value="Save"/>&#160;
		    <input i18n:attr="value" type="button" onClick="location.href='{$requesturi}';" value="Cancel"/>
		  </td>
		</tr>
	      </table>
	    </form>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>