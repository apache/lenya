<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.9 2003/07/08 14:41:31 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<xsl:include href="../menu/root.xsl"/>

<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="area"/>
    
<xsl:template match="lenya/cmsbody">
<html>
<head>

<!-- These three scripts define the tree, do not remove-->
<script src="ua.js"/>
<script src="tree.js"/>
<script src="output.js"/>
</head>

<body >
<a href="#" id="link1" class="lenya-tablink-active">de</a>
<a href="#" id="link2" class="lenya-tablink">en</a>

<div id="lenya-info-treecanvas">
<!-- Build the tree. -->

   <div id="lenya-info-tree">
      <div style="display:none; "><table border="0"><tr><td><a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/" target="_blank">JavaScript Tree Menu</a></td></tr></table></div>
   <script>initializeDocument()</script>
   </div>
</div>

<div id="lenya-info-content"><iframe src="" id="basefrm" name="basefrm" frameborder="0" width="100%" height="100%"></iframe></div>
</body>
</html>
</xsl:template>

</xsl:stylesheet> 