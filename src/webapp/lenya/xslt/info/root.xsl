<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.18 2003/08/21 18:07:15 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<xsl:include href="../menu/root.xsl"/>

<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="area"/>
<xsl:param name="tab"/>
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
<script>// Load a page as if a node on the tree was clicked (synchronize frames)
// (Highlights selection if highlight is available.)
function loadSynchPage(srclink) 
{
	var docObj;
	var linkID;
	linkID = findIDbyLink(srclink);
	docObj = findObj(linkID);
	docObj.forceOpeningOfAncestorFolders();
	clickOnLink(linkID,docObj.srclink,'basefrm');
	
    //Scroll the tree window to show the selected node
    //Other code in these functions needs to be changed to work with
    //frameless pages, but this code should, I think, simply be removed
   document.body.scrollTop=docObj.navObj.offsetTop
}

function findIDbyLink(srclink)
{
<![CDATA[
  var i=0;
  for(i=0;i<nEntries&&indexOfEntries[i].link!=srclink;i++);
  //FIXME: extend to allow for mapping of index.html to index_defaultlanguage.html
  if (i >= nEntries) {
     return 1; //example: node removed in DB
  }
  else {
    return i;
  }
]]>
}
</script>
	
</head>

<body>
<a id="de">
	<xsl:call-template name="activate">
		<xsl:with-param name="tablanguage">de</xsl:with-param>
	</xsl:call-template>
</a>
<a id="en"> 
	<xsl:call-template name="activate">
		<xsl:with-param name="tablanguage">en</xsl:with-param>
	</xsl:call-template>
</a>

<div id="lenya-info-treecanvas">
<!-- Build the tree. -->

   <div id="lenya-info-tree">
      <div style="display:none;"><table border="0"><tr><td><a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/" target="_blank">JavaScript Tree Menu</a></td></tr></table></div>
    </div>
  <script>initializeDocument();
 loadSynchPage('<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="$documentid"/><xsl:call-template name="selecttab"/>');
   </script>
</div>
<div id="lenya-info-content"><iframe src="" id="basefrm" name="basefrm" frameborder="0" width="100%" height="100%"></iframe>
  <script>
   	frames['basefrm'].location.href = '<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="$documentid"/><xsl:call-template name="selecttab"/>';
   </script>
</div>
</body>
</html>
</xsl:template>

<xsl:template name="activate">
	<xsl:param name="tablanguage"/>
	<xsl:variable name="docidwithoutlanguage"><xsl:value-of select="substring-before($documentid, '_')"/></xsl:variable>
   <xsl:attribute name="href"><xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="$docidwithoutlanguage"/>_<xsl:value-of select="$tablanguage"/>.html?lenya.language=<xsl:value-of select="$tablanguage"/></xsl:attribute>
   <xsl:attribute name="class">lenya-tablink<xsl:choose><xsl:when test="$chosenlanguage = $tablanguage">-active</xsl:when><xsl:otherwise/></xsl:choose></xsl:attribute><xsl:value-of select="$tablanguage"/>
</xsl:template>

<xsl:template name="selecttab">
  <xsl:text>?lenya.usecase=info-</xsl:text>
  <xsl:choose>
  	<xsl:when test="$tab">
  		<xsl:text><xsl:value-of select="$tab"/></xsl:text>
    </xsl:when>
  	<xsl:otherwise>
        <xsl:text>overview</xsl:text>
  	</xsl:otherwise>
  </xsl:choose>
  <xsl:text>&amp;lenya.step=showscreen&amp;lenya.area=authoring</xsl:text>
</xsl:template>


</xsl:stylesheet> 