<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:sch="http://www.ascc.net/xml/schematron" 
 xmlns:zvon="http://zvon.org/schematron"
 xmlns:request="http://xml.apache.org/cocoon/requestgenerator/2.0"
 xmlns:tf="http://chaperon.sourceforge.net/schema/textfragment/1.0"
 >

<!-- 

	This StyleSheet converts the output of the Validator and wraps it 
	with 'source:write' tags, so the SourceWritingTransformer can save it
	only if there have been no validation errors.

	NB. the indentation of these templates effects the output document
	
	NB. it can get tricky to decide which namespaces need to be in which stylesheet, 
	to make sure the wrong ones don't end up being written to source
		
-->

 
<xsl:template match="/">
  <div>
  <source:write xml:space="preserve" xmlns:source="http://apache.org/cocoon/source/1.0">
			<source:source create="true">content/xdocs/<xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'target']/request:value"/></source:source>
			<source:fragment><document>
      <header>
        <title><xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'title']/request:value"/></title>
      </header>
      <body>
      
        <tf:textfragment>
          <xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'content']/request:value"/> 
        </tf:textfragment> 
    
	 </body>
	</document></source:fragment>
		</source:write>
		

  </div>	 
</xsl:template>

<!-- copy content ** this copies any tags into the source, when the source is re-parsed, they will be XML. **
									** this is highly dangerous because no validation takes place !!!!!                      **
	<xsl:template match="para"><para><xsl:value-of disable-output-escaping="yes" select="text()"/></para></xsl:template>
	-->
<!-- copy anything -->

<!--
  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>
-->
</xsl:stylesheet>
