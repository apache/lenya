<?xml version="1.0" encoding="UTF-8"?>

<!--
 $Id: root.xsl,v 1.2 2003/06/06 15:29:47 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    >
    
<xsl:template match="/">    
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
   #navigation { border: solid #0000FF; height: 600px; width: 200px; float: left;}
   #content { border: solid #0000FF; height: 600px; width: 700px; float: left;}
</style>


<!-- SECTION 2: Replace everything (HTML, JavaScript, etc.) from here until the beginning 
of SECTION 3 with the pieces of the head section that are needed for your site  -->

<!-- SECTION 3: These four scripts define the tree, do not remove-->
<script src="ua.js"></script>
<script src="tree.js"></script>
<script src="output.js"></script>
</head>


<!-- SECTION 4: Change the body tag to fit your site -->
<body>


<!-- SECTION 5: Replace all the HTML from here until the beginning of SECTION 6 with the pieces of the head section that are needed for your site  -->
<div id="navigation">
<!-- SECTION 6: Build the tree. -->

<!-- By making any changes to this code you are violating your user agreement.
     Corporate users or any others that want to remove the link should check 
	 the online FAQ for instructions on how to obtain a version without the link -->
<!-- Removing this link will make the script stop from working -->
<table border="0"><tr><td><a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/" target="_blank">Javascript Tree Menu</a></td></tr></table>

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