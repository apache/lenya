<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/" xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="tablecolor">orange</xsl:variable>
<!-- context_prefix is just a temporary setting, will be given by general logicsheet -->
<xsl:variable name="CONTEXT_PREFIX">/lenya/oscom</xsl:variable>
<xsl:variable name="images"><xsl:value-of select="$CONTEXT_PREFIX"/>/images</xsl:variable>

<xsl:include href="navigation.xsl"/>
<xsl:include href="berkeley.xsl"/>
 
</xsl:stylesheet>  
