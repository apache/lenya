<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="../../../../../../stylesheets/cms/Page/root-dhtml.xsl"/>

<!-- Build the base page -->
<xsl:include href="skeleton.xsl"/>

<xsl:template match="span[@id = 'preview']">
    <!-- Insert article content here... -->
    <xsl:apply-templates select="/wyona/article"/>
</xsl:template>

<xsl:param name="documentid"/>
  <xsl:template match="article">
    <table cellpadding="1" border="0" width="100%" bgcolor="#cccccc"><tr><td>
    <table cellpadding="3" border="0" width="100%" bgcolor="white">
      <tr>
        <td class="txt-s-black"><b>Teaser-Image</b></td>
	<td class="txt-s-black">
         <a href="../{$documentid}?usecase=uploadimage&amp;step=showteaserscreen&amp;documentid={$documentid}&amp;xpath=/article/head/*[1]">
          <xsl:choose>
            <xsl:when test="head/media">
              <img src="/img/news/{head/media/media-reference/@source}" border="0" alt="Teaser Image" align="middle" /> Change Image
            </xsl:when>
            <xsl:otherwise>
              <img src="/images/wyona/cms/util/reddot.gif" alt="Upload Image" border="0"/> Upload Image
            </xsl:otherwise>
          </xsl:choose>
         </a>
        </td>
      </tr>
    </table>
    </td></tr></table>
    <br/>
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
				<td width="440" align="left" valign="top" colspan="2">
					<br/>
	<a href="index.html?usecase=uploadimage&amp;step=showscreen&amp;documentid={$documentid}&amp;xpath=/article/body/*[1]"><img src="/images/wyona/cms/util/reddot.gif" alt="Insert Image" border="0"/></a>
<xsl:apply-templates select="body/media[1]"/>
	<span class="txt-m-black-bold"><xsl:value-of select="head/abstract" /></span><br />
        	<span class="txt-m-black"><xsl:apply-templates select="body/p"/>
          &#160;<a href="../impressum/" class="txt-m-red">(<xsl:value-of select="head/byline" />)</a></span></td>
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
  

<xsl:template match="body/media">
          <div align="left">
          <table border="0" cellpadding="0" cellspacing="0" align="left">
						<tr>
							<td valign="top" align="left"><img 
border="0" src="/img/news/{media-reference/@source}" align="left" /></td>
							<td width="3" valign="top" align="left" rowspan="4"><img src="/img/layout/trans1x1.gif" width="3" height="1"/></td>
						</tr>
						<tr><td height="3" valign="top" align="left" 
style="background-image:url(/img/layout/lines/linecontent440x3.gif)"><img src="/img/layout/trans1x1.gif" width="1" height="3"/></td></tr>
						<tr bgcolor="#EFEFE7">
							<td height="25" valign="middle" align="left"><span class="txt-s-black"><xsl:value-of select="media-caption"/></span></td>
						</tr>
						<tr bgcolor="#EFEFE7"><td height="3" valign="top" align="left" style="background-image:url(/img/layout/lines/linecontent440x3.gif)"><img src="/img/layout/trans1x1.gif" width="1" height="3"/></td></tr>
					</table>
          </div>
</xsl:template>

</xsl:stylesheet>
