<?xml version="1.0"?>

<!--
 $Id: rename.xsl,v 1.17 2004/02/23 08:37:07 roku Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>
  <xsl:variable name="source-document-id"><xsl:value-of select="/page/info/source-document-id"/></xsl:variable>
  <xsl:variable name="ref-document-id"><xsl:value-of select="/page/info/ref-document-id"/></xsl:variable>
  <xsl:variable name="destination-id"><xsl:value-of select="/page/info/destination-id"/></xsl:variable>
  <xsl:variable name="area"><xsl:value-of select="/page/info/area"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text>Rename Document</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
        <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Rename Document</i18n:text></div>
      <div class="lenya-box-body">
        <script Language="JavaScript">
function validRequired(formField,fieldLabel)
{
	var result = true;
	
	if (formField.value.match("[^a-zA-Z0-9_\\-]+"))
	{
		alert('Please enter a valid value for the "' + fieldLabel +'" field.');
		formField.focus();
		result = false;
	}

	return result;
	
}

function validateForm(theForm)
{
	if (!validRequired(theForm["properties.node.secdocumentid"],"New Document ID"))
		return false;

	return true;
}
</script>
           <form method="get" onsubmit="return validateForm(this)">
          <xsl:attribute name="action"></xsl:attribute>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <xsl:call-template name="task-parameters">
            <xsl:with-param name="prefix" select="''"/>
          </xsl:call-template>
          <input type="hidden" name="lenya.usecase" value="rename"/>
          <input type="hidden" name="lenya.step" value="rename"/>
          <input type="hidden" name="parenturl" value="{parent-url}"/>
          
          <table class="lenya-table-noborder">
            <tr>
              <td class="lenya-entry-caption"><i18n:text>New Document ID</i18n:text>:</td>
              <td><input type="text" class="lenya-form-element" name="properties.node.secdocumentid" value="{$destination-id}"/> (<i18n:text>No whitespace, no special characters</i18n:text>)</td>
            </tr>
            <tr>
              <td/>
              <td>
                <br/>
                <input i18n:attr="value" type="submit" value="Rename"/>&#160;
                <input i18n:attr="value" type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
  </xsl:template>

  <xsl:template name="task-parameters">
    <xsl:param name="prefix" select="'task.'"/>
    <input type="hidden" name="{$prefix}properties.node.firstdocumentid" value="{$source-document-id}"/>
    <input type="hidden" name="{$prefix}properties.firstarea" value="{$area}"/>
    <input type="hidden" name="{$prefix}properties.secarea" value="{$area}"/>
    <input type="hidden" name="{$prefix}properties.node.refdocumentid" value="{$ref-document-id}"/>
  </xsl:template>

</xsl:stylesheet>