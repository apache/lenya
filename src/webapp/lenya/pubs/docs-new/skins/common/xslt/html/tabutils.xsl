<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


  <xsl:template name="unselected-tab-href">

    <xsl:param name="dir_index" select="'index.html'"/>

    <xsl:param name="tab"/> <!-- current 'tab' node -->
    <xsl:param name="path" select="$path"/>

    <xsl:if test="starts-with($tab/@href, 'http')">  <!-- Absolute URL -->
      <xsl:value-of select="$tab/@href"/>
    </xsl:if>
    <xsl:if test="not(starts-with($tab/@href, 'http'))">  <!-- Root-relative path -->
      <xsl:variable name="backpath">
        <xsl:call-template name="dotdots">
          <xsl:with-param name="path" select="$path"/>
        </xsl:call-template>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="$tab/@dir | $tab/@href"/>
        <!-- If we obviously have a directory, add /index.html -->
        <xsl:if test="$tab/@dir or substring($tab/@href, string-length($tab/@href),
          string-length($tab/@href)) = '/'">
          <xsl:text>/</xsl:text>
          <xsl:value-of select="$dir_index"/>
        </xsl:if>
      </xsl:variable>

      <xsl:value-of
        select="translate(normalize-space(translate($backpath, ' /', '/ ')), ' /', '/ ')"/>
      <!-- Link to backpath, normalizing slashes -->
    </xsl:if>
  </xsl:template>


  <!--
    The longest path of any tab, whose path is a subset of the current URL.  Ie,
    the path of the 'current' tab.
  -->
  <xsl:template name="longest-dir">
    <xsl:param name="tabfile"/>
    <xsl:for-each select="$tabfile/tabs/tab[starts-with($path, @dir|@href)]">
      <xsl:sort select="string-length(@dir|@href)"
        data-type="number" order="descending"/>
      <xsl:if test="position()=1">
        <xsl:value-of select="@dir|@href"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>


</xsl:stylesheet>

