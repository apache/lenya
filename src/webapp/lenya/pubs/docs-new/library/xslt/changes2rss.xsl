<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="config-file" select="'../../skinconf.xml'"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>

  <xsl:output method = "xml"
    version="1.0" 
    encoding="ISO-8859-1" 
    indent="yes"  
    doctype-public="-//Netscape Communications//DTD RSS 0.91//EN"                   
    doctype-system="http://my.netscape.com/publish/formats/rss-0.91.dtd"                   
    />

  <xsl:template match="status">

    <xsl:variable name="changes-url"
      select="concat($config/project-url, '/changes.html')"/>

    <rss version="0.91">
      <channel>
        <title><xsl:value-of select="$config/project-name"/> Changes</title>

        <link><xsl:value-of select="$changes-url"/></link>

        <description><xsl:value-of select="$config/project-name"/> Changes</description>

        <language>en-us</language>

        <xsl:for-each select="changes/release[1]/action">
          <item>
            <title>
              <xsl:value-of select="@context" />
              <xsl:text> </xsl:text>
              <xsl:value-of select="@type" />

              <xsl:if test="@type='fix' and @fixes-bug">
                (bug <xsl:value-of select="@fixes-bug" />)
              </xsl:if>

            </title>

            <link><xsl:value-of select="$changes-url"/></link>

            <description>
              <xsl:value-of select="@context" />
              <xsl:text> </xsl:text>
              <xsl:value-of select="@type" />
              by 
              <xsl:value-of select="@dev" />
              <xsl:if test="@type='fix' and @fixes-bug">
                (fixes bug <xsl:value-of select="@fixes-bug" />)
              </xsl:if>
              :
              <xsl:value-of select="." />
              <xsl:if test="@due-to"> Thanks to <xsl:value-of select="@due-to" />.</xsl:if> 
            </description>
          </item>
        </xsl:for-each>
      </channel>
    </rss>
  </xsl:template>
</xsl:stylesheet>

