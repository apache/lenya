<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
PathUtils.xsl

A set of XSLT templates useful for parsing URI paths:

dirname: return the directory part of a path
filename: return the file part of a path
ext: return the last extension of the filename in a path
filename-noext: return the file part of a path without its last extension

@author Jeff Turner <jefft@apache.org>
$Id: pathutils.xsl,v 1.1 2003/04/02 09:00:25 andreas Exp $
-->

<!-- Returns the directory part of a path.  Equivalent to Unix 'dirname'.
Examples:
'' -> ''
'foo/index.html' -> 'foo/'
-->
<xsl:template name="dirname">
  <xsl:param name="path" />
  <xsl:if test="contains($path, '/')">
    <xsl:value-of select="concat(substring-before($path, '/'), '/')" />
    <xsl:call-template name="dirname">
      <xsl:with-param name="path"
        select="substring-after($path, '/')" />
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!-- Normalized (..'s eliminated) version of 'dirname' -->
<xsl:template name="dirname-nz">
  <xsl:param name="path" />
  <xsl:call-template name="normalize">
    <xsl:with-param name="path">
      <xsl:call-template name="dirname">
        <xsl:with-param name="path" select="$path" />
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>


<!-- Returns the filename part of a path.  Equivalent to Unix 'basename'
Examples:
'index.html'  ->  'index.html' 
'foo/bar/'  ->  '' 
'foo/bar/index.html'  ->  'index.html' 
-->
<xsl:template name="filename">
  <xsl:param name="path"/>
  <xsl:choose>
    <xsl:when test="contains($path, '/')">
      <xsl:call-template name="filename">
        <xsl:with-param name="path" select="substring-after($path, '/')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$path"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Returns the last extension of a filename in a path.
Examples:
'index.html'  ->  '.html' 
'index.dtdx.html'  ->  '.html' 
'foo/bar/'  ->  '' 
'foo/bar/index.html'  ->  '.html' 
'foo/bar/index'  ->  '' 
-->
<xsl:template name="ext">
  <xsl:param name="path"/>
  <xsl:param name="subflag"/> <!-- Outermost call? -->
  <xsl:choose>
    <xsl:when test="contains($path, '.')">
      <xsl:call-template name="ext">
        <xsl:with-param name="path" select="substring-after($path, '.')"/>
        <xsl:with-param name="subflag" select="'sub'"/>
      </xsl:call-template>
    </xsl:when>
    <!-- Handle extension-less filenames by returning '' -->
    <xsl:when test="not($subflag) and not(contains($path, '.'))">
      <xsl:text/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="concat('.', $path)"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Returns a filename of a path stripped of its last extension.
Examples:
'foo/bar/index.dtdx.html' -> 'index.dtdx'
-->
<xsl:template name="filename-noext">
  <xsl:param name="path"/>
  <xsl:variable name="filename">
    <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="ext">
    <xsl:call-template name="ext">
      <xsl:with-param name="path" select="$filename"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="substring($filename, 1, string-length($filename) - string-length($ext))"/>
</xsl:template>

<!-- Returns a path with the filename stripped of its last extension.
Examples:
'foo/bar/index.dtdx.html' -> 'foo/bar/index.dtdx'
-->
<xsl:template name="path-noext">
  <xsl:param name="path"/>
  <xsl:variable name="ext">
    <xsl:call-template name="ext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="substring($path, 1, string-length($path) - string-length($ext))"/>
</xsl:template>

<!-- Normalized (..'s eliminated) version of 'path-noext' -->
<xsl:template name="path-noext-nz">
  <xsl:param name="path" />
  <xsl:call-template name="normalize">
    <xsl:with-param name="path">
      <xsl:call-template name="path-noext">
        <xsl:with-param name="path" select="$path" />
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>


<!-- Normalizes a path, converting '/' to '\' and eliminating ..'s
Examples:
'foo/bar/../baz/index.html' -> foo/baz/index.html'
-->
<xsl:template name="normalize">
  <xsl:param name="path"/>
  <xsl:variable name="path-" select="translate($path, '\', '/')"/>
  <xsl:choose>
    <xsl:when test="contains($path-, '/../')">

      <xsl:variable name="pa" select="substring-before($path-, '/../')"/>
      <xsl:variable name="th" select="substring-after($path-, '/../')"/>
      <xsl:variable name="pa-">
        <xsl:call-template name="dirname">
          <xsl:with-param name="path" select="$pa"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="pa-th" select="concat($pa-, $th)"/>
      <xsl:call-template name="normalize">
        <xsl:with-param name="path" select="$pa-th"/>
      </xsl:call-template>
    </xsl:when>

    <xsl:otherwise>
      <xsl:value-of select="$path-"/>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

<!--
Uncomment this to test.
Usage: saxon pathutils.xsl pathutils.xsl path=foo/bar

<xsl:param name="path" select="'/foo/bar/../baz/index.html'"/>
<xsl:template match="/">
  <xsl:message>
    path           = <xsl:value-of select="$path"/>
    normalize      = <xsl:call-template name="normalize">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    dirname        = <xsl:call-template name="dirname">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    dirname-nz     = <xsl:call-template name="dirname-nz">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    filename       = <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    ext            = <xsl:call-template name="ext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    filename-noext = <xsl:call-template name="filename-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    path-noext     = <xsl:call-template name="path-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
    path-noext-nz  = <xsl:call-template name="path-noext-nz">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:message>
</xsl:template>
-->

</xsl:stylesheet>
