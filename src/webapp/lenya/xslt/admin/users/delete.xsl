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
      <page:title>Delete User</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    <table class="lenya-table">
      <tr>
        <th>Delete User</th>
      </tr>
      <tr>
        <td>
          <div class="lenya-pad">
          <form method="GET">
            <input name="lenya.usecase" type="hidden" value="user-delete"/>
            <input name="lenya.step" type="hidden" value="delete"/>
            <input name="user-id" type="hidden">
              <xsl:attribute name="value">
                <xsl:value-of select="id"/>
              </xsl:attribute>
            </input>
            <p> Really delete user "<xsl:value-of select="id"/>" (<xsl:value-of select="fullName"/>)? </p>
            <input type="submit" value="Delete"/>
            <input type="submit" value="Cancel"/>
          </form>
          </div>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  
</xsl:stylesheet>
