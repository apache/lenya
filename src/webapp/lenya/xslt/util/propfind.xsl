<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    >

<xsl:param name="depth"/>
<xsl:param name="href"/>

<xsl:template match="/">
<D:multistatus xmlns:D="DAV:">
<xsl:choose>
<xsl:when test="$depth='0'">
  <xsl:apply-templates select="dir:directory"/>
</xsl:when>
<xsl:when test="$depth='1'">
  <xsl:apply-templates select="dir:directory/dir:directory"/>
</xsl:when>
<xsl:when test="$depth='infinity'">
  Depth "infinity" not implemented yet!
</xsl:when>
<xsl:otherwise>
  No such Depth implemented: <xsl:value-of select="$depth"/>
</xsl:otherwise>
</xsl:choose>
</D:multistatus>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory">
<D:response xmlns:D="DAV:" xmlns:lp1="DAV:" xmlns:lp2="http://apache.org/dav/props/">
<xsl:if test="$depth='0'">
<D:href><xsl:value-of select="$href"/></D:href>
</xsl:if>
<xsl:if test="$depth='1'">
<D:href><xsl:value-of select="$href"/><xsl:value-of select="@name"/>/</D:href>
</xsl:if>
<D:propstat>
<D:prop>
<lp1:resourcetype><D:collection/></lp1:resourcetype>
<lp1:creationdate>2003-07-21T13:03:09Z</lp1:creationdate>
<lp1:getlastmodified>Mon, 21 Jul 2003 13:03:09 GMT</lp1:getlastmodified>
<lp1:getetag>"945fd7-4f-5c287d40"</lp1:getetag>
<D:supportedlock>
<D:lockentry>
<D:lockscope><D:exclusive/></D:lockscope>
<D:locktype><D:write/></D:locktype>
</D:lockentry>
<D:lockentry>
<D:lockscope><D:shared/></D:lockscope>
<D:locktype><D:write/></D:locktype>
</D:lockentry>
</D:supportedlock>
<D:lockdiscovery/>
<D:getcontenttype>httpd/unix-directory</D:getcontenttype>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
</xsl:template>

</xsl:stylesheet>
