<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes"/>

<xsl:key name="targets-names" match="target" use="@name"/>

<xsl:template match="/">
  <html>
    <head>
      <title>Ant/Centipede target dependency chain</title>
    </head>
    <body>
      <ul>
    	  <xsl:apply-templates select="project/target">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </ul>
    </body>
  </html>
</xsl:template>

<xsl:template match="target">
  <li><xsl:value-of select="@name"/></li>
  <xsl:if test="@depends | antcall">
    <ul>
      <xsl:if test="string-length(@depends) &gt; 1">
        <li>depends:
          <ul>
            <xsl:call-template name="process-depends-attrs">
              <xsl:with-param name="depends-string" select="@depends"/>
            </xsl:call-template>
          </ul>
        </li>
      </xsl:if>
      <xsl:if test="antcall">
        <li>calls:
          <xsl:variable name="calltarget" select="antcall/@target"/>
          <ul>
            <xsl:for-each select="key('targets-names',$calltarget)">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </ul>
        </li>
      </xsl:if>
    </ul>
  </xsl:if>
</xsl:template>

<xsl:template name="process-depends-attrs">
  <xsl:param name="depends-string"/>
  <xsl:choose>
    <xsl:when test="contains($depends-string,',')">
      <xsl:variable name="substr" select="substring-before(normalize-space($depends-string),',')"/>
      <xsl:variable name="remains" select="normalize-space(substring-after(normalize-space($depends-string),concat($substr,',')))"/>
      <xsl:apply-templates select="key('targets-names',$substr)"/>
      <xsl:if test="string-length($remains) &gt; 1">
        <xsl:call-template name="process-depends-attrs">
          <xsl:with-param name="depends-string" select="$remains"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="key('targets-names',$depends-string)"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet><!-- Stylesheet edited using Stylus Studio - (c)1998-2002 eXcelon Corp. -->