<?xml version="1.0" encoding="UTF-8"?>

<!--
  $Id: groups.xsl,v 1.4 2004/02/18 18:08:16 roku Exp $
-->

<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="UTF-8" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text>User Details</i18n:text>: <xsl:value-of select="user/id"/></page:title>
      <page:body>
    <table class="lenya-noborder">
    <tr>
    <td>
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Group Affiliation</i18n:text></div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{continuation}.continuation">
          <input type="hidden" name="user-id" value="{id}"/>
          
                <table class="lenya-table-noborder-nopadding">
                  <tr>
                    <td><strong><i18n:text>User Groups</i18n:text></strong></td>
                    <td/>
                    <td><strong><i18n:text>All Groups</i18n:text></strong></td>
                  </tr>
                  <tr>
                    <td valign="middle">
                      <select name="user_group" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="user-groups"/>
                      </select>
                    </td>
                    <td valign="middle">
                      <input name="add_group" type="submit" value="&lt;"/>
                      <br/>
                      <input name="remove_group" type="submit" value="&gt;"/>
                    </td>
                    <td valign="middle">
                      <select name="group" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="groups"/>
                      </select>
                    </td>
                  </tr>
                </table>
                
                <div style="margin-top: 10px; text-align: center">
                  <input i18n:attr="value" type="submit" name="submit" value="Save"/>
                  &#160;
                  <input i18n:attr="value" type="submit" name="cancel" value="Cancel"/>
                </div>
                </form>
              </div>
            </div>
          </td>
        </tr>
    </table>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="groups">
    <xsl:apply-templates select="group"/>
  </xsl:template>
  
  
  <xsl:template match="group">
    <option value="{@id}">
    	<xsl:value-of select="@id"/>
    	<xsl:if test="normalize-space(.)">
    		&#160;(<xsl:value-of select="normalize-space(.)"/>)
    	</xsl:if>
    </option>
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
