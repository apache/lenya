<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="small-preview">
		<!-- COMPUTERWORLD MAGAZIN FORECAST BEGINS HERE -->    
    <table border="0" cellpadding="0" cellspacing="0" width="140">
         <xsl:for-each select="item">    
		<tr>
    		<td width="52" valign="top" align="center"><a href="magazine/"><img border="0" src="/img/categories/{media/media-reference/@source}" width="42" height="42" alt="{media/media-reference/@alternate-text}" /></a></td>
    		<td width="88" valign="top"><span class="txt-s-black"><xsl:value-of select="p"/><br /></span>
	        <a href="magazine/" class="txt-link-red"><img border="0" src="/img/layout/arrow-red.gif" width="9" height="7" alt="»" />weiter</a></td>
  	       </tr>
		<tr>
		<td colspan="2" height="20" valign="middle"><img border="0" src="/img/layout/line-nav140x1.gif" width="140" height="1" /></td>
		</tr>
	   </xsl:for-each>
	</table>
		<!-- COMPUTERWORLD MAGAZIN FORECAST ENDS HERE -->
</xsl:template>
</xsl:stylesheet>
