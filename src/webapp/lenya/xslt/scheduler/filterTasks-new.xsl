<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://www.lenya.org/2002/sch">
  
  <xsl:variable name="params" select="/sch:scheduler/sch:parameters"/>
  <xsl:param name="documentType"/>

  <xsl:template match="sch:doctypes">
  <sch:test id="{$documentType}"></sch:test>
    <sch:tasks>
      <xsl:apply-templates/>
    </sch:tasks>
  </xsl:template>
  
  <xsl:template match="sch:doctypes/sch:doc">
    <xsl:if test="$documentType = '' or @type = $documentType">
        <xsl:for-each select="sch:tasks/sch:task">
          <sch:task id="{@id}">
            <label>
              <xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = current()/@id]/sch:label"/>
            </label>
          </sch:task>
        </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sch:scheduler/sch:tasks"/>

  <xsl:template match="* | @*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
