<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1">
		<title>Insert Image from List</title>
		<style type="text/css">
<!--
			.bigger { font-size: 12px; line-height:20px;font-family: Verdana, Geneva }
			.bold { font-weight:bold;font-size: 12px; font-family: Verdana, Geneva }
			td { font-size: 11px; font-family: Verdana, Geneva }
			form { font-size: 11px; font-family: Verdana, Geneva }
			input { font-size: 11px; font-family: Verdana, Geneva }
			a:link { color: black; text-decoration: underline }
			#feld { font-size: 11px; font-family: Verdana, Geneva; width: 300px }
			.tabchoosen, .tabchoosen a {color: black; background-color: #f1f1f1; font-weight: 				bold; text-decoration: none;}
			.tabwhite {background-color: white; font-weight: normal;}
	-->
    	</style>

	<script>
	</head>

	<body bgcolor="#ffffff" leftmargin="0" marginwidth="0" topmargin="0" marginheight="0">
	<br>
		
			
			
			<table border="0" cellpadding="0" cellspacing="0" width="500">
				<tr>
				    <!-- with All Images, this td has also #f1f1f1 -->
				 	 <td bgcolor="#ffffff">
					
						<table border="0" cellpadding="3" cellspacing="1" width="100%">
							<tr>
								<td align="left"><span class="bold">This Document's Images</span></td>
							</tr>
						</table>
					</td>
					<!-- later
					<td colspan="2" bgcolor="white">&nbsp;</td>
				  
					<td bgcolor="#f1f1f1">
						 
						<table border="0" cellpadding="3" cellspacing="1" width="100%">
							<tr>
								<td bgcolor="white" align="center"><a href="Image_inserter_2.html">All Images</a></td>
							</tr>
						</table>
					
					</td>
					-->
					<td>&nbsp;</td>
					<td bgcolor="white" align="right" valign="top">&nbsp;<span class="bigger"><a href="#" onclick="window.open('upload.php?ID=<?php echo $_GET["ID"];?>','h','Popup:width:500;height:290');">Upload new Image</a></span></td>
				</tr>
			</table>
			
			<table border="0" cellpadding="0" cellspacing="0" width="100%"><tr><td>
			<table border="0" cellpadding="0" cellspacing="0" width="380">
				<tr>
					<td bgcolor="#f1f1f1">
						<table border="0" cellpadding="3" cellspacing="1" width="100%">
							<tr>
								<td id="tab_Imagesingle" class="tabchoosen"  align="center"><a href="../../../../editor/insertsingleimage?ID=<?php echo $_GET["ID"];?>" onclick="document.getElementById('tab_Imagesingle').className='tabchoosen';document.getElementById('tab_Imagegroup').className='tabwhite';" target="Images">Insert Single Image</a></td>
							</tr>
						</table>
					</td>
					<td bgcolor="white">&nbsp;</td>
					
					<td bgcolor="#f1f1f1">
						<table border="0" cellpadding="3" cellspacing="1" width="100%">
							<tr>
								<td id="tab_Imagegroup" class="tabwhite" align="center"><a href="../../../../editor/insertgroupimage?ID=<?php echo $_GET["ID"];?>" onclick="document.getElementById('tab_Imagegroup').className='tabchoosen';document.getElementById('tab_Imagesingle').className='tabwhite';" target="Images">Insert Image Group</a></td>
							</tr>
						</table>
						
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
			<iframe width="100%" src="../../../../editor/insertsingleimage?ID=<?php echo $_GET["ID"];?>" frameborder="no" border="0" framespacing="0" height="450" name="Images"></iframe>
		</td></tr></table>	
			
			
		
	</body>

</html>
