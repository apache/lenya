<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: propfind.xsl,v 1.6 2004/03/13 12:42:09 gregor Exp $ -->
    
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dir="http://apache.org/cocoon/directory/2.0">
  
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
          <xsl:apply-templates select="dir:directory/dir:file"/>
        </xsl:when>
        <xsl:when test="$depth='infinity'"> Depth "infinity" not implemented yet!</xsl:when>
        <xsl:otherwise> No such Depth implemented: <xsl:value-of select="$depth"/>
        </xsl:otherwise>
      </xsl:choose>
    </D:multistatus>
  </xsl:template>
  
  
  <xsl:template match="dir:directory">
    <D:response xmlns:D="DAV:" xmlns:lp1="DAV:" xmlns:lp2="http://apache.org/dav/props/">
      <xsl:if test="$depth='0'">
        <D:href>
          <xsl:value-of select="$href"/>
        </D:href>
      </xsl:if>
      <xsl:if test="$depth='1'">
        <D:href>
          <xsl:value-of select="$href"/>
          <xsl:value-of select="@name"/>/</D:href>
      </xsl:if>
      <D:propstat>
        <D:prop>
          <lp1:resourcetype>
            <D:collection/>
          </lp1:resourcetype>
          <lp1:creationdate>2003-07-21T13:03:09Z</lp1:creationdate>
          <!--
<lp1:getlastmodified><xsl:value-of select="@date"/>  (<xsl:value-of select="@lastModified"/>)</lp1:getlastmodified>
-->
          <lp1:getlastmodified>Wed, 27 Aug 2003 12:50:23 GMT</lp1:getlastmodified>
          <lp1:getetag>"945fd7-4f-5c287d40"</lp1:getetag>
          <D:supportedlock>
            <D:lockentry>
              <D:lockscope>
                <D:exclusive/>
              </D:lockscope>
              <D:locktype>
                <D:write/>
              </D:locktype>
            </D:lockentry>
            <D:lockentry>
              <D:lockscope>
                <D:shared/>
              </D:lockscope>
              <D:locktype>
                <D:write/>
              </D:locktype>
            </D:lockentry>
          </D:supportedlock>
          <D:lockdiscovery/>
          <D:getcontenttype>httpd/unix-directory</D:getcontenttype>
        </D:prop>
        <D:status>HTTP/1.1 200 OK</D:status>
      </D:propstat>
    </D:response>
  </xsl:template>
  
  
  <xsl:template match="dir:file">
    <D:response xmlns:D="DAV:" xmlns:lp1="DAV:" xmlns:lp2="http://apache.org/dav/props/">
      <xsl:if test="$depth='0'">
        <D:href>
          <xsl:value-of select="$href"/>
        </D:href>
      </xsl:if>
      <xsl:if test="$depth='1'">
        <D:href>
          <xsl:value-of select="$href"/>
          <xsl:value-of select="@name"/>
        </D:href>
      </xsl:if>
      <D:propstat>
        <D:prop>
          <lp1:resourcetype/>
          <lp1:creationdate>2003-07-21T13:03:09Z</lp1:creationdate>
          <lp1:getcontentlength>
            <xsl:value-of select="@size"/>
          </lp1:getcontentlength>
          <!--
<lp1:getlastmodified><xsl:value-of select="@date"/> (<xsl:value-of select="@lastModified"/>)</lp1:getlastmodified>
-->
          <lp1:getlastmodified>Wed, 27 Aug 2003 08:25:20 GMT</lp1:getlastmodified>
          <lp1:getetag>"945fd7-4f-5c287d40"</lp1:getetag>
          <D:supportedlock>
            <D:lockentry>
              <D:lockscope>
                <D:exclusive/>
              </D:lockscope>
              <D:locktype>
                <D:write/>
              </D:locktype>
            </D:lockentry>
            <D:lockentry>
              <D:lockscope>
                <D:shared/>
              </D:lockscope>
              <D:locktype>
                <D:write/>
              </D:locktype>
            </D:lockentry>
          </D:supportedlock>
          <D:lockdiscovery/>
          <D:getcontenttype>text/xml</D:getcontenttype>
        </D:prop>
        <D:status>HTTP/1.1 200 OK</D:status>
      </D:propstat>
    </D:response>
  </xsl:template>
  
  
</xsl:stylesheet>
