<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="iso-8859-1" indent="yes"
		doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>


<!-- if you would only use this page then you can drop the call-template, named template stuff-->		
<xsl:template match="specialcharacters">
<html>
<head>
<title>Sonderzeichen</title>
<style type="text/css">
<xsl:comment>
body 	{background-color:#ffffff;
		 }
table	{
		border-collapse: collapse;
      	margin-left:15px;
		padding:0px;
		border-right: medium none;border-top: medium none;border-left: medium none;border-bottom: 	medium none;
		border:none;
		padding-right: 0px;  padding-left: 0px; padding-bottom: 0px;  padding-top: 0px;
		}
#Sonderzeichen {
		background-color: #f9f9f9; 
		padding-right: 0px;  padding-left: 0px; padding-bottom: 0px;  padding-top: 0px;
		margin: 0px; width: 360px; height: 390px; text-align: left;
		}
select  {
		background-color: #ffffff;
		font-size: 12px;font-family: arial,verdana,helvetica,sans-serif;color:black;
		margin-top:15px;
		margin-left:15px;
		}
.button {
		background: white; 
		width: 25px;height: 25px;
		font-size: 12px;font-family:georgia,'times new roman',times,serif;color: black;
		text-align: center; 
		border-right: #cccccc 1px solid;border-top: #cccccc 1px solid;border-left: #cccccc 1px solid;border-bottom: #cccccc 1px solid;
		padding-right: 4px;padding-left: 4px;padding-bottom: 4px;padding-top: 4px; 
		margin:0px;
		}
.raised {
		background: #000066;
		width: 25px;height: 25px;
		font-size: 12px;font-family: arial,verdana,helvetica,sans-serif;color: white;
		text-align: center; 
		padding-right: 4px; padding-left: 4px;padding-bottom: 4px; padding-top: 4px;  
		}
.pressed {
		background: #000066; 
		width: 25px; height: 25px;
		font-size: 12px;font-family: arial,verdana,helvetica,sans-serif;color: white; 
		text-align: center;
		border-right: #0000FF 1px solid;border-top:#0000FF 1px solid;border-left:#0000FF 1px solid;border-bottom: #0000FF 1px solid;   
		padding-left: 4px;padding-bottom: 4px;padding-right: 4px;padding-top: 4px;
		}
p 		{
		margin-left:10px;border: 1px dashed #cccccc; width: 340px; line-height:18px;
		text-align:center;
		font-size: 11px;font-family:georgia,'times new roman',times,serif;}
</xsl:comment>
</style>	

<!--

Javascript comes here, Create Range functions only for IE 6 

 function insertSz(mySz) {
  	var str = document.selection.createRange().text;
  	document.my_form.my_textarea.focus();
    
	if (mySz != null) {
    var sel = document.selection.createRange();
	sel.text = mySz;
  	}
  	return;

}
-->

<script type="text/javascript">
    <xsl:comment>
        <![CDATA[
		
		//works only in IE 6 because of Create Range
function insertSz(mySz) {
opener.BX_insertContent(mySz);
return;
/*
  	var str = document.selection.createRange().text;
  	document.my_form.my_textarea.focus();
    	if (mySz != null) {
    	var sel = document.selection.createRange();
		sel.text = mySz;
  		}
  	return;*/
}


function mouseover(el) {
  el.className = "raised";
}

function mouseout(el) {
  el.className = "button";
}

function mousedown(el) {
  el.className = "pressed";
}

function mouseup(el) {
  el.className = "raised";
}


function displaydesc(which){
	document.getElementById(visiblediv).style.visibility = 'hidden' ;
	document.getElementById(which).style.visibility = 'visible' ;
	visiblediv = which;
}

/* vielleicht spaeter einmal - behaviour ist nicht ganz, das was man moechte
function changefont(fontname){
 	var fontname;
	stsh = document.styleSheets[0]; 
	cssB= stsh.cssRules[5];
	cssR= stsh.cssRules[6];
	cssP= stsh.cssRules[7];
	cssB.style.fontFamily=fontname;
	cssR.style.fontFamily=fontname;
	cssP.style.fontFamily=fontname;
	alert(cssB.style.fontFamily);
	}
*/


var visiblediv =   ']]><xsl:value-of select="generate-id(/specialcharacters/*[position() = 1])" />';
   </xsl:comment>
</script>

</head>
<body >
<div id="Sonderzeichen">
<form name="subsets" id="subsets">
<!-- evtl. spaeter auch mit font-wechsel
<table border="0" cellpadding="0" cellspacing="0"><tr><td>-->
<select id="blocks" name="blocks" size="1" 
onChange="displaydesc(this.value)">
<xsl:for-each select="subset | unicodeblock">
	<option value="{generate-id()}"><xsl:value-of select="./@name"/></option>
</xsl:for-each>
</select>
<!--evtl. spaeter
</td><td>&#160;
<select id="fonts" name="fonts" size="1" onChange="changefont(this.value)">
    <option value="arial,verdana,helvetica,sans-serif">Arial, Verdana, Helvetica </option>
	<option value="'times new roman'">Times New Roman</option>
	<option value="arial">Arial</option>
	<option value="verdana">Verdana</option>
	<option value="georgia">Georgia</option>
	<option value="serif">Serif</option>
	<option value="sans-serif">Sans-Serif</option>
</select>
</td></tr></table>-->
</form>

</div>
<xsl:apply-templates select="subset | unicodeblock" mode="javascript"/>
<p>&#160;Characters are shown as serif and sans-serif (mouseover).</p>
</body>
</html>
</xsl:template>
	

<xsl:template match="subset | unicodeblock" mode="javascript">
<xsl:variable name="visibility">
	<xsl:choose>
		<xsl:when test="position() = 1">visible</xsl:when>
		<xsl:otherwise>hidden</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
<div id="{generate-id()}"  style="top:60px; position:absolute;visibility:{$visibility};">

<table name="blocks" cellspacing="0" cellpadding="0">

<xsl:for-each select="character[(position() mod 10 )= 1]">
<tr>
<xsl:variable name="durchlauf" select="floor (position()-1 div 10)"/>
<!--<xsl:for-each select="../character[((position()-1)  div 10) > $durchlauf and ((position()-1) div 10) &lt; $durchlauf + 1] ">-->
<xsl:for-each select="../character[floor((position() -1 ) div 10) = $durchlauf]">
<td class='button' 
 onmouseover='mouseover(this);' 
 onmouseout="mouseout(this);" 
 onmousedown="mousedown(this);" 
 onmouseup="mouseup(this);" 
 onclick="insertSz('{.}');" 
 title="{@meaning}"> <xsl:value-of select="."/></td>
 <xsl:text>
 </xsl:text>
</xsl:for-each>
</tr>
</xsl:for-each>
</table>

</div>





</xsl:template>
 
<!-- only for demo purposes - this textarea will be integrated in the bitflux Editor
<textarea name="my_textarea">lfdsjfdsaf</textarea></form>-->

</xsl:stylesheet>
