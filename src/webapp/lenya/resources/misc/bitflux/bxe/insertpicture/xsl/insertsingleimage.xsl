<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output encoding="iso-8859-1" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
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
<script>
<![CDATA[
function BX_insert()
{
var checkboxes = document.forms.images.bild;
if (checkboxes.checked)
{
	var mediaobject = checkboxes.value;
}

for (var i=0; i < checkboxes.length; i++)
{

	if (checkboxes[i].checked )
    {
    	var mediaobject = checkboxes[i].value;
        i = checkboxes.length;
    }
}    

var pos = document.forms.images.pos;
for (var i=0; i < pos.length; i++)
{

	if (pos[i].checked )
    {
    	var position = pos[i].value;
        i = pos.length;
    }
}    
parent.opener.BX_insertContent("<xref internalid='yes' id='BX_image"+Math.random()+"' align='"+position+"' linkend='"+mediaobject+"'><img temporaryelement='yes' src='/admin/editor/"+mediaobject+"' width='"+object[mediaobject]["width"]+"'  height='"+object[mediaobject]["height"] + "'/><caption style='width: "+object[mediaobject]["width"]+"px' temporaryelement='yes'>"+object[mediaobject]["caption"]+"</caption></xref>");
// caption='"+object[mediaobject]["caption"]+"' 
parent.opener.BX_transform();
parent.opener.BX_doTansformOnFocus = 1;
return false;
}

]]>
object = new Array();

				<xsl:for-each select="objects//Mediaobject[number(Imageobject/imagedata/height) &gt; 0] ">
object["Mediaobject<xsl:value-of select="ID"/>"] = new Array();
object["Mediaobject<xsl:value-of select="ID"/>"]["caption"] = '<xsl:value-of select="translate(caption/main,'&#13;&#10;','  ')"/>';
object["Mediaobject<xsl:value-of select="ID"/>"]["height"] = <xsl:value-of select="Imageobject/imagedata/height"/>;
object["Mediaobject<xsl:value-of select="ID"/>"]["width"] = <xsl:value-of select="Imageobject/imagedata/width"/>;
</xsl:for-each>

</script>

        
	</head>

	<body bgcolor="#f1f1f1" leftmargin="3" marginwidth="3" topmargin="3" marginheight="3">
	
		<form name="images" action="#" method="get" onsubmit="BX_insert();">
		
		
		<div class="radio">
			<table border="0" cellpadding="7" cellspacing="0">
				<tr>
					<td colspan="9" class="bold">Position</td>
				</tr>	
				<tr>
					<td width="80" class="bigger">Top aligned:</td>	
					<td><input type="radio" value="upleft" name="pos" checked="checked" /></td>
					<td><img src="../wysiwyg/img/upleft.png" width="27" height="22" border="0"/></td>
					<td>&#xA0;</td>
					<td><input type="radio" value="upright" name="pos"/></td>
					<td><img src="../wysiwyg/img/upright.png" width="27" height="22" border="0"/></td>
<!-->					<td>&#xA0;</td>
					<td><input type="radio" value="upcenter" name="pos"/></td>
					<td><img src="/admin/editor/img/upcenter.png" width="27" height="22" border="0"/></td>-->
				</tr>
				<tr>
					<td width="80" class="bigger">Enclosed:</td>	
					<td><input type="radio" value="left" name="pos"/></td>
					<td><img src="../wysiwyg/img/left.png" width="27" height="22" border="0"/></td>
					<td>&#xA0;</td>
					<td><input type="radio" value="right" name="pos"/></td>
					<td><img src="../wysiwyg/img/right.png" width="27" height="22" border="0"/></td>
<!--					<td>&#xA0;</td>
					<td><input type="radio" value="center" name="pos"/></td>
					<td><img src="/admin/editor/img/center.png" width="27" height="22" border="0"/></td>
-->				</tr>
			
				<tr valign="bottom">
					<td width="80"><input type="button" onclick="BX_insert()" name="submitButtonName" value="Insert"/>
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
		    
			<table width="450" border="0" cellpadding="3" cellspacing="0">
				
				<xsl:for-each select="objects//Mediaobject[number(Imageobject/imagedata/height) &gt; 0]">
			
				<!-- 1 Element -->
				<tr valign="top" >
					<td ><input type="radio" value="Mediaobject{./ID}" name="bild"/></td>
					<td class="pic">
					

					<a href="#" onclick="window.open('/admin/editor/originalsize?ID={./ID}','originalsize','toolbar=no,width=550,height=550,scrollbars=no,resizable=yes')">
                    <img src="/files/images/{Imageobject/ID}.{Imageobject/imagedata/fileref}" height="80" width="$newwidth" border="1"  alt="{textobject/phrase}" title="{textobject/phrase}" /></a>
</td>


					<td  class="pic"><b><xsl:value-of select="objectinfo/title"/></b><br/>
					    <xsl:value-of select="Imageobject/imagedata/fileref"/><br/>
						<xsl:value-of select="Imageobject/imagedata/width"/>&#xD7;<xsl:value-of select="Imageobject/imagedata/height"/>
<br/>
						<xsl:value-of select="round((Imageobject/imagedata/filesize div 1024)*100) div 100"/> <xsl:text> KB</xsl:text><br/>
						<xsl:value-of select="./changed"/>
						<br/>
						<br/><xsl:text>Caption: </xsl:text>
						<xsl:apply-templates select="./caption/main"/><br/>
						</td>
				</tr>
						
				<tr><td colspan="3">&#xA0;</td>
				<!-- 1 Element -->
				</tr>
				</xsl:for-each>
				
			
			</table>
			
			
			
		







	<table>
		
	</table>
</xsl:template>
<xsl:template match="br">
<xsl:copy>
<xsl:value-of select="."/>
</xsl:copy>
</xsl:template>
</xsl:stylesheet>
