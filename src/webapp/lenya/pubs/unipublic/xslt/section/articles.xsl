<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="section/articles" mode="articles">
  <tr>
    <td width="5" bgcolor="white" valign="top">&#160;</td>
    <td width="295" bgcolor="white" class="tsr-text"><br />

      <xsl:for-each select="article">
        <p>
          <a href="{@href}/"><span class="tsr-title"><xsl:apply-templates select="body.head/hedline/hl1"/></span></a><br />
          <xsl:apply-templates select="body.head/abstract"/> (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>)
        </p>
      </xsl:for-each>

    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
