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
    <div style="margin: 10px 0px">
      <xsl:call-template name="add-user"/>
    </div>
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
        <xsl:value-of select="name"/>
      </td>
      <xsl:apply-templates select="groups"/>
      <td style="vertical-align: middle">
        <form method="GET" action="users/lenya.usecase.delete_user">
          <input name="user-id" type="hidden" value="{id}"/>
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
    <a href="groups/{@id}/index.html"><xsl:value-of select="@id"/></a>
    <xsl:if test="position() != last()">, <xsl:text/>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="add-user">
    <form method="GET" action="users/lenya.usecase.add_user">
      <input type="submit" value="Add User"/>
    </form>
  </xsl:template>
  
  
</xsl:stylesheet>
