<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title><xsl:value-of select="title"/></page:title>
      <page:body>
        <xsl:apply-templates select="iprange"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="iprange">
    
    <table class="lenya-noborder">
    <tr>
    <td valign="top">
    
    <div class="lenya-box">
      <div class="lenya-box-title">IP Range</div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{/page/continuation}.continuation">
          <table class="lenya-table-noborder">
            
            <xsl:apply-templates select="message"/>
            
            <tr>
              
              <td class="lenya-entry-caption">IP&#160;range&#160;ID</td>
              <td>
                 <xsl:choose>
                   <xsl:when test="@new = 'true'">
                     <input class="lenya-form-element" name="iprange-id" type="text" value="{id}"/>
                   </xsl:when>
                   <xsl:otherwise>
                     <xsl:value-of select="id"/>
                   </xsl:otherwise>
                 </xsl:choose>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption">Name</td>
              <td>
                <input class="lenya-form-element" name="name" type="text" value="{name}"/>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption">Description</td>
              <td>
                <input class="lenya-form-element" name="description" type="text" value="{description}"/>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input name="submit" type="submit" value="Submit"/>
                &#160;
                <input name="cancel" type="submit" value="Cancel"/>
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
