<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
<!-- sprachendefinition -->
<xsl:template match="/iba">
<html>

	<head>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1"/>
		<title>Insert Group Images</title>
		<style type="text/css">
		<xsl:comment>
		.bigger { font-size: 11px; line-height:20px;font-family: Verdana, Geneva; }
.bold { font-weight:bold;font-size: 11px; font-family: Verdana, Geneva; }
ul { font-size: 11px; line-height:20px;font-family: Verdana, Geneva; }
td  { font-size: 10px; font-family: Verdana, Geneva; }
form { font-size: 11px; font-family: Verdana, Geneva; }
input { font-size: 11px; font-family: Verdana, Geneva; }
option { font-family: Verdana, Geneva; }
a:link, a:visited { color: black; text-decoration: underline; }
.grau {background-color:#f1f1f1; position:absolute;width:500px;height:300px;}
td.pic {background-color:#ffffff;padding:12px;}
div.caption {position:absolute;top:0;left:0;position:fixed;background-color:#f1f1f1;width:100%;}
		</xsl:comment>
		</style>
<script>
<![CDATA[
function SortImages(a,b)
{
	alert("Bla");
	return 1;
}
function BX_insert()
{
	var toBeInserted = new Array();

	for (var name in object)
	{
		var order = eval("document.forms.images."+name+"Order.value");
        if (order > 0)
        {
        	toBeInserted[order] = name;
		}
        
	}

/*var pos = document.forms.images.pos;
for (var i=0; i < pos.length; i++)
{

	if (pos[i].checked )
    {
    	var position = pos[i].value;
        i = pos.length;
    }
}    
*/

    var xml = "<mediagroup internalid='yes' id='BX_media"+Math.random()+"'> \n"
    var done = false;

   for (var i = 0; i <= toBeInserted.length; i++)
	{

		if (toBeInserted[i])
        {
    	name = toBeInserted[i];
       	
	    	done = true;
			xml +="<xref internalid='yes' id='BX_image"+Math.random()+"' linkend='"+name+"'><img temporaryelement='yes' src='/admin/editor/"+name+"' width='"+object[name]["width"]+"'  height='"+object[name]["height"] + "'/><caption style='width: "+object[name]["width"]+"px' internalid='yes' id='BX_caption"+Math.random()+"' temporaryelement='yes'>"+object[name]["caption"]+"</caption></xref>\n";
        }
	}

xml += "<caption internalid='yes' id='BX_caption"+Math.random()+"' >"+    document.forms.images.caption.value+ "</caption>";
	xml += "</mediagroup>";
    if (done)
    {
    	
        
	parent.opener.BX_insertContent(xml);
	parent.opener.BX_transform();
	parent.opener.BX_doTansformOnFocus = 1;
}
return false;
}
]]>
object = new Array();

				<xsl:for-each select="objects//Mediaobject[number(Imageobject/imagedata/height) &gt; 0]">
object["Mediaobject<xsl:value-of select="ID"/>"] = new Array();
object["Mediaobject<xsl:value-of select="ID"/>"]["caption"] = "<xsl:value-of select="translate(caption/main,'&#13;&#10;','  ')"/>";
object["Mediaobject<xsl:value-of select="ID"/>"]["height"] = <xsl:value-of select="Imageobject/imagedata/height"/>;
object["Mediaobject<xsl:value-of select="ID"/>"]["width"] = <xsl:value-of select="Imageobject/imagedata/width"/>;
</xsl:for-each>
</script>
        
	</head>

	<body bgcolor="#f1f1f1" leftmargin="3" marginwidth="3" topmargin="3" marginheight="3">
	
		<form name="images" action="#" method="get" onsubmit="BX_insert();">		

		<div class="caption">
			<table border="0" cellpadding="7" cellspacing="0">
			   <tr>
					<td colspan="9" class="bold">Possibilities of Imagegroups</td>
				</tr>	
			    <tr valign="top">
					<td colspan="9"><ul>
					<li>4 Images with width=100 Pixel;</li>
					<li> 2 Images with width=220 Pixel;</li>
					<li> 1 Image with width=460 Pixel</li></ul></td>
				</tr>
				<tr>
					<td colspan="9" class="bold">Caption for whole Imagegroup (bottom)</td>
				</tr>	
				<tr valign="top">
					<td colspan="9"><textarea  name="caption"  id="feld" cols="45" rows="2"></textarea></td>
				</tr>
				
				
				<tr valign="bottom">

					<td width="80">
                    <input type="button" onclick="BX_insert()" name="submitButtonName" value="Insert"/>                
                    
							</td>
					<td colspan="8"><input type="reset" value="Cancel"/></td>
				</tr>
				
			</table>
			
			</div>
		    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
		
		
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
<!--					<td ><input type="checkbox"  name="Mediaobject{ID}Checked"/></td>-->
					<td ><select  name="Mediaobject{ID}Order">
							<option value="0">Order</option>
							<option value="1">First</option>
							<option value="2">Second</option>
							<option value="3">Third</option>
							<option value="4">Fourth</option>
						</select></td>
					<td class="pic">
					
					
										
					<a href="/admin/editor/originalsize?ID={./ID}" target="_"><img src="/files/images/{Imageobject/ID}.{Imageobject/imagedata/fileref}" height="80" width="$newwidth" border="1"  alt="{textobject/phrase}" title="{textobject/phrase}" /></a>
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
<xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>
