<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.28 2003/09/05 14:42:38 andreas Exp $
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
<xsl:param name="documentextension"/>
<xsl:param name="documenturl"/>
<xsl:param name="chosenlanguage"/>
<xsl:param name="defaultlanguage"/>

<xsl:variable name="extension"><xsl:if test="$documentextension != ''">.</xsl:if><xsl:value-of select="$documentextension"/></xsl:variable>
    
<xsl:template match="lenya/cmsbody">
<html>
<head>

<!-- These three scripts define the tree, do not remove-->
<script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/ua.js"/>
<script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/tree.js"/>
<script src="{$contextprefix}/{$publicationid}/{$area}/info-sitetree/sitetree.js?language={$chosenlanguage}"/>
<script language="JavaScript">// Load a page as if a node on the tree was clicked (synchronize frames)
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
  //document.body.scrollTop = docObj.navObj.offsetTop
}

function findIDbyLink(srclink)
{
<![CDATA[
  var i=0;
  
  for (i = 0; i < nEntries &&
      (indexOfEntries[i].link == undefined || indexOfEntries[i].link.split('?')[0] != srclink) &&
      (indexOfEntries[i].hreference == undefined || indexOfEntries[i].hreference.split('?')[0] != srclink)
      ;
      i++) {
  }
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
<div id="lenya-info-body">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top" width="20%">
<div id="lenya-info-treecanvas">
<!-- Build the tree. -->


	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><a id="de">
				<xsl:call-template name="activate">
					<xsl:with-param name="tablanguage">de</xsl:with-param>
				</xsl:call-template>
			</a></td>
			<td><a id="en">
				<xsl:call-template name="activate">
					<xsl:with-param name="tablanguage">en</xsl:with-param>
				</xsl:call-template>
			</a></td>
		</tr>
	</table>

   <div id="lenya-info-tree">
      <div style="display:none;">
      	<table border="0">
      		<tr>
      			<td>
      				<a style="font-size:7pt;text-decoration:none;color:white" href="http://www.treemenu.net/" target="_blank">JavaScript Tree Menu</a>
      			</td>
      		</tr>
      	</table>
      </div>
  <script>
 	initializeDocument();
 	<xsl:variable name="language-suffix"><xsl:if test="$chosenlanguage != $defaultlanguage">_<xsl:value-of select="$chosenlanguage"/></xsl:if></xsl:variable>
  loadSynchPage('<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/info-<xsl:value-of select="$area"/><xsl:value-of select="$documentid"/><xsl:value-of select="$language-suffix"/><xsl:value-of select="$extension"/>');
   </script>
    </div>
</div>
</td>	
<td valign="top" width="80%">
<div id="lenya-info-content">
	
	<xsl:variable name="url">
		/<xsl:value-of select="$area"/><xsl:if test="$documentid != '/'"><xsl:value-of select="$documenturl"/></xsl:if>
	</xsl:variable>
	
	<strong>URL:&#160;&#160;</strong><xsl:value-of select="$url"/><br/><br/>
	
	<xsl:copy-of select="*"/>
</div>
</td>
</tr>
</table>
</div>
</body>
</html>
</xsl:template>

<xsl:template name="activate">
	<xsl:param name="tablanguage"/>
	<xsl:variable name="docidwithoutlanguage"><xsl:value-of select="substring-before($documentid, '_')"/></xsl:variable>
   <xsl:attribute name="href"><xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/info-<xsl:value-of select="$area"/><xsl:value-of select="$documentid"/>_<xsl:value-of select="$tablanguage"/><xsl:value-of select="$extension"/>?lenya.usecase=info-overview&amp;lenya.step=showscreen</xsl:attribute>
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