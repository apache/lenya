<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="dossiers">
  <table width="399" border="0" cellspacing="0" cellpadding="0">
  <xsl:for-each select="dossier">
    <!-- Layout has two columns that are built by drawing <tr> or not  -->
    <xsl:if test="position() mod 2">
      <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>  <!-- Just writing <tr> results in not well-formed xml -->
    </xsl:if>
      <td valign="top" width="199">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	  <tr>
	    <td valign="top" bgcolor="{head/color}">
	      <a href="{@href}/index.html"> 
	        <img src="{@href}/{head/image/@href}" alt="{head/title}" width="80" height="60" border="0"/>
	      </a>
	    </td>
          </tr>
	  <tr>
            <td class="tsr-text" valign="top" width="199">
              <span class="tsr-title">
                <a href="{@href}/index.html"><xsl:value-of select="head/title" /></a>
              </span>
              <br/>
              <xsl:value-of select="head/teasertext" />
            </td>
	  </tr>
	</table>
      </td>
      <xsl:if test="position() mod 2">
      <td width="1" bgcolor="white">
        <img src="{$img-unipub}/spacer.gif" alt="" height="10" width="1" border="0"/>
      </td>
      </xsl:if>
    <xsl:if test="not(position() mod 2)"><xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text></xsl:if>
  </xsl:for-each>
  </table>
</xsl:template>

</xsl:stylesheet>
