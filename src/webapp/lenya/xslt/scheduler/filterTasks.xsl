<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:sch="http://www.wyona.org/2002/sch">
  
  <xsl:param name="documentType"/>

  <xsl:template match="sch:doctypes">
  <sch:test id="{$documentType}"></sch:test>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="sch:doctypes/sch:doc">
    <xsl:if test="@type = $documentType">
      <sch:tasks>
        <xsl:for-each select="sch:tasks/sch:task">
          <sch:task id="{@id}">
            <label>
              <xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = current()/@id]/sch:label"/>
            </label>
          </sch:task>
        </xsl:for-each>
      </sch:tasks>
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
