<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:param name="lenya.usecase" select="'create-language'"/>
  
  <xsl:template match="/">
    <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
      <page:title>Create new language version</page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="parent-child">
    
    <xsl:apply-templates select="exception"/>
    
    <xsl:if test="count(//dc:language) = 0">
      <div class="lenya-box">
        <div class="lenya-box-title">New language for existing Document</div>
        <div class="lenya-box-body">  
          <form method="GET" action="{/parent-child/referer}">
            <table class="lenya-table-noborder">
              <tr>
                <td>
                  <p>There are already versions for all languages for this document</p>
                </td>
              </tr>
              <tr>
                <td>
                  <input type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
                </td>
              </tr>
            </table>
          </form>
        </div>
      </div>
    </xsl:if>

    <xsl:if test="not(exception) and count(//dc:language) &gt; 0">
      <div class="lenya-box">
        <div class="lenya-box-title">New language for existing Document</div>
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

function validateForm(theForm)
{
	if (!validRequired(theForm["properties.create.child-name"],"Navigation Title"))
		return false;

	if (!validRequired(theForm["properties.create.title"],"Document Title"))
		return false;
	
	return true;
}
</script>
          <form method="GET" 
            action="{/parent-child/referer}" onsubmit="return validateForm(this)">
            <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
            <input type="hidden" name="lenya.step" value="create"/>
            <input type="hidden" name="properties.create.document-id" value="{/parent-child/document-id}"/>
            <input type="hidden" name="properties.create.old.language" value="{/parent-child/document-language}"/>
            <input type="hidden" name="properties.create.userid" value="{/parent-child/user-id}"/>
            <input type="hidden" name="properties.create.ipaddress" value="{/parent-child/ip-address}"/>
            <table class="lenya-table-noborder">
              <tr>
                <td class="lenya-form-caption">Document ID:</td><td>hidden value="<xsl:value-of select="/parent-child/document-id"/>"</td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Navigation Title*:</td><td><input class="lenya-form-element" type="text" name="properties.create.child-name"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Document Title*:</td><td><input class="lenya-form-element" type="text" name="properties.create.title"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Language:</td><td><select class="lenya-form-element"  name="properties.create.new.language"><xsl:apply-templates select="dc:languages"/></select></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Creator:</td><td><input class="lenya-form-element" type="hidden" name="properties.create.creator" value="{/parent-child/dc:creator}"/><xsl:value-of select="/parent-child/dc:creator"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Subject:</td><td><input class="lenya-form-element" type="text" name="properties.create.subject"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Publisher:</td><td><input class="lenya-form-element" type="text" name="properties.create.publisher" value="{/parent-child/dc:publisher}"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Date:</td><td><input class="lenya-form-element" type="hidden" name="properties.create.date" value="{/parent-child/dc:date}"/><xsl:value-of select="/parent-child/dc:date"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption">Rights:</td><td><input class="lenya-form-element" type="text" name="properties.create.rights" value="{/parent-child/dc:rights}"/></td>
              </tr>
              <tr>
                <td>* required fields</td>
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
  
  <xsl:template match="dc:languages">
    <xsl:for-each select="dc:language">
      <option><xsl:value-of select="."/></option>
    </xsl:for-each>
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
