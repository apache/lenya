<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:wiki="http://chaperon.sourceforge.net/grammar/wiki/1.0">

 <xsl:output indent="yes" method="xml"/>

 <xsl:template match="/">
  <html>
   <body>
    <xsl:apply-templates/>
   </body>
  </html>
 </xsl:template>


<!-- Identity transformation template -->			
<xsl:template match="@* | * | comment() | processing-instruction() | text()"> 
	<xsl:copy> 
		<xsl:apply-templates select="@* | * | comment() | processing-instruction() | text()"/> 
	</xsl:copy> 
</xsl:template> 
    

</xsl:stylesheet>
