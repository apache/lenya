<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.19 2003/08/22 12:42:47 gregor Exp $
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
<style>
#lenya-info-tree {
    font-size: 8em; 
    font-family: verdana,helvetica, sans-serif; 
    text-decoration: none;
    color: black;	
    background-color: transparent;
}

#lenya-info-tree a:hover {
    color: #FF3333;
    background-color: transparent;
}
   
#lenya-info-tree td {
    font-size: 8pt; 
    font-family: verdana,helvetica, sans-serif; 
}
   
#lenya-info-treecanvas { border: dotted 1px #CCCCCC; width: 200px; float: left; padding: 10px; margin: 2px; font-size: 8pt; }
#lenya-info-content { border: dotted 1px #CCCCCC; height: 600px; width: 700px; float: left; padding: 10px; margin: 2px;}

	.lenya-tab {
	width: auto;
	font-family: verdana, sans-serif;
	font-size:  x-small;
	background-color: #F5F4E9; 
	padding: 20px;
	color: black;
	border: solid 1px #CCCCCC;
	position: relative;
	top: 1px;
}

.lenya-tablink {
	color: #666666;
	font-size: x-small;
	display: inline; /*mandatory*/
	margin-right: .5em;
	padding: 0px 1em;
	position: relative;
	top: 1px;
	
	text-decoration: none;
	
	background-color: #DDDCCF; 
	border: solid 1px #CCCCCC;
}

.lenya-tablink-active {
	color: black;
	font-size:  x-small;
	display: inline; /*mandatory*/
	margin-right: .5em;
	padding: 0px 1em;
	position: relative;
	top: 1px;
	
	text-decoration: none;
	
	background-color: #F5F4E9; 
	border: solid 1px #CCCCCC;
	border-bottom: solid 1px #F5F4E9;
	z-index: 1;
}	
</style>	
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