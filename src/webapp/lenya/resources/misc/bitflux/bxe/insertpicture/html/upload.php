<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>

	<head>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1">
		<title>Upload</title>
		<style type="text/css">
		<!--
			b { font-size: 12px; font-family: Verdana, Geneva }
			td { font-size: 11px; font-family: Verdana, Geneva }
			form { font-size: 11px; font-family: Verdana, Geneva }
			input { font-size: 11px; font-family: Verdana, Geneva }
			textarea { font-size: 11px; font-family: Verdana, Geneva }
			a:link { color: black; text-decoration: underline }
			.grau {background-color:#f1f1f1; position:absolute;width:500px;height:300px;}
--></style>
	</head>

	<body bgcolor="#f1f1f1" onload="focus();">
	<div class="grau"><br>
    
    <b>
<?php
if (isset($_GET["ok"]) && $_GET["ok"] == 1)
{
print 'Image was uploaded. You can upload the next one or <br/>go back to "Insert Image from List" and add it to the Article<br/>';
}
?>
</b>
	<br>

    <form method="post" action="../php/uploadimage.php" enctype="multipart/form-data"/>
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="9"><b>Upload Image from local drive</b></td>
				
			</tr>
			<tr>
				<td colspan="9">&nbsp;</td>
				
			</tr>
		    <tr>
				<td width="90">Filename:</td>
				<td colspan="8">
				<input type="file" name="Imageobject_imagedata_fileref" size="35"></td>
			</tr>
		    <tr valign="top">
					<td colspan="9">&nbsp;</td>
					
				</tr>
		
				<!-- by writing title, alt should also be filled -->
				
				<tr valign="top">
					<td width="90">Title:</td>
					<td colspan="8"><input type="text" name="Mediaobject_objectinfo_title" id="feld" size="35"></td>
				</tr>
				<tr valign="top">
					<td colspan="9">&nbsp;</td>
					
				</tr>
				
				<tr valign="top">
					<td width="90">Alt*:</td>
					<td colspan="8"><input type="text" name="Mediaobject_textobject_phrase" id="feld" size="35"></td>
				</tr>
				<tr valign="top">
					<td colspan="9">&nbsp;</td>
					
				</tr>
				<tr valign="top">
					<td width="90">Caption*:</td>
					<td colspan="8"><textarea  name="Mediaobject_caption_main"  id="feld" cols="45" rows="2"></textarea></td>
				</tr>
				<tr valign="top">
					<td colspan="9">&nbsp;</td>
					
				</tr>
				<tr valign="top">
					<td colspan="9">* Textcontent can be changed later</td>
					
				</tr>
			</table>
		
		<br>
		<input type="hidden" name="DocumentID" value="<?php echo $_GET["ID"]; ?>">
        
		<input type="submit" name="submitButtonName" value="Upload">&nbsp; <input type="reset" value="Cancel">
       </form>
<br>
<br>
</div>
	</body>

</html>
