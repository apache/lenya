<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:cinclude="http://apache.org/cocoon/include/1.0">

<!-- unalog.xsl: copy input XML stream and set up cinclude directive
                @author <a href="http://librarycog.uwindsor.ca">art rhyno</a>
-->


<xsl:template match="/">
	<retrieval-results>
		<xsl:apply-templates/>
<!--
		<unalog>
    			<cinclude:include src="http://unalog.com/xbel"/>
		</unalog>
-->
		<unalog>
    			<cinclude:include src="http://unalog.com/rss"/>
		</unalog>
	</retrieval-results>
</xsl:template>


<xsl:template match="node() | @*">
        <xsl:copy>
                <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
</xsl:template>


</xsl:stylesheet>

