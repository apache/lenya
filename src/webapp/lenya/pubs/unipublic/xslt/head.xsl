<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template name="Searchbox">

<xsl:param name="is-front" />

  <!--BEGINN KOPFTEIL-->
  <form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
    <input type="hidden" value="www.unipublic.unizh.ch" name="url"/>
    <a id="topofpage" name="topofpage">&#160;</a>
    <table width="585" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td valign="middle" bgcolor="#999966" width="142">
          <img src="{$img-unipub}/spacer.gif" alt=" " width="3" height="20" border="0"/>
          <a href="http://www.unizh.ch/">
            <img src="{$img-unipub}/head/home.gif" alt="Home" border="0" height="17" width="31"/>
          </a>
        </td>
        <td colspan="2" align="right" valign="middle" bgcolor="#999966">&#160;<a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">
            <img src="{$img-unipub}/head/kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle"/>
          </a>
          <img src="{$img-unipub}/head/strich.gif" alt="|" height="17" width="7" align="middle"/>
          <img src="{$img-unipub}/head/up_suchen.gif" alt="Suchen" height="17" width="79" align="middle"/>
          <input type="text" name="keywords" size="18"/>
          <input src="{$img-unipub}/head/go.gif" type="image" border="0" name="search4" align="middle" height="17" width="28"/>
        </td>
        <td valign="middle" bgcolor="#F5F5F5" width="57">&#160;</td>
      </tr>
      <tr>
        <td bgcolor="#666699" width="142">
          <img src="{$img-unipub}/spacer.gif" alt=" " width="10" height="39" border="0"/>
        </td>
        <td valign="bottom" bgcolor="#666699" width="96">
	  <!-- No link needed on the unipublic logo if we are on the frontpage -->
	  <xsl:choose>
	    <xsl:when test="$is-front">
              <img src="{$img-unipub}/head/uplogo_oben.gif" alt="unipublic" width="96" height="21" border="0"/>
	    </xsl:when>
	    <xsl:otherwise>
              <a href="{$unipublic}{$view}/"><img src="{$img-unipub}/head/uplogo_oben.gif" alt="unipublic" width="96" height="21" border="0"/></a>
	    </xsl:otherwise>
	  </xsl:choose>
        </td>
        <td align="right" valign="top" bgcolor="#666699" width="290">
          <a href="http://www.unizh.ch/">
            <img src="{$img-unipub}/head/uni_zh.gif" alt="Universit&#228;t Z&#252;rich" width="235" height="29" border="0"/>
          </a>
        </td>
        <td bgcolor="#666699" width="57"/>
      </tr>
      <tr>
        <td width="142"/>
        <td valign="top" width="96">
	  <!-- No link needed on the unipublic logo if we are on the frontpage -->
	  <xsl:choose>
	    <xsl:when test="$is-front">
              <img src="{$img-unipub}/head/uplogo_unten.gif" alt="unipublic" width="96" height="29" border="0"/>
	    </xsl:when>
	    <xsl:otherwise>
              <a href="{$unipublic}{$view}/"><img src="{$img-unipub}/head/uplogo_unten.gif" alt="unipublic" width="96" height="29" border="0"/></a>
	    </xsl:otherwise>
	  </xsl:choose>
        </td>
        <td width="290"/>
        <td width="57"/>
      </tr>
    </table>
  </form>
  <!--ENDE KOPFTEIL-->


</xsl:template>

</xsl:stylesheet>
