<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org" />
<title>Midas - Apache Lenya</title>
<link href="midas_files/midas.css" type="text/css"
rel="stylesheet" />
<script type="text/javascript" src="midas_files/midas.js">
</script>
</head>
<body onload="Start()">
<h2>Editing with Midas</h2>

<p><a href="http://mozilla.org/editor/midasdemo/index.html">Midas
Demo</a></p>

<p><b>Note:</b> The changing of text format (Heading, Paragraph,
etc.) will only function properly on a 1.3b build dated after
January 26, 2003. Thanks.</p>

<p>The Cut, Copy, and Paste buttons below are disabled for security
reasons. To enable them for purposes of this demo, you need to <a
href="http://mozilla.org/editor/midasdemo/securityprefs.html">edit
your preferences file</a>.</p>



<form enctype="multipart/form-data" method="post" action="sugus">
<!--
<form enctype="multipart/form-data" method="post" action="index.html?lenya.usecase=midas&amp;lenya.step=save">
-->

<input type="hidden" name="lenya.usecase" value="midas" />
<input type="hidden" name="lenya.step" value="save" />

<p>
<input type="file" name="file" />
</p>



<p><input name="save" value="Save" type="submit" /> <input
name="savexit" value="Save and Exit" type="submit" /> <!--
  <input onclick="doAction('save')" value="Save" type="button">
  <input onclick="doAction('savexit')" value="Save and Exit" type="button">
-->
</p>


<p xmlns:xhtml="http://www.w3.org/1999/xhtml">
<h3>Title:</h3>
<input name="title" type="text" value="{/xhtml:html/xhtml:head/xhtml:title}" size="50"/>
</p>


<h3>Body:</h3>

<!-- TOOLBAR 1 -->
<table bgcolor="#c0c0c0" id="toolbar1">
<tbody>
<tr>
<td>
<div class="imagebutton" id="cut"><img class="image"
src="midas_files/cut.gif" alt="Cut" title="Cut" /></div>
</td>
<td>
<div class="imagebutton" id="copy"><img class="image"
src="midas_files/copy.gif" alt="Copy" title="Copy" /></div>
</td>
<td>
<div class="imagebutton" id="paste"><img class="image"
src="midas_files/paste.gif" alt="Paste" title="Paste" /></div>
</td>
<td></td>
<td></td>
<td>
<div class="imagebutton" id="undo"><img class="image"
src="midas_files/undo.gif" alt="Undo" title="Undo" /></div>
</td>
<td>
<div class="imagebutton" id="redo"><img class="image"
src="midas_files/redo.gif" alt="Redo" title="Redo" /></div>
</td>
<td></td>
<td>
<div style="left: 10px;" class="imagebutton" id="createlink"><img
class="image" src="midas_files/link.gif" alt="Insert Link"
title="Insert Link" /></div>
</td>
<td></td>
<td>
<div style="left: 10px;" class="imagebutton" id="createtable"><img
class="image" src="midas_files/table.gif" alt="Insert Table"
title="Insert Table" /></div>
</td>
</tr>
</tbody>
</table>

<br />
 <!-- TOOLBAR 2 -->
 

<table bgcolor="#c0c0c0" id="toolbar2">
<tbody>
<tr>
<td><select id="formatblock" onchange="Select(this.id);">
<option value="&lt;p&gt;">Normal</option>
<option value="&lt;p&gt;">Paragraph</option>
<option value="&lt;h1&gt;">Heading 1</option>
<option value="&lt;h2&gt;">Heading 2</option>
<option value="&lt;h3&gt;">Heading 3</option>
<option value="&lt;h4&gt;">Heading 4</option>
<option value="&lt;h5&gt;">Heading 5</option>
<option value="&lt;h6&gt;">Heading 6</option>
<option value="&lt;address&gt;">Address</option>
<option value="&lt;pre&gt;">Formatted</option>
</select> </td>
<td><select id="fontname" onchange="Select(this.id);">
<option value="Font">Font</option>
<option value="Arial">Arial</option>
<option value="Courier">Courier</option>
<option value="Times New Roman">Times New Roman</option>
</select> </td>
<td><select unselectable="on" id="fontsize"
onchange="Select(this.id);">
<option value="Size">Size</option>
<option value="1">1</option>
<option value="2">2</option>
<option value="3">3</option>
<option value="4">4</option>
<option value="5">5</option>
<option value="6">6</option>
<option value="7">7</option>
</select> </td>
<td>
<div class="imagebutton" id="bold"><img class="image"
src="midas_files/bold.gif" alt="Bold" title="Bold" /></div>
</td>
<td>
<div class="imagebutton" id="italic"><img class="image"
src="midas_files/italic.gif" alt="Italic" title="Italic" /></div>
</td>
<td>
<div class="imagebutton" id="underline"
style="border: 2px solid rgb(192, 192, 192);"><img class="image"
src="midas_files/underline.gif" alt="Underline"
title="Underline" /></div>
</td>
<td></td>
<td>
<div style="left: 10px;" class="imagebutton" id="forecolor"><img
class="image" src="midas_files/forecolor.gif" alt="Text Color"
title="Text Color" /></div>
</td>
<td>
<div style="left: 40px;" class="imagebutton" id="hilitecolor"><img
class="image" src="midas_files/backcolor.gif"
alt="Background Color" title="Background Color" /></div>
</td>
<td></td>
<td>
<div style="left: 10px;" class="imagebutton" id="justifyleft"><img
class="image" src="midas_files/justifyleft.gif" alt="Align Left"
title="Align Left" /></div>
</td>
<td>
<div style="left: 40px;" class="imagebutton" id="justifycenter">
<img class="image" src="midas_files/justifycenter.gif" alt="Center"
title="Center" /></div>
</td>
<td>
<div style="left: 70px;" class="imagebutton" id="justifyright"><img
class="image" src="midas_files/justifyright.gif" alt="Align Right"
title="Align Right" /></div>
</td>
<td></td>
<td>
<div style="left: 10px;" class="imagebutton"
id="insertorderedlist"><img class="image"
src="midas_files/orderedlist.gif" alt="Ordered List"
title="Ordered List" /></div>
</td>
<td>
<div style="left: 40px;" class="imagebutton"
id="insertunorderedlist"><img class="image"
src="midas_files/unorderedlist.gif" alt="Unordered List"
title="Unordered List" /></div>
</td>
<td></td>
<td>
<div style="left: 10px;" class="imagebutton" id="outdent"><img
class="image" src="midas_files/outdent.gif" alt="Outdent"
title="Outdent" /></div>
</td>
<td>
<div style="left: 40px;" class="imagebutton" id="indent"><img
class="image" src="midas_files/indent.gif" alt="Indent"
title="Indent" /></div>
</td>
</tr>
</tbody>
</table>

<br />
 <iframe id="edit" width="640" height="200" src="midas_content/content.html"></iframe>

<iframe width="250" height="170" id="colorpalette" src="midas_files/colors.html" style="visibility: hidden; position: absolute;"></iframe> 

<script type="text/javascript">
function viewsource(source)
{
  if (source) {
    var html = document.createTextNode(document.getElementById('edit').contentWindow.document.body.innerHTML);
    document.getElementById('edit').contentWindow.document.body.innerHTML = "";
    document.getElementById('edit').contentWindow.document.body.appendChild(html);
    document.getElementById("toolbar1").style.visibility="hidden";
    document.getElementById("toolbar2").style.visibility="hidden";  
  } else {
    var html = document.getElementById('edit').contentWindow.document.body.ownerDocument.createRange();
    html.selectNodeContents(document.getElementById('edit').contentWindow.document.body);
    document.getElementById('edit').contentWindow.document.body.innerHTML = html.toString();
    document.getElementById("toolbar1").style.visibility="visible";
    document.getElementById("toolbar2").style.visibility="visible";  
  }
}

function usecss(source)
{
  document.getElementById('edit').contentWindow.document.execCommand("useCSS", false, !(source));  
}

function readonly(source)
{
    document.getElementById('edit').contentWindow.document.execCommand("readonly", false, !(source));  
}
</script>

<br />
 <input type="checkbox" onclick="viewsource(this.checked)" />View
HTML Source <input checked="checked" type="checkbox"
onclick="usecss(this.checked)" />Use CSS <input type="checkbox"
onclick="readonly(this.checked)" />Read only</form>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
