<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:param name="lenya.usecase" select="'create'"/>
  
  <xsl:template match="/">
	  <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
	  <page:title>Create Document</page:title>
	  <page:body>
	    <xsl:apply-templates/>
    </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="parent-child">
    
    <xsl:apply-templates select="exception"/>
    
    <xsl:if test="not(exception)">
      <div class="lenya-box">
        <div class="lenya-box-title">New Document</div>
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
	
	if (formField.value.match("[^a-zA-Z0-9_\\-]+"))
	{
		alert('Please enter a valid value for the "' + fieldLabel +'" field. A-Z, a-z, 0-9, _ or -');
		formField.focus();
		result = false;
	}

	return result;
}

function validateForm(theForm)
{
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
	      <td class="lenya-form-caption">Parent ID:</td><td><xsl:value-of select="/parent-child/parentid"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Document ID:</td><td><input class="lenya-form-element" type="text" name="properties.create.child-id"/></td>
	    </tr>
	    <tr>
	      <td class="lenya-form-caption">Name:</td><td><input class="lenya-form-element" type="text" name="properties.create.child-name"/></td>
	    </tr>
	    <tr>
	      <td/>
	      <td>
            <input type="submit" value="Create"/>&#160;
            <input type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
	      </td>
	    </tr>
	  </table>
	</form>
      </div>
      </div>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="exception">
    <font color="red">EXCEPTION</font><br />
    Go <a href="{../referer}">back</a> to page.<br />
    <p>
      Exception handling isn't very good at the moment. 
      For further details please take a look at the log-files
      of Cocoon. In most cases it's one of the two possible exceptions:
      <ol>
	<li>The id is not allowed to have whitespaces</li>
	<li>The id is already in use</li>
      </ol>
      Exception handling will be improved in the near future.
    </p>
  </xsl:template>
  
</xsl:stylesheet>  
