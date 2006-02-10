<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<xsl:param name="index"/>
<xsl:param name="id"/>

<xsl:template match="/">  
  <lucene:delete indexid="{$index}">
    <lucene:document uid="{$id}"/>
  </lucene:delete>
</xsl:template>

</xsl:stylesheet>
