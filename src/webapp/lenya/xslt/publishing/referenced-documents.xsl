<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"      
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

  <xsl:variable name="document-id"><xsl:value-of select="/usecase:publish/usecase:document-id"/></xsl:variable>
  <xsl:variable name="document-language"><xsl:value-of select="/usecase:publish/usecase:language"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/usecase:publish/usecase:task-id"/></xsl:variable>
  <xsl:variable name="referer"><xsl:value-of select="/usecase:publish/usecase:referer"/></xsl:variable>


  <xsl:template match="/usecase:publish">

    <xsl:text>Referenced Documents for Document ID: </xsl:text><xsl:value-of select="$document-id"/>
    <xsl:text>
</xsl:text>
    <xsl:text>List of referenced documents which are not published

</xsl:text>
    <xsl:apply-templates select="referenced-documents"/>
    
  </xsl:template>

  <xsl:template match="referenced-documents">
    <xsl:for-each select="referenced-document">
      <xsl:value-of select="@id"/><xsl:value-of select="."/><xsl:text> </xsl:text><xsl:value-of select="@href"/><xsl:text>
</xsl:text>
    </xsl:for-each>
  </xsl:template>
  

</xsl:stylesheet>  
