<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:param name="lenya.usecase"/>
  
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  <xsl:variable name="user-id" select="/uc:create/uc:user-id"/>
  
  <xsl:template match="/">
    <page:page xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
      <page:title><xsl:call-template name="title"/></page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="uc:confirm">
    <div class="lenya-box">
      <div class="lenya-box-title"><xsl:value-of select="uc:form-title"/></div>
      <div class="lenya-box-body">

          <xsl:apply-templates select="uc:error-messages"/>
        

          <xsl:call-template name="fields"/>
        
      </div>
    </div>
  </xsl:template>

<xsl:template match="uc:error-messages">
  <table class="lenya-table-noborder">
    <xsl:if test="uc:message">
       <tr>
          <td class="lenya-entry-caption">Problem:</td>
          <td><xsl:apply-templates select="uc:message"/></td>
       </tr>
    </xsl:if>
  </table>
</xsl:template>

<xsl:template match="uc:message">
  <span class="lenya-form-message-error"><xsl:value-of select="."/></span><br/>
</xsl:template>


<xsl:template name="title">Upload Document</xsl:template>

<xsl:template name="fields"/>


</xsl:stylesheet>
