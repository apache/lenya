<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: create.xsl,v 1.15 2004/03/14 16:42:15 roku Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:param name="status"/>
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
		alert('<i18n:text key="failmessage.createdoc.required"/>');
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
		alert('<i18n:text key="failmessage.createdoc.invalidformat"/>');
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

	if (!validRequired(theForm["properties.create.language"],"Language"))
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
            <xsl:if test="$status != ''">
              <tr>
                <td class="lenya-form-message-error" colspan="2"><i18n:text key="default.createdoc.idtaken"/></td>
              </tr>
            </xsl:if>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Parent ID</i18n:text>:</td><td><xsl:value-of select="/parent-child/parentid"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Document ID</i18n:text>*: </td><td><input class="lenya-form-element" type="text" name="properties.create.child-id"/><br/> (<i18n:text>No whitespace, no special characters</i18n:text>)</td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Navigation Title</i18n:text>*: </td><td><input class="lenya-form-element" type="text" name="properties.create.child-name"/></td>
            </tr>
	    <xsl:apply-templates select="allowedLanguages"/>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Creator</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.creator" value="{/parent-child/dc:creator}"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Subject</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.subject"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Publisher</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.publisher" value="{/parent-child/dc:publisher}"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Date</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.date" value="{/parent-child/dc:date}"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Rights</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.rights"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption">* <i18n:text>required fields</i18n:text></td>
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
  
  <xsl:template match="allowedLanguages">
    <tr>
      <td class="lenya-entry-caption"><i18n:text>Language</i18n:text>*:</td>
      <td>
        <select class="lenya-form-element" name="properties.create.language">
	  <xsl:apply-templates select="allowedLanguage"/>
        </select>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template match="allowedLanguage">
    <option value="{.}">
      <xsl:value-of select="."/>
    </option>
  </xsl:template>
  
  <xsl:template match="exception">
    <font color="red"><i18n:text>EXCEPTION</i18n:text></font><br />
    <a href="{../referer}"><i18n:text>Back</i18n:text></a><br />
    <p><i18n:text>Please check the following possible causes of the exception</i18n:text>
      <ol>
	<li><i18n:text key="exception.cause.createdoc.whitespace-in-id"/></li>
	<li><i18n:text key="exception.cause.createdoc.id-in-use"/></li>
      </ol>
      <i18n:text>Exception handling will be improved in the near future</i18n:text>
    </p>
  </xsl:template>
  
</xsl:stylesheet>  
