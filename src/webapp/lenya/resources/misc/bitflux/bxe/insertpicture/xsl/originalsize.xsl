<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>

<!-- sprachendefinition -->
<xsl:template match="/iba/objects//Mediaobject">
<html>
 
	<head>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1"/>
		<title>Original Size</title>
		<style type="text/css">
		<xsl:comment>
		    b { font-size: 12px; font-family: Verdana, Geneva; }
			.bold { font-size: 11px; font-family: Verdana, Geneva; font-weight:bold; }
			td { font-size: 11px; font-family: Verdana, Geneva; }
			form { font-size: 11px; font-family: Verdana, Geneva; }
			input { font-size: 11px; font-family: Verdana, Geneva; }
			textarea { font-size: 11px; font-family: Verdana, Geneva; }
			a:link { color: black; text-decoration: underline; }
			.grau {background-color:#f1f1f1; position:absolute;width:500px;height:300px;}
		</xsl:comment>
		</style>
	</head>

	<body bgcolor="#f1f1f1" onload="window.focus()">
	<br/>

	<div class="grau">
<form method="post" action="/admin/editor/insertpicture/php/updatemediaobject.php">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="9"><b>Picture (original size)</b></td>
				
			</tr>
			<tr>
				<td colspan="9">&#xA0;</td>
				
			</tr>
			
			
			<tr valign="top">
					<td colspan="9"><span class="bold"><xsl:value-of select ="./objectinfo/title"/></span><br/>
					
					
					</td>
				</tr>
		  
			<tr>
				<td colspan="9">&#xA0;</td>
				
			</tr>	
			<tr valign="top">
					<td colspan="9">
					<table border="0" cellpadding="0" cellspacing="0" width="{Imageobject/imagedata/width}"><tr><td>
					<img src="/files/images/{Imageobject/ID}.{Imageobject/imagedata/fileref}"
					alt="{./textobject/phrase}" title="{./textobject/phrase}"/>
					</td></tr>
					<tr><td>
					<xsl:apply-templates select="./caption/main"/>
					</td></tr></table>
					
					</td>
				</tr>
			
			<tr>
				<td colspan="9">&#xA0;</td>
				
			</tr>
			  <tr valign="top">
					<td width="90">Fileref:</td>
					<td colspan="8"><xsl:value-of select="Imageobject/imagedata/fileref"/>
					
					
					</td>
				</tr>
			
			<tr valign="top">
					<td width="90">Width&#xD7;Height:</td>
					<td colspan="8">
			<xsl:value-of select="Imageobject/imagedata/width"/>&#xD7;<xsl:value-of select="Imageobject/imagedata/height"/>
			</td>
				</tr>
				
			<tr valign="top">
					<td width="90">Filesize (KB):</td>
					<td colspan="8">
					<!-- round ? -->
			<xsl:value-of select="round((Imageobject/imagedata/filesize div 1024)*100) div 100"/> <xsl:text> KB</xsl:text>
			</td>
				</tr>
			<tr valign="top">
					<td width="90">Date:</td>
					<td colspan="8">
			<xsl:value-of select="./changed"/>
			</td>
				</tr>
			
		    <tr valign="top">
					<td colspan="9">&#xA0;</td>
					
				</tr>
		
				<!-- by writing title, alt should also be filled -->
				
				
				<tr valign="top">
					<td colspan="9">&#xA0;</td>
					
				</tr>
				
				<tr valign="top">
					<td width="90">Alt:</td>
					<td colspan="8"><input type="text" name="Mediaobject_textobject_phrase" id="feld" value="{./textobject/phrase}" size="35"/></td>
				</tr>
				<tr valign="top">
					<td colspan="9">&#xA0;</td>
					
				</tr>
				<tr valign="top">
					<td width="90">Caption:</td>
					<td colspan="8"><textarea  name="Mediaobject_caption_main"  id="feld" cols="45"  rows="2"><xsl:value-of select="./caption/main"/></textarea></td>
				</tr>
				<tr valign="top">
					<td colspan="9">&#xA0;</td>
					
				</tr>
			
			</table>
		
		<br/>

		<input type="hidden" name="Mediaobject_ID" value="{ID}"/>

		<input type="submit" name="submitButtonName" value="Change"/>&#xA0; <input type="reset" value="Cancel"/>
</form>
<br/>
<br/>
</div>
	</body>

</html>
</xsl:template>
<xsl:template match="br">
<xsl:copy-of select="."/>
</xsl:template>
</xsl:stylesheet>
