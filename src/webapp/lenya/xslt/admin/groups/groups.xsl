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
      <page:title>Group Administration</page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="groups">
    <div style="margin: 10px 0px">
      <xsl:call-template name="add-group"/>
    </div>
    <table cellspacing="0" class="lenya-table">
      <tr>
        <th>Group ID</th>
        <th>Name</th>
        <th></th>
      </tr>
      <xsl:apply-templates select="group">
        <xsl:sort select="id"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>
  
  
  <xsl:template match="group">
    <tr>
      <td style="vertical-align: middle">
        <a href="groups/{id}/index.html"><xsl:value-of select="id"/></a>
      </td>
      <td style="vertical-align: middle">
        <xsl:value-of select="name"/>
      </td>
      <td style="vertical-align: middle">
        <form method="GET" action="index">
          <input name="lenya.usecase" type="hidden" value="group-delete"/>
          <input name="lenya.step" type="hidden" value="showscreen"/>
          <input name="group-id" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="id"/>
            </xsl:attribute>
          </input>
          <input type="submit" value="Delete"/>
        </form>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template name="add-group">
    <form method="GET" action="groups/lenya.usecase.add_group">
      <input type="submit" value="Add Group"/>
    </form>
  </xsl:template>
  
  
</xsl:stylesheet>
