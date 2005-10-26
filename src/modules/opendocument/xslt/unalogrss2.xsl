<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
		xmlns:purl="http://purl.org/rss/1.0/"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		xmlns:cinclude="http://apache.org/cocoon/include/1.0">

<!--
 unalog2.xsl: write out XML stream and change Unalog folder content
                @author <a href="http://librarycog.uwindsor.ca">art rhyno</a>
-->

<xsl:template match="/">

<xsl:text disable-output-escaping="yes">
	<![CDATA[
<!DOCTYPE xbel PUBLIC "+//IDN python.org//DTD XML Bookmark Exchange Language 1.0//EN//XML" "http://pyxml.sourceforge.net/topics/dtds/xbel-1.0.dtd">
	]]>
</xsl:text>
	<xbel version="1.0">
	<xsl:apply-templates select="*/xbel/*"/>
	</xbel>
</xsl:template>


<xsl:template match="node() | @*">

        <xsl:copy>
                <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>

</xsl:template>

<!-- Pull in info from unalog -->
<!-- some trouble with XBEL for some reason -->
<!--
<xsl:template match="folder[contains(.,'Unalog Bookmarks')]">
	<folder>
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>
		<xsl:attribute name="folded">
			<xsl:value-of select="@folded"/>
		</xsl:attribute>
		<xsl:copy-of select="/retrieval-results/unalog/xbel/*"/>
	</folder>
</xsl:template>
-->

<!-- Pull in info from unalog RSS feed -->
<xsl:template match="folder[contains(.,'Unalog Bookmarks')]">
	<folder>
		<title>Unalog Bookmarks</title>
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>
		<xsl:attribute name="folded">
			<xsl:value-of select="@folded"/>
		</xsl:attribute>
		<!-- get each entry, also eliminate duplicates 
			and merge comments  -->
		<xsl:for-each select="/retrieval-results/unalog/rdf:RDF/
		purl:item[not(@rdf:about=following::purl:item/@rdf:about)]">
			<bookmark>
			<xsl:variable name="theHref">
				<xsl:choose>
				<xsl:when test="string-length(@rdf:about) &gt; 0">
					<xsl:value-of select="@rdf:about"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>http://unalog.com</xsl:text>
				</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:attribute name="href">
				<xsl:value-of select="$theHref"/>
			</xsl:attribute>
			<xsl:attribute name="visited">
				<xsl:value-of select="dc:date"/>
			</xsl:attribute>
			<title>
			<xsl:value-of select="purl:title"/>
			</title>
			<info>
				<xsl:attribute name="owner">
					<xsl:value-of select="dc:contributor"/>
				</xsl:attribute>
			</info>
			<desc>
				<xsl:for-each select="/retrieval-results/unalog/rdf:RDF/
				purl:item[@rdf:about=$theHref]">
				<xsl:text>From: </xsl:text>
				<xsl:value-of select="dc:contributor"/>
				<xsl:if test="string-length(purl:description) &gt; 0">
				<xsl:text>- </xsl:text>
				<xsl:value-of select="purl:description"/>
				</xsl:if>
				<xsl:if test="position() &lt; last()">
					<xsl:text>/ </xsl:text>
				</xsl:if>
				</xsl:for-each>
			</desc>
			</bookmark>
		</xsl:for-each>
	</folder>
</xsl:template>

</xsl:stylesheet>