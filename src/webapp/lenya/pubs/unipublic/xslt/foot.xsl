<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="NewsLines" mode="Article_copyright">
  <tr>
    <td width="187"></td>
    <td width="10" bgcolor="white">&#160;</td>
    <td bgcolor="white" width="388"><br />
     <div align="left"><a href="#topofpage"><font size="1">zum Anfang<br /> <br />
      </font></a> <img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
      <font size="1"><xsl:apply-templates select="CopyrightLine" mode="copyright"/>
      <xsl:apply-templates select="../../NewsManagement/RevisionDate"/>,
      <a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">Impressum</a>
      </font></div>
    </td>
  </tr>
</xsl:template>

<xsl:template match="CopyrightLine" mode="copyright">
  <xsl:apply-templates/>,
</xsl:template>

<xsl:template match="articles" mode="Section_copyright">
  <tr>
    <td colspan="2" bgcolor="white"><br />
      <div align="left"><a href="#topofpage"><!--<img height="4" width="10" src="{$img-unipub}/1.gif"/>-->&#160;&#160;<font size="1">zum Anfang<br /><br />
        </font></a><img height="4" width="10" src="{$img-unipub}/1.gif"/><img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
        <img height="4" width="10" src="{$img-unipub}/1.gif"/><font size="1">&#169; Universit&#228;t Z&#252;rich,&#160;<xsl:apply-templates select="article[1]/body.head/dateline/story.date/@norm"/>,&#160;
        <a href="/ssi_unipublic/impressum.html">Impressum</a></font></div><br />
    </td>
  </tr>
</xsl:template>

<xsl:template match="Articles" mode="Front_copyright">
  <tr>
    <td width="135"></td>
    <td colspan="2"><br />
      <div align="left"><a href="#topofpage"><!--<img height="4" width="4" src="{$img-unipub}/1.gif"/>-->&#160;<font size="1">zum Anfang<br /><br /></font></a>
        <img height="4" width="4" src="{$img-unipub}/1.gif"/><img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
        <img height="4" width="4" src="{$img-unipub}/1.gif"/><font size="1">&#169; Universit&#228;t Z&#252;rich,&#160;<xsl:apply-templates select="Article[1]/body.head/dateline/story.date/@norm"/>,&#160;<a href="/ssi_unipublic/impressum.html">Impressum</a></font></div><br /></td>
  </tr>
</xsl:template>

<xsl:template match="RevisionDate">
  <xsl:value-of select="@day"/>.<xsl:value-of select="@month"/>.<xsl:value-of select="@year"/>
</xsl:template>
                                                                                                                                            
</xsl:stylesheet>
