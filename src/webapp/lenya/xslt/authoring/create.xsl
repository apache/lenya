<?xml version="1.0" encoding="utf-8"?>

<!--
  $Id: create.xsl,v 1.6 2004/03/03 09:46:51 roku Exp $
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
>
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:param name="lenya.usecase" select="'create'"/>
  
  <xsl:template match="/">
	  <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
	  <page:title><i18n:text>New Document</i18n:text></page:title>
	  <page:body>
	    <xsl:apply-templates/>
    </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="parent-child">
    
    <xsl:apply-templates select="exception"/>
    
    <xsl:if test="not(exception)">
      <div class="lenya-box">
        <div class="lenya-box-title"><i18n:text>New Document</i18n:text></div>
      <div class="lenya-box-body">  
        <script Language="JavaScript">
function validRequired(formField,fieldLabel)
{
	var result = true;
	
	if (formField.value == "")
	{
		alert('Please enter a value for the "' + fieldLabel +'" field.');
		formField.focus();
		result = false;
	}
	
	return result;
}

function validContent(formField,fieldLabel)
{
	var result = true;
	
	if (formField.value.match("[^a-zA-Z0-9\\-]+"))
	{
		alert('Please enter a valid value for the "' + fieldLabel +'" field. A-Z, a-z, 0-9 or -');
		formField.focus();
		result = false;
	}
	
	return result;
}

function validateForm(theForm)
{
	if (!validContent(theForm["properties.create.child-id"],"Document ID"))
		return false;

	if (!validRequired(theForm["properties.create.child-id"],"Document ID"))
		return false;

	if (!validRequired(theForm["properties.create.child-name"],"Navigation Title"))
		return false;

	return true;
}
</script>
   	<form method="GET" 
	  action="{/parent-child/referer}" onsubmit="return validateForm(this)">
	  <input type="hidden" name="properties.create.parent-id" value="{/parent-child/parentid}"/>
	  <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
	  <input type="hidden" name="lenya.step" value="create"/>
	  <input type="hidden" name="properties.create.child-type" value="branch"/>
	  <input type="hidden" name="properties.create.doctype" value="{/parent-child/doctype}"/>
	  <table class="lenya-table-noborder">
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Parent ID</i18n:text>:</td><td><xsl:value-of select="/parent-child/parentid"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Document ID</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.child-id"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption"><i18n:text>Name</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.child-name"/></td>
	    </tr>
	    <tr>
	      <td/>
	      <td>
            <input i18n:attr="value" type="submit" value="Create"/>&#160;
            <input i18n:attr="value" type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
	      </td>
	    </tr>
	  </table>
	</form>
      </div>
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="exception">
    <font color="red"><i18n:text>EXCEPTION</i18n:text></font><br />
    <a href="{../referer}"><i18n:text>Back</i18n:text></a><br />
    <p><i18n:text>Please check the following possible causes of the exception</i18n:text>
      <ol>
	<li><i18n:text>The id is not allowed to have whitespaces</i18n:text></li>
	<li><i18n:text>The id is already in use</i18n:text></li>
      </ol>
      <i18n:text>Exception handling will be improved in the near future</i18n:text>
    </p>
  </xsl:template>
  
</xsl:stylesheet>  
