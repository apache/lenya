<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" indent="no" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
<!-- sprachendefinition -->
<xsl:template match="/iba">
<html>

	<head>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1"/>
		<title>Insert Single Image</title>
		<style type="text/css">
		<xsl:comment>
		.bigger { font-size: 11px; line-height:20px;font-family: Verdana, Geneva; }
		.bold { font-weight:bold;font-size: 11px; font-family: Verdana, Geneva; }
		td  { font-size: 10px; font-family: Verdana, Geneva; }
		form { font-size: 11px; font-family: Verdana, Geneva; }
		input { font-size: 11px; font-family: Verdana, Geneva; }
		a:link, a:visited { color: black; text-decoration: underline; }
		.grau {background-color:#f1f1f1; position:absolute;width:500px;height:300px;}
		td.pic {background-color:#ffffff;padding:12px;}
		div.radio {position:absolute; top:0;left:0; position:fixed; background-color:#f1f1f1; width:100%;}
		</xsl:comment>
		</style>
	</head>

	<body bgcolor="#f1f1f1" leftmargin="3" marginwidth="3" topmargin="3" marginheight="3">
	
		
		<form name="FormName" action="#" method="get">
		
		
		<div class="radio">
			<table border="0" cellpadding="7" cellspacing="0">
				<tr>
					<td colspan="9" class="bold">Position</td>
				</tr>	
				<tr>
					<td width="80" class="bigger">Top aligned:</td>	
					<td><input type="radio" value="radioValue" name="pos" checked="checked" /></td>
					<td><img src="../../img/upleft.png" width="27" height="22" border="0"/></td>
					<td>&#xA0;</td>
					<td><input type="radio" value="radioValue" name="pos"/></td>
					<td><img src="../../img/upright.png" width="27" height="22" border="0"/></td>
					<td>&#xA0;</td>
					<td><input type="radio" value="radioValue" name="pos"/></td>
					<td><img src="../../img/upcenter.png" width="27" height="22" border="0"/></td>
				</tr>
				<tr>
					<td width="80" class="bigger">Enclosed:</td>	
					<td><input type="radio" value="radioValue" name="pos"/></td>
					<td><img src="../../img/left.png" width="27" height="22" border="0"/></td>
					<td>&#xA0;</td>
					<td><input type="radio" value="radioValue" name="pos"/></td>
					<td><img src="../../img/right.png" width="27" height="22" border="0"/></td>
					<td>&#xA0;</td>
					<td><input type="radio" value="radioValue" name="pos"/></td>
					<td><img src="../../img/center.png" width="27" height="22" border="0"/></td>
				</tr>
			
				<tr valign="bottom">
					<td width="80"><input type="submit" name="submitButtonName" value="Insert"/>
							</td>
					<td colspan="8"><input type="reset" value="Cancel"/></td>
				</tr>
				
			</table>
			
			</div>
		    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
			<xsl:call-template name="bilder"/>
			
			</form>
	<br/><br/>
	
	</body>

</html>
</xsl:template>
			
<xsl:template name="bilder">			
		    
			<table border="0" cellpadding="3" cellspacing="0">
				
				<xsl:for-each select="objects//Mediaobject">
			
				<!-- 1 Element -->
				<tr valign="top" >
					<td ><input type="radio" value="radioValue" name="bild"/></td>
					<td class="pic">
					
					
					<a href="/editor/originalsize?ID={./ID}"><img src="/files/images/{Imageobject/ID}.{Imageobject/imagedata/fileref}" height="80" width="(80 * {Imageobject/imagedata/width}) div {Imageobject/imagedata/height}" border="1"  alt="{textobject/phrase}"/></a>
</td>

<!--
					<td class="pic"><b>{Mediaobject_objectinfo_title}</b><br/>
					    {Imageobject_imagedata_fileref}<br/>
						{Imageobject_imagedata_width}&#xD7;{..._height}
<br/>
						{Imageobject_imagedata_filesize:1024} KB<br/>
						{Mediaobject_changed}</td>
				</tr>
				<tr><td>&#xA0;</td><td colspan="2" class="pic">{Mediaobject_caption_main}<br/></td></tr>
				
				<tr><td colspan="3">&#xA0;</td>-->
				<!-- 1 Element -->
				</tr>
				</xsl:for-each>
				
			
			</table>
			
			
			
		







	<table>
		
	</table>
</xsl:template>

</xsl:stylesheet>
