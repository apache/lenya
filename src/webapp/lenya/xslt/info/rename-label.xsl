<?xml version="1.0"?>

<!--
 $Id: rename-label.xsl,v 1.3 2003/10/27 15:44:43 andreas Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
      <page:title>Edit Navigation Title</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">Edit Navigation Title</div>
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
		  <td class="lenya-entry-caption">New Navigation Title:</td>
		  <td><input type="text" class="lenya-form-element" name="properties.rename.label.label-name" value="{$label}"/></td>
		</tr>
		<tr>
		  <td/>
		  <td>
		    <br/>
		    <input type="submit" value="Submit"/>&#160;
		    <input type="button" onClick="location.href='{$requesturi}';" value="Cancel"/>
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