<?xml version="1.0" encoding="UTF-8"?>

<!--
  $Id: ipranges.xsl,v 1.2 2004/02/18 19:26:08 roku Exp $
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
      <page:title><i18n:text>IP Range Administration</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="ipranges">
    <div style="margin: 10px 0px">
      <xsl:call-template name="add-iprange"/>
    </div>
    <table cellspacing="0" class="lenya-table">
      <tr>
        <th><i18n:text>IP Range ID</i18n:text></th>
        <th><i18n:text>Name</i18n:text></th>
        <th><i18n:text>Groups</i18n:text></th>
        <th></th>
      </tr>
      <xsl:apply-templates select="iprange">
        <xsl:sort select="id"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>
  
  
  <xsl:template match="iprange">
    <tr>
      <td style="vertical-align: middle">
        <a href="ipranges/{id}/index.html"><xsl:value-of select="id"/></a>
      </td>
      <td style="vertical-align: middle">
        <xsl:value-of select="name"/>
      </td>
      <xsl:apply-templates select="groups"/>
      <td style="vertical-align: middle">
        <form method="GET" action="ipranges/lenya.usecase.delete_iprange">
          <input name="iprange-id" type="hidden" value="{id}"/>
          <input i18n:attr="value" type="submit" value="Delete"/>
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
    <a href="../groups/{@id}/index.html"><xsl:value-of select="@id"/></a>
    <xsl:if test="position() != last()">, <xsl:text/>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="add-iprange">
    <form method="GET" action="ipranges/lenya.usecase.add_iprange">
      <input i18n:attr="value" type="submit" value="Add IP Range"/>
    </form>
  </xsl:template>
  
  
</xsl:stylesheet>
