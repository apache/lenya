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

<!-- $Id: create-language.xsl,v 1.6 2004/03/13 14:42:38 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml">
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:param name="lenya.usecase" select="'create-language'"/>
  
  <xsl:template match="/">
    <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
      <page:title><i18n:text>Create new language version</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="parent-child">
    
    <xsl:apply-templates select="exception"/>
    
    <xsl:if test="count(//dc:language) = 0">
      <div class="lenya-box">
        <div class="lenya-box-title"><i18n:text>New language for existing Document</i18n:text></div>
        <div class="lenya-box-body">  
          <form method="GET" action="{/parent-child/referer}">
            <table class="lenya-table-noborder">
              <tr>
                <td>
                  <p><i18n:text key="default.createdoc.info.all-language-versions-exists"/></p>
                </td>
              </tr>
              <tr>
                <td>
                  <input i18n:attr="value" type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
                </td>
              </tr>
            </table>
          </form>
        </div>
      </div>
    </xsl:if>

    <xsl:if test="not(exception) and count(//dc:language) &gt; 0">
      <div class="lenya-box">
        <div class="lenya-box-title"><i18n:text>New language for existing Document</i18n:text></div>
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
                <td class="lenya-form-caption"><i18n:text>Document ID</i18n:text>:</td><td><xsl:value-of select="/parent-child/document-id"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Navigation Title</i18n:text>*:</td><td><input class="lenya-form-element" type="text" name="properties.create.child-name"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Language</i18n:text>:</td><td><select class="lenya-form-element"  name="properties.create.new.language"><xsl:apply-templates select="dc:languages"/></select></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Creator</i18n:text>:</td><td><input class="lenya-form-element" type="hidden" name="properties.create.creator" value="{/parent-child/dc:creator}"/><xsl:value-of select="/parent-child/dc:creator"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Subject</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.subject"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Publisher</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.publisher" value="{/parent-child/dc:publisher}"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Date</i18n:text>:</td><td><input class="lenya-form-element" type="hidden" name="properties.create.date" value="{/parent-child/dc:date}"/><xsl:value-of select="/parent-child/dc:date"/></td>
              </tr>
              <tr>
                <td class="lenya-form-caption"><i18n:text>Rights</i18n:text>:</td><td><input class="lenya-form-element" type="text" name="properties.create.rights" value="{/parent-child/dc:rights}"/></td>
              </tr>
              <tr>
                <td>* <i18n:text>required fields</i18n:text></td>
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
  
  <xsl:template match="dc:languages">
    <xsl:for-each select="dc:language">
      <option><xsl:value-of select="."/></option>
    </xsl:for-each>
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
