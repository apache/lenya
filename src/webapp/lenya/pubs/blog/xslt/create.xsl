<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:import href="../../../xslt/authoring/parent-child.xsl"/>
  
  <xsl:template match="parent-child">
    <h1>New Entry</h1>
    
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
<!--
	    <tr>
	      <td>parentid:</td><td><xsl:value-of select="/parent-child/parentid"/></td>
	    </tr>
	    <tr>
	      <td>childtype:</td><td><xsl:value-of select="/parent-child/childtype"/></td>
	    </tr>
	    <tr>
	      <td>doctype:</td><td><xsl:value-of select="/parent-child/doctype"/></td>
	    </tr>
-->
	    <tr>
	      <td>id:</td><td><input type="text" name="childid"/></td>
	    </tr>
	    <tr>
	      <td>title:</td><td><input type="text" name="title"/></td>
	    </tr>
<!--
	    <tr>
	      <td>tree node name:</td><td>My Node Name</td>
	      <td>tree node name:</td><td><input type="text" name="childname"/></td>
	    </tr>
-->
	  </table>
	  <input type="submit" value="Create"/>&#160;&#160;&#160;
	  <input type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
	</form>
      </p>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>  
