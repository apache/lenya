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
      <page:title>IP Range Overview: <xsl:value-of select="iprange/id"/></page:title>
      <page:body>
    <table class="lenya-noborder">
    <tr>
    <td>
    
    <div class="lenya-box">
      <div class="lenya-box-title">Group Affiliation</div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{continuation}.continuation">
          <input type="hidden" name="iprange-id" value="{id}"/>
          
                <table class="lenya-table-noborder-nopadding">
                  <tr>
                    <td><strong>IP range groups</strong></td>
                    <td/>
                    <td><strong>All groups</strong></td>
                  </tr>
                  <tr>
                    <td valign="middle">
                      <select name="iprange_group" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="iprange-groups"/>
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
                  <input type="submit" name="submit" value="Submit"/>
                  &#160;
                  <input type="submit" name="cancel" value="Cancel"/>
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
      <xsl:value-of select="."/>
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
