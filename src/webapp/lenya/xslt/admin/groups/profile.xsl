<?xml version="1.0" encoding="UTF-8"?>
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
      <page:title><i18n:text><xsl:value-of select="title"/></i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="group"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="group">
    
    <table class="lenya-noborder">
    <tr>
    <td valign="top">
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Group Data</i18n:text></div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{/page/continuation}.continuation">
          <table class="lenya-table-noborder">
            
            <xsl:apply-templates select="message"/>
            
            <tr>
              
              <td class="lenya-entry-caption"><i18n:text>Group ID</i18n:text></td>
              <td>
                 <xsl:choose>
                   <xsl:when test="@new = 'true'">
                     <input class="lenya-form-element" name="group-id" type="text" value="{id}"/>
                   </xsl:when>
                   <xsl:otherwise>
                     <xsl:value-of select="id"/>
                   </xsl:otherwise>
                 </xsl:choose>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Name</i18n:text></td>
              <td>
                <input class="lenya-form-element" name="name" type="text" value="{name}"/>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption" valign="top"><i18n:text>Description</i18n:text></td>
              <td>
                <textarea class="lenya-form-element" name="description"><xsl:value-of select="description"/>&#160;</textarea>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input i18n:attr="value" name="submit" type="submit" value="Save"/>
                &#160;
                <input i18n:attr="value" name="cancel" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
    
    </td>
    </tr>
    </table>
    
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
