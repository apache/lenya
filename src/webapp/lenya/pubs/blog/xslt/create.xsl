<?xml version="1.0"?>

<!--
 $Id: create.xsl,v 1.2 2004/02/06 19:23:35 gregor Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
    
  <xsl:template match="/">
    <page:page>
      <page:title>New Entry</page:title>
      <page:body>
    <div class="lenya-box">
      <div class="lenya-box-title">New Entry</div>
      <div class="lenya-box-body">
       <xsl:apply-templates select="parent-child"/>
      </div>
    </div>
      </page:body>
    </page:page>
  </xsl:template>


  <xsl:template match="parent-child">
    <xsl:apply-templates select="exception"/>
    
    <xsl:if test="not(exception)">
      <p>
	<form method="POST" action="{/parent-child/referer}">
	  <input type="hidden" name="parentid" value="{/parent-child/parentid}"/>
	  <input type="hidden" name="lenya.usecase" value="create"/>
	  <input type="hidden" name="lenya.step" value="execute"/>
	  <input type="hidden" name="childtype" value="{/parent-child/childtype}"/>
	  <input type="hidden" name="doctype" value="{/parent-child/doctype}"/>
	  <input type="hidden" name="childname" value="Levi"/>
	  <table>
	    <tr>
	      <td>id:</td><td><input type="text" name="childid"/></td>
	    </tr>
	    <tr>
	      <td>title:</td><td><input type="text" name="title"/></td>
	    </tr>
	  </table>
	  <input type="submit" value="Create"/>&#160;&#160;&#160;
	  <input type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
	</form>
      </p>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>  