<?xml version="1.0"?>

<!--
 $Id: rename-label.xsl,v 1.1 2003/09/12 12:34:11 egli Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:param name="request-uri"/>
  <xsl:param name="area"/>
  <xsl:param name="label"/>
  <xsl:param name="document-id"/>
  <xsl:param name="task-id"/>

  <xsl:template match="/">
    <page:page>
      <page:title>Rename Label</page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title">Rename Label</div>
	  <div class="lenya-box-body">
	    <form method="get">
	      <xsl:attribute name="action"></xsl:attribute>
	      <input type="hidden" name="task-id" value="{$task-id}"/>
	      <input type="hidden" name="properties.node.documentid" value="{$document-id}"/>
	      <input type="hidden" name="properties.node.area" value="{$area}"/>
	      <input type="hidden" name="lenya.usecase" value="rename-label"/>
	      <input type="hidden" name="lenya.step" value="rename-label"/>
	      
	      <table class="lenya-table-noborder">
		<tr>
		  <td class="lenya-entry-caption">New Label:</td>
		  <td><input type="text" class="lenya-form-element" name="properties.node.label" value="{$label}"/></td>
		</tr>
		<tr>
		  <td/>
		  <td>
		    <br/>
		    <input type="submit" value="Rename"/>&#160;
		    <input type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
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