<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xslout="Can be anything, doesn't matter">

<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>


<!-- Copies everything else to the result tree -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

<!--
	<table border="0" cellpadding="2" cellspacing="0" bgcolor="#CCCCFF">
		<span bxe-editable="article">
		<snip>Here goes the xslt/xhtml code that displays this part of the page.</snip>
		</span>
	</table>
-->


<!-- Replaces the html code of the editable section by the bitflux specific code -->
<xsl:template match="*[@bxe-editable='preview']">
	<articles contentEditable="true">
                <xslout:for-each select="article">
<table border="0" cellpadding="0" cellspacing="0" width="440">
			<tr><td width="440" height="3" colspan="2"><img src="/img/layout/linecontent440x3.gif" width="440" height="3"/></td></tr>
			<tr>
				<td width="300" height="30" align="left" valign="middle"></td>
				<td width="140" height="30" align="left" valign="middle">
				</td>
			</tr>

			<tr><td width="440" height="3" colspan="2"><img src="/img/layout/linecontent440x3.gif" width="440" height="3"/></td></tr>

			<tr>
				<td width="440" height="30" align="left" valign="middle" colspan="2">
        	<span bxe-editable="title" /></td>
			</tr>

			<tr>
				<td width="440" align="left" valign="top" colspan="2">
					<br/>
	<br/><span bxe-editable="abstract" /><br />
        	<span bxe-editable="body" />
        	          &#160;<a href="../impressum/" class="txt-m-red">(gis)</a></td>
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
                </xslout:for-each>
	</articles>
</xsl:template>

<!--
<xslout:template match="article">
		
</xslout:template>
-->

<!-- Adds the stylesheet specific elements -->
<xsl:template match="/">
  <xslout:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xslout:output type="xml"/>
    <xslout:template match="/">
      <xsl:apply-templates/>
    </xslout:template>

	Template used by Bitfluxeditor to make things editable 
        <xslout:template match="*">
                <xslout:copy>
                        <xslout:for-each select="@*">
                                <xslout:copy/>
                        </xslout:for-each>
                        <xslout:apply-templates select="node()"/>
                </xslout:copy>
        </xslout:template>

  </xslout:stylesheet>
</xsl:template>  

</xsl:stylesheet>

		