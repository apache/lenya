<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:include href="../variables.xsl"/> 

<xsl:template match="MainColumn">
  <xsl:apply-templates select="Articles"/>
</xsl:template>

<xsl:template match="Articles">

<table cellspacing="0" cellpadding="0" width="315"
bgcolor="#CCCC99" border="0" bordercolor="blue">
<tbody>
<tr>
<td width="3" height="1"><img height="1" alt=" "
src="{$img-unipub}/spacer.gif" width="1" border="0" /></td>
<td width="153" height="1"></td>
<td width="3" height="1"><img height="1" alt=" "
src="{$img-unipub}/spacer.gif" width="1" border="0" /></td>
<td width="153" height="1"></td>
<td width="3" height="1"><img height="1" alt=" "
src="{$img-unipub}/spacer.gif" width="1" border="0" /></td>
</tr>

    <xsl:apply-templates select="../Articles" mode="top"/>

<tr height="1">
<td width="3" height="1"><img height="1" alt=" "
src="{$img-unipub}/spacer.gif" width="1" border="0" /></td>
<td width="153" height="1"></td>
<td width="3" height="1"></td>
<td width="153" height="1"></td>
<td width="3" height="1"></td>
</tr>
</tbody>
</table>

<br />

<xsl:for-each select="Article">
<xsl:if test="position()>=3">

            <table border="0" bordercolor="green" cellpadding="0" cellspacing="0" width="315">
              <tr>
                <td colspan="3"><a href="{$unipublic}{$view}/{@channel}/{@section}/"><img src="{$img-unipub}/t_{@section}.gif" width="315" 
height="13" border="0" alt="{@section}"/></a></td>
              </tr>

              <tr>
                <td bgcolor="white" colspan="3" height="3"><img height="1" alt=" "
src="{$img-unipub}/spacer.gif" width="1" border="0" /></td>
              </tr>

              <tr>
                <td width="4" bgcolor="white">&#160;</td>
                <td bgcolor="white" class="tsr-text">
                  <p>
                    <xsl:apply-templates select="body.head" mode="media-column"/>
                    <span class="tsr-title"><a href="{@href}/"><xsl:apply-templates select="body.head/hedline/hl1"/></a> </span><br />
                    <xsl:apply-templates select="body.head/abstract"/>
                  </p>
                  </td>
                <td width="4" bgcolor="white">&#160;</td>
              </tr>
              <tr>
                <td bgcolor="white" colspan="3" height="3"><img height="1" alt=" "
src="{$img-unipub}/spacer.gif" width="1" border="0" /></td>
              </tr>
            </table>
            <br />

    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template match="Articles" mode="top">
  <tr>
    <xsl:for-each select="Article">
      <xsl:if test="position()&#60;=2">
        <td width="3" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
        <td align="right" width="153" bgcolor="#CCCC99" valign="bottom">
          <xsl:apply-templates select="body.head" mode="media-top"/>
        </td>
      </xsl:if>
    </xsl:for-each>
    <td width="3"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
  </tr>

  <tr>
    <td width="3"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
    <td width="153" valign="top" bgcolor="white" >
      <table cellspacing="0" cellpadding="3" border="0" bordercolor="green">
       <tr>
       <td class="tsr-text"><a href="{Article[1]/@href}/"><span class="tsr-title"><xsl:apply-templates select="Article[1]/body.head/hedline/hl1"/></span></a><br />
      <xsl:apply-templates select="Article[1]/body.head/abstract"/>(<xsl:apply-templates select="Article[1]/body.head/dateline/story.date/@norm"/>)</td>
       </tr>
      </table>
    </td>
    <td width="3"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
    <td width="153" valign="top" bgcolor="white">
      <table cellspacing="0" cellpadding="3" border="0" bordercolor="green">
       <tr>
       <td class="tsr-text"><a href="{Article[2]/@href}/"><span class="tsr-title"><xsl:apply-templates select="Article[2]/body.head/hedline/hl1"/> </span></a><br />
      <xsl:apply-templates select="Article[2]/body.head/abstract"/>(<xsl:apply-templates select="Article[2]/body.head/dateline/story.date/@norm"/>)</td>
       </tr>
     </table>
    </td>
    <td width="3"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
  </tr>
</xsl:template>


</xsl:stylesheet>
