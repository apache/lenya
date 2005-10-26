<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		xmlns:purl="http://purl.org/rss/1.0/" 
		xmlns:dc="http://purl.org/dc/elements/1.1/">

<!-- xbel2ics.xsl: output iCal (ics) format as gleaned from Mozilla calendar 
                @author <a href="http://librarycog.uwindsor.ca">art rhyno</a>
-->

<xsl:output method="ics"/>
<xsl:strip-space elements="*"/>
<!-- use this for training, for-each -->
<xsl:template match="/xbel">
BEGIN:VCALENDAR<text/>
VERSION
 :2.0
PRODID
 :-//XBEL COnvertor - GPL<text/>
<xsl:apply-templates/><text/>
END:VCALENDAR
</xsl:template>

<xsl:template match="//bookmark">
BEGIN:VEVENT<text/>
<xsl:apply-templates/><text/>
END:VEVENT<text/>
</xsl:template>

<xsl:template match="title">
SUMMARY<text/>
 :Bookmark - <xsl:value-of select="."/>
UID
 :<xsl:value-of select="@id"/>
</xsl:template>

<xsl:template match="sql:row/sql:uid">

</xsl:template>

<xsl:template match="sql:row/sql:shortdesc">
DESCRIPTION<text/>
 :<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="sql:row/sql:parstat">
STATUS<text/>
<xsl:variable name="status" select="string(.)"/>
<xsl:choose>
<xsl:when test="$status='T'">
 :TENTATIVE<text/>
</xsl:when>
<xsl:when test="$status='D'">
 :DECLINED<text/>
</xsl:when>
<xsl:when test="$status='C'">
 :COMPLETED<text/>
</xsl:when>
<xsl:when test="$status='A'">
 :ACCEPTED<text/>
</xsl:when>
<xsl:otherwise>
 :IN-PROCESS<text/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="sql:row/sql:bookstart">
DTSTART
 :<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="sql:row/sql:bookend">
DTEND
 :<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="sql:row/sql:bookstamp">
DTSTAMP
 :<xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
