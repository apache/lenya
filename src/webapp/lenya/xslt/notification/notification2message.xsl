<?xml version="1.0" encoding="iso-8859-1"?>


<!--
	This stylesheet filters the messages in notification.xconf.
	Only the message of the specified usecase is forwarded.
-->


<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
    >
    
<xsl:param name="usecase"/>

<xsl:template match="not:message[@usecase != $usecase]"/>

<xsl:template match="@*|node()">
	<xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>
 
</xsl:stylesheet>  
