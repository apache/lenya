<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.6 2003/06/16 09:34:32 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<xsl:include href="../menu/root.xsl"/>
    
<xsl:template match="lenya/cmsbody">
<html>
<head>

<!-- SECTION 1 -->
<style>
   /* styles for the tree */
   SPAN.TreeviewSpanArea A {
        font-size: 8pt; 
        font-family: verdana,helvetica; 
        text-decoration: none;
        color: black
   }
   SPAN.TreeviewSpanArea A:hover {
        color: '#820082';
   }
   /* rest of the document */
   BODY {background-color: white}
   TD {
        font-size: 10pt; 
        font-family: verdana,helvetica; 
   }
   #navigation { border: dotted 1px #CCCCCC; height: 600px; width: 200px; float: left; padding: 10px; margin: 2px; }
   #content { border: dotted 1px #CCCCCC; height: 600px; width: 700px; float: left; padding: 10px; margin: 2px;}
.contextmenu{
position:absolute;
width:150px;
border:3px ridge #C0C0C0;
background-color:#C0C0C0;
font-family:arial,helvetica,verdana;
font-size:12px;
font-style:normal;
font-weight:normal;
color:#000000;
line-height:18px;
cursor:hand;
z-index:1000;
visibility:hidden;}
.menuentry{
padding-left:10px;
text-align:left;}</style>


<!-- SECTION 2: Replace everything (HTML, JavaScript, etc.) from here until the beginning 
of SECTION 3 with the pieces of the head section that are needed for your site  -->

<!-- SECTION 3: These four scripts define the tree, do not remove-->
<script src="ua.js"/>
<script src="tree.js"/>
<script src="output.js"/>
<!--<script src="contextmenu.js"/> -->
</head>


<!-- SECTION 4: Change the body tag to fit your site -->
<body>
<div id="menucontainer" class="contextmenu" onMouseover="activate(event)" onMouseout="deactivate(event)" onClick="follow(event)" style="cursor:hand; display:none">
<div class="menuentry" url="javascript:window.history.go(-1);" target="_self" description="">Cut</div>
<div class="menuentry" url="javascript:window.history.go(1);" target="_self" description="">Copy</div>
<div class="menuentry" url="javascript:window.parent.external.AddFavorite(window.top.location.href,window.top.document.title);" target="_self" description="">Paste</div>
<hr/>
<div class="menuentry" url="javascript:window.parent.location.href=window.parent.location.href;" target="_self" description="">Rename</div>
<div class="menuentry" url="javascript:window.parent.location.href=window.parent.location.href;" target="_self" description="">Move Up</div>
<div class="menuentry" url="javascript:window.parent.location.href=window.parent.location.href;" target="_self" description="">Move Down</div>
<div class="menuentry" url="javascript:window.parent.location.href=window.parent.location.href;" target="_self" description="">Add Child Element</div>
<div class="menuentry" url="javascript:window.parent.location.href=window.parent.location.href;" target="_self" description="">Insert New Document</div>
</div>


<!-- SECTION 5: Replace all the HTML from here until the beginning of SECTION 6 with the pieces of the head section that are needed for your site  -->
<div id="navigation">
<!-- SECTION 6: Build the tree. -->

<!-- By making any changes to this code you are violating your user agreement.
     Corporate users or any others that want to remove the link should check 
	 the online FAQ for instructions on how to obtain a version without the link -->
<!-- Removing this link will make the script stop from working -->

<span class="TreeviewSpanArea">
<script>initializeDocument()</script>
</span>

<!-- SECTION 7: Continuation of the body of the page, after the tree. Replace whole section with 
your site's HTML. -->
</div><div id="content"><iframe src="" id="basefrm" name="basefrm" frameborder="0" width="100%" height="100%"></iframe></div>
</body>
</html>
</xsl:template>

</xsl:stylesheet> 