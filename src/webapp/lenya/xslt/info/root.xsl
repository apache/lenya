<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.13 2003/07/16 18:07:14 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<xsl:include href="../menu/root.xsl"/>

<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="area"/>
<xsl:param name="documentid"/>
<xsl:param name="chosenlanguage"/>
<xsl:param name="defaultlanguage"/>
    
<xsl:template match="lenya/cmsbody">
<html>
<head>

<!-- These three scripts define the tree, do not remove-->
<script src="ua.js"/>
<script src="tree.js"/>
<script src="lenyasitetree/{$chosenlanguage}"/>

</head>

<body >
<a>
	<xsl:call-template name="activate">
		<xsl:with-param name="tablanguage">de</xsl:with-param>
	</xsl:call-template>
</a>
<a> 
	<xsl:call-template name="activate">
		<xsl:with-param name="tablanguage">en</xsl:with-param>
	</xsl:call-template>
</a>

<div id="lenya-info-treecanvas">
<!-- Build the tree. -->

   <div id="lenya-info-tree">
      <div style="display:none;"><table border="0"><tr><td><a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/" target="_blank">JavaScript Tree Menu</a></td></tr></table></div>
   <script>initializeDocument()</script>
   </div>
</div>

<div id="lenya-info-content"><iframe src="{$contextprefix}/{$publicationid}/{$area}/{$documentid}?lenya.usecase=info&amp;lenya.step=showscreen" id="basefrm" name="basefrm" frameborder="0" width="100%" height="100%"></iframe></div>
</body>
</html>
</xsl:template>

<xsl:template name="activate">
	<xsl:param name="tablanguage"/>
   <xsl:attribute name="href"><xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/?lenya.language=<xsl:value-of select="$tablanguage"/></xsl:attribute>
   <xsl:attribute name="class">lenya-tablink<xsl:choose><xsl:when test="$chosenlanguage = $tablanguage">-active</xsl:when><xsl:otherwise/></xsl:choose></xsl:attribute><xsl:value-of select="$tablanguage"/>
</xsl:template>

</xsl:stylesheet> 