<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://www.lenya.org/2003/cms-page"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title>Edit User</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    <table class="lenya-table">
      <tr>
        <th>Edit User</th>
      </tr>
      <tr>
        <td>
          <div class="lenya-pad">
            <form method="GET">
              <input name="lenya.usecase" type="hidden" value="user-modify"/>
              <input name="lenya.step" type="hidden" value="modify"/>
              <table class="lenya-table-noborder">
                <tr>
                  <td>User ID</td>
                  <td>
                    <input class="lenya-form-element" name="user-id" readonly="true" type="text">
                      <xsl:attribute name="value">
                        <xsl:value-of select="id"/>
                      </xsl:attribute>
                    </input>
                  </td>
                </tr>
                <tr>
                  <td>Full Name</td>
                  <td>
                    <input class="lenya-form-element" name="fullname" type="text">
                      <xsl:attribute name="value">
                        <xsl:value-of select="fullname"/>
                      </xsl:attribute>
                    </input>
                  </td>
                </tr>
                <tr>
                  <td>Email</td>
                  <td>
                    <input class="lenya-form-element" name="email" type="text">
                      <xsl:attribute name="value">
                        <xsl:value-of select="email"/>
                      </xsl:attribute>
                    </input>
                  </td>
                </tr>
                <tr>
                  <td>Password</td>
                  <td>
                    <input class="lenya-form-element" name="password" type="password"/>
                  </td>
                </tr>
                <tr>
                  <td>Confirm Password</td>
                  <td>
                    <input class="lenya-form-element" name="confirm-password" type="password"/>
                  </td>
                </tr>
                <tr>
                  <td>Group</td>
                  <td>
                    <select class="lenya-form-element" name="groups" size="1">
                      <xsl:apply-templates select="groups"/>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td/>
                  <td>
                    <input type="submit" value="Save"/>
                  </td>
                </tr>
              </table>
            </form>
          </div>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  
  <xsl:template match="groups">
    <xsl:apply-templates select="group"/>
  </xsl:template>
  
  
  <xsl:template match="group">
    <option>
      <xsl:value-of select="."/>
    </option>
  </xsl:template>
  
  
</xsl:stylesheet>
