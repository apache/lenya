<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.8 2003/07/03 14:10:39 gregor Exp $
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
   #navigation { border: dotted 1px #CCCCCC; width: 200px; float: left; padding: 10px; margin: 2px; }
   #content { border: dotted 1px #CCCCCC; height: 600px; width: 700px; float: left; padding: 10px; margin: 2px;}
</style>


<!-- SECTION 2: Replace everything (HTML, JavaScript, etc.) from here until the beginning 
of SECTION 3 with the pieces of the head section that are needed for your site  -->

<!-- SECTION 3: These four scripts define the tree, do not remove-->
<script src="ua.js"/>
<script src="tree.js"/>
<script src="output.js"/>
</head>


<!-- SECTION 4: Change the body tag to fit your site -->
<body>

<!-- SECTION 5: Replace all the HTML from here until the beginning of SECTION 6 with the pieces of the head section that are needed for your site  -->
<div id="navigation">
<!-- SECTION 6: Build the tree. -->

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