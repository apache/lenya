<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:h="http://apache.org/cocoon/request/2.0"
   exclude-result-prefixes="h">

<xsl:template match="/">
<data>
<newresource>
<xsl:attribute name="id"><xsl:value-of select="/h:request/h:requestParameters/h:parameter[@name='id']/h:value"/></xsl:attribute>
<xsl:attribute name="type"><xsl:value-of select="/h:request/h:requestParameters/h:parameter[@name='type']/h:value"/></xsl:attribute>
<xsl:attribute name="unid"><xsl:value-of select="/h:request/h:requestParameters/h:parameter[@name='unid']/h:value"/></xsl:attribute>
</newresource>
</data>
</xsl:template>


</xsl:stylesheet> 
