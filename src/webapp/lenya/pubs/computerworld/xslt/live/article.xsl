<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Build the base page -->
<xsl:include href="skeleton.xsl"/>

<xsl:template match="span[@id = 'preview']">
	<span id="preview" bxe-editable="article">
    <!-- Insert article content here... -->
    <xsl:apply-templates select="/wyona/article"/>
    </span>
</xsl:template>

  <xsl:template match="article">
  		<table border="0" cellpadding="0" cellspacing="0" width="440">
			<tr><td width="440" height="3" colspan="2"><img src="/img/layout/linecontent440x3.gif" width="440" height="3"/></td></tr>
			<tr>
				<td width="300" height="30" align="left" valign="middle">
        	<span class="txt-s-black"><xsl:value-of select="head/dateline/story.date/@norm" /></span></td>
				<td width="140" height="30" align="left" valign="middle">
				</td>
			</tr>

			<tr><td width="440" height="3" colspan="2"><img src="/img/layout/linecontent440x3.gif" width="440" height="3"/></td></tr>

			<tr>
				<td width="440" height="30" align="left" valign="middle" colspan="2">
        	<span class="txt-l-black"><b><xsl:value-of select="head/title" /></b></span></td>
			</tr>

			<tr>
				<td width="440" align="left" valign="top" colspan="2"><span class="txt-m-black-bold"><xsl:value-of select="head/abstract" /></span><br />
        	<span class="txt-m-black"> <xsl:apply-templates select="body"/>
          &#160;<a href="../impressum/" class="txt-m-red">(gis)</a></span></td>
			</tr>

			<tr><td width="440" height="10" valign="bottom" colspan="2"><img src="/img/layout/linecontent440x3.gif" width="440" height="3"/></td></tr>

			<tr>
				<td width="300" height="20" valign="middle">
        	<a href="#"><img border="0" src="/img/layout/arrow-red-top.gif" width="16" height="16" alt="^"/></a></td>
				<td width="140" height="20" align="left" valign="middle">
				</td>
			</tr>

			<tr><td width="440" height="3" colspan="2"><img src="/img/layout/linecontent440x3.gif" width="440" height="3"/></td></tr>
		</table>
  </xsl:template>
</xsl:stylesheet>