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
      <page:title>User Administration</page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="users">
    <table cellspacing="0" class="lenya-table">
      <tr>
        <th>User ID</th>
        <th>Full Name</th>
        <th>Groups</th>
        <th></th>
      </tr>
      <xsl:apply-templates select="user">
        <xsl:sort select="id"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>
  
  
  <xsl:template match="user">
    <tr>
      <td style="vertical-align: middle">
        <a href="users/{id}/index.html"><xsl:value-of select="id"/></a>
      </td>
      <td style="vertical-align: middle">
        <xsl:value-of select="fullName"/>
      </td>
      <xsl:apply-templates select="groups"/>
      <td style="vertical-align: middle">
        <form method="GET" action="index">
          <input name="lenya.usecase" type="hidden" value="user-delete"/>
          <input name="lenya.step" type="hidden" value="showscreen"/>
          <input name="user-id" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="id"/>
            </xsl:attribute>
          </input>
          <input type="submit" value="Delete"/>
        </form>
      </td>
    </tr>
  </xsl:template>
  
  
  <xsl:template match="groups">
   <td style="vertical-align: middle">
      <xsl:apply-templates select="group"/>
    </td>
  </xsl:template>
  
  
  <xsl:template match="group">
    <xsl:value-of select="."/>
    <xsl:if test="position() != last()">, <xsl:text/>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
