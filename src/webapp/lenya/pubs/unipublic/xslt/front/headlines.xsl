<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">



<xsl:template match="MainColumn">
  <xsl:apply-templates select="Articles"/>
</xsl:template>

<xsl:template match="Articles">
  <table border="0" cellpadding="0" cellspacing="0" width="315">
    <tr height="5">
      <td width="5" bgcolor="#CCCC99" height="5"></td>
      <td width="150" bgcolor="#CCCC99" height="5"><img src="{$img-unipub}/1.gif" width="10" height="5" border="0"/></td>
      <td width="5" bgcolor="#CCCC99" height="5"></td>
      <td width="1" bgcolor="#CCCC99" height="5"></td>
      <td align="right" width="150" bgcolor="#CCCC99" height="5"></td>
      <td width="5" bgcolor="#CCCC99" height="5"></td>
    </tr>

    <xsl:apply-templates select="../Articles" mode="top"/>

    <tr>
      <td width="5"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      <td width="150"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      <td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      <td width="1"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      <td width="150"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      <td width="5"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
    </tr>
  <tr height="5">
    <td width="5" height="1"><img height="1" width="1" src="img_unipublic/1.gif"/></td>
    <td colspan="4" bgcolor="#cccc99" width="306" height="1"><img height="1" width="10" src="img_unipublic/1.gif"/></td>
    <td width="5" height="1"><img height="1" width="1" src="img_unipublic/1.gif"/></td>
  </tr>
  </table>

  <br />
 
  <xsl:for-each select="Article">
    <xsl:if test="position()>=3">

      <table border="0" cellpadding="0" cellspacing="0" width="316">
        <tr>
          <td colspan="3">
            <table border="0" cellpadding="0" cellspacing="0" width="316">
              <tr>
                <td colspan="3"><a href="{$unipublic}/{@channel}/{@section}/"><img src="{$img-unipub}/t_{@section}.gif" width="316" height="13" border="0" alt="{@section}"/></a></td>
              </tr>

              <tr>
                <td bgcolor="white" colspan="3">&#160;</td>
              </tr>

              <tr>
                <td width="4" bgcolor="white">&#160;</td>
                <td bgcolor="white" class="tsr-text">

                     <xsl:choose>
                       <xsl:when test="body.head/media/media-reference">
                  <p><a href="{@href}/">
<img src="{$unipublic}/{@href}/{body.head/media/media-reference/@source}" width="80" height="60" border="0" alt="{body.head/media/media-reference/@alternate-text}" align="right"/>
                     </a>
                  </p>
                       </xsl:when>
                       <xsl:otherwise>
                  <p>
                    <font color="red"> Attention: no image </font>
                  </p>
                       </xsl:otherwise>
                     </xsl:choose>
                     <span class="tsr-title"><a href="{@href}/"><xsl:apply-templates select="body.head/hedline/hl1"/></a> </span><br />
                    <xsl:apply-templates select="body.head/abstract"/>

                  </td>
                <td width="4" bgcolor="white">&#160;</td>
              </tr>

              <tr>
                <td bgcolor="white" colspan="3">&#160;</td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template match="Articles" mode="top">
  <tr>
    <xsl:for-each select="Article">
      <xsl:if test="position()&#60;=2">
        <td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
                      <xsl:choose>
                       <xsl:when test="body.head/media/media-reference">
        <td align="right" width="150" bgcolor="#CCCC99" valign="bottom"><a href="{@href}/">
<img src="{$unipublic}/{@href}/{body.head/media/media-reference/@source}" width="80" height="60" border="0" alt="{body.head/media/media-reference/@alternate-text}" align="right"/>
        </a></td>
                       </xsl:when>
                       <xsl:otherwise>
        <td>
                    <font color="red"> Attention:</font><br/><font color="red">no image </font>
        </td>
                       </xsl:otherwise>
                     </xsl:choose>                                                                                                          
        <td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      </xsl:if>
    </xsl:for-each>
  </tr>

  <tr>
    <td width="5" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
    <td width="150" valign="top" bgcolor="white" class="tsr-text">

     <p><a href="{Article[1]/@href}/"><span class="tsr-title"><xsl:apply-templates select="Article[1]/body.head/hedline/hl1"/></span></a><br />
      <xsl:apply-templates select="Article[1]/body.head/abstract"/>(<xsl:apply-templates select="Article[1]/body.head/dateline/story.date/@norm"/>)</p>

    </td>
    <td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
    <td width="1" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
    <td width="150" valign="top" bgcolor="white" class="tsr-text">

     <p><a href="{Article[2]/@href}/"><span class="tsr-title"><xsl:apply-templates select="Article[2]/body.head/hedline/hl1"/> </span></a><br />
      <xsl:apply-templates select="Article[2]/body.head/abstract"/>(<xsl:apply-templates select="Article[2]/body.head/dateline/story.date/@norm"/>)</p>

    </td>
    <td width="5" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
  </tr>
</xsl:template>

</xsl:stylesheet>
