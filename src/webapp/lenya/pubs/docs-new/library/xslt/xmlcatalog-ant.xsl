<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <xsl:apply-templates select="//catalog"/>
</xsl:template>
 
<xsl:template match="catalog">
  <xmlcatalog id="forrest-schema">
    <xsl:apply-templates/>
<!-- now append entries for the old document-v10 DTDs -->
<dtd publicId="-//APACHE//DTD Documentation V1.0//EN"
     location="dtd/v10/document-v10.dtd"/>
<dtd publicId="-//APACHE//DTD Changes V1.0//EN"
     location="dtd/v10/changes-v10.dtd"/>
<dtd publicId="-//APACHE//DTD FAQ V1.0//EN"
     location="dtd/v10/faq-v10.dtd"/>
<dtd publicId="-//APACHE//DTD Todo V1.0//EN"
     location="dtd/v10/todo-v10.dtd"/>
  </xmlcatalog>
</xsl:template>
 
<xsl:template match="public">
  <dtd publicId="{@publicId}" location="{@uri}"/>
</xsl:template>

</xsl:stylesheet>
