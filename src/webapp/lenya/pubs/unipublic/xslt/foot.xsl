<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template name="footer"> 
  <xsl:param name="footer_date" />
      <div align="left">
		<!--BEGINN FUSSTEIL-->
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><br/>
						<a href="#topofpage"><img src="{$img-unipub}/top.gif" alt="top" width="40" height="13" border="0" /></a></td>
				</tr>
				<tr>
					<td bgcolor="#666666" height="1"><img src="{$img-unipub}/spacer.gif" alt="top" width="40" height="1" border="0" /></td>
				</tr>
				<tr>
					<td><font size="1">&#169; Universit&#228;t Z&#252;rich,&#160;<!--
				           --><xsl:if test="$footer_date and not($footer_date = '')"><xsl:value-of select="$footer_date" />,&#160;</xsl:if><!--
					   --><a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">Impressum</a></font>
					</td>
				</tr>
			</table>
		<!--ENDE FUSSTEIL-->
      </div>
</xsl:template>

</xsl:stylesheet>
