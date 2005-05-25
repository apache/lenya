/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
// $Id$
//**************************************************************** 
// TreeView by Marcelino Martins http://www.treeview.net
//**************************************************************** 
 
// Definition of class Folder 
// ***************************************************************** 
function Folder(folderDescription, hreference) //constructor 
{ 
  //constant data 
  ICONPATH = CONTEXT_PREFIX + '/lenya/images/tree/';
  this.desc = folderDescription; 
  this.hreference = hreference;
  this.area = "";
  this.documentid = "";
  this.id = -1;
  this.navObj = 0;
  this.iconImg = 0; 
  this.nodeImg = 0;
  this.isLastNode = 0;
  this.iconSrc = ICONPATH + "ftv2folderopen.gif";
  this.iconSrcClosed = ICONPATH + "ftv2folderclosed.gif";
  this.children = new Array;
  this.nChildren = 0;
  this.level = 0;
  this.leftSideCoded = "";
  this.isLastNode=false;
  this.parentObj = null;
  this.maySelect=true;
  this.prependHTML = "";
 
  //dynamic data 
  this.isOpen = false
  this.isLastOpenedFolder = false
  this.isRendered = 0
  this.isLoaded = false
 
  //methods 
  this.initialize = initializeFolder 
  this.reinitialize = reinitializeFolder 
  this.setState = setStateFolder 
  this.addChild = addChild 
  this.createIndex = createEntryIndex 
  this.escondeBlock = escondeBlock
  this.esconde = escondeFolder 
  this.folderMstr = folderMstr 
  this.renderOb = drawFolder 
  this.totalHeight = totalHeight 
  this.subEntries = folderSubEntries 
  this.linkHTML = linkFolderHTML
  this.blockStartHTML = blockStartHTML
  this.blockEndHTML = blockEndHTML
  this.nodeImageSrc = nodeImageSrc
  this.iconImageSrc = iconImageSrc
  this.getID = getID
  this.forceOpeningOfAncestorFolders = forceOpeningOfAncestorFolders
} 
 
function initializeFolder(level, lastNode, leftSide) 
{ 
  var j=0 
  var i=0       
  nc = this.nChildren 
   
  this.createIndex() 
  this.level = level
  this.leftSideCoded = leftSide

  if (browserVersion == 0 || STARTALLOPEN==1)
    this.isOpen=true;

  if (level>0)
    if (lastNode) //the last child in the children array 
    leftSide = leftSide + "0"
  else
    leftSide = leftSide + "1"

  this.isLastNode = lastNode
 
  if (nc > 0) 
  { 
    level = level + 1 
    for (i=0 ; i < this.nChildren; i++)  
    { 
      if (i == this.nChildren-1) 
        this.children[i].initialize(level, 1, leftSide)
      else 
        this.children[i].initialize(level, 0, leftSide)
    } 
  } 
} 

// call this after ading new children
function reinitializeFolder() 
{ 
  var i=0;       
  var nc = this.nChildren 
  var leftSide;
   
  if (this.level>0) {
    if (this.isLastNode) { //the last child in the children array 
      leftSide = this.leftSideCoded + "0"
    } else {
      leftSide = this.leftSideCoded + "1"
    }
  }

  if (nc > 0) 
  { 
    var level = this.level + 1 
    for (i=0 ; i < nc; i++)  
    { 
      if (i == nc-1) 
        this.children[i].initialize(level, 1, leftSide)
      else 
        this.children[i].initialize(level, 0, leftSide)
    } 
  } 
} 
 
function setAreaTabs(url) 
{
   // rewrite the links of the language tabs when a node is clicked
   var authoringTab = getElById('authoring-tab');
   var liveTab = getElById('live-tab');

   // make sure the area tabs are there (they are not in the link popup, for instance)
   if (authoringTab != null && liveTab != null) {
     area = url.substring(url.indexOf('lenya.area=')+11, url.length);
     urlhead = url.substring(0, url.indexOf(area)); // strip usecase   
     urltail = url.substring(url.indexOf(area)+area.length, url.indexOf('?')); // strip usecase
     authoringTab.href = urlhead +'authoring'+ urltail;
     liveTab.href = urlhead +'live'+ urltail;
  }
}


function drawFolder(insertAtObj) 
{ 
  var nodeName = ""
  var auxEv = ""
  var docW = ""

  var leftSide = leftSideHTML(this.leftSideCoded)

  if (browserVersion > 0) 
    auxEv = "<a href='javascript:clickOnNode(\""+this.getID()+"\")'>" 
  else 
    auxEv = "<a>" 

  nodeName = this.nodeImageSrc()
 
  if (this.level>0) 
    if (this.isLastNode) //the last child in the children array 
      leftSide = leftSide + "<td valign=top>" + auxEv + "<img name='nodeIcon" + this.id + "' id='nodeIcon" + this.id + "' src='" + nodeName + "' width=16 height=22 border=0></a></td>"
    else 
      leftSide = leftSide + "<td valign=top background=" + ICONPATH + "ftv2vertline.gif>" + auxEv + "<img name='nodeIcon" + this.id + "' id='nodeIcon" + this.id + "' src='" + nodeName + "' width=16 height=22 border=0></a></td>"

  this.isRendered = 1

  if (browserVersion == 2) { 
    if (!doc.yPos) 
      doc.yPos=20 
  } 

  docW = this.blockStartHTML("folder");

  docW = docW + "<tr>" + leftSide + "<td valign=top>";
  if (USEICONS)
  {
    docW = docW + this.linkHTML(false) 
    docW = docW + "<img id='folderIcon" + this.id + "' name='folderIcon" + this.id + "' src='" + this.iconImageSrc() + "' border=0></a>"
  }
  else
  {
    if (this.prependHTML == "")
        docW = docW + "<img src=" + ICONPATH + "ftv2blank.gif height=2 width=2>"
  }
  if (WRAPTEXT)
    docW = docW + "</td>"+this.prependHTML+"<td valign=middle width=100%>"
  else
    docW = docW + "</td>"+this.prependHTML+"<td valign=middle nowrap width=100%>"
  if (USETEXTLINKS) 
  { 
    docW = docW + this.linkHTML(true) 
    docW = docW + this.desc;
    if (this.hreference) {
      docW = docW + "</a>"
    }
  } 
  else 
    docW = docW + this.desc
  docW = docW + "</td>"

  docW = docW + this.blockEndHTML()

  if (insertAtObj == null)
  {
    if (supportsDeferral) {
      doc.write("<div id=domRoot></div>") //transition between regular flow HTML, and node-insert DOM DHTML
      insertAtObj = getElById("domRoot")
      insertAtObj.insertAdjacentHTML("beforeEnd", docW)
    }
    else
      doc.write(docW)
  }
  else
  {
      insertAtObj.insertAdjacentHTML("afterEnd", docW)
  }
 
  if (browserVersion == 2) 
  { 
    this.navObj = doc.layers["folder"+this.id] 
    if (USEICONS)
      this.iconImg = this.navObj.document.images["folderIcon"+this.id] 
    this.nodeImg = this.navObj.document.images["nodeIcon"+this.id] 
    doc.yPos=doc.yPos+this.navObj.clip.height 
  } 
  else if (browserVersion != 0)
  { 
    this.navObj = getElById("folder"+this.id)
    if (USEICONS)
      this.iconImg = getElById("folderIcon"+this.id) 
    this.nodeImg = getElById("nodeIcon"+this.id)
  } 
} 
 
function setStateFolder(isOpen) 
{ 
  var subEntries 
  var totalHeight 
  var fIt = 0 
  var i=0 
  var currentOpen
 
  if (isOpen == this.isOpen) 
    return 
 
  if (browserVersion == 2)  
  { 
    totalHeight = 0 
    for (i=0; i < this.nChildren; i++) 
      totalHeight = totalHeight + this.children[i].navObj.clip.height 
      subEntries = this.subEntries() 
    if (this.isOpen) 
      totalHeight = 0 - totalHeight 
    for (fIt = this.id + subEntries + 1; fIt < nEntries; fIt++) 
      indexOfEntries[fIt].navObj.moveBy(0, totalHeight) 
  }  
  this.isOpen = isOpen;

  if (this.getID()!=foldersTree.getID() && PRESERVESTATE && !this.isOpen) //closing
  {
     currentOpen = GetCookie("clickedFolder")
     if (currentOpen != null) {
         currentOpen = currentOpen.replace(this.getID()+cookieCutter, "")
         SetCookie("clickedFolder", currentOpen)
     }
  }
  
  if (!this.isOpen && this.isLastOpenedfolder)
  {
    lastOpenedFolder = null;
    this.isLastOpenedfolder = false;
  }
  propagateChangesInState(this) 
} 
 
function propagateChangesInState(folder) 
{   
  var i=0 

  //Change icon
  if (folder.nChildren > 0 && folder.level>0)  //otherwise the one given at render stays
    folder.nodeImg.src = folder.nodeImageSrc()

  //Change node
  if (USEICONS)
    folder.iconImg.src = folder.iconImageSrc()

  //Propagate changes
  for (i=folder.nChildren-1; i>=0; i--) 
    if (folder.isOpen) 
      folder.children[i].folderMstr(folder.navObj)
    else 
      folder.children[i].esconde() 
} 
 
function escondeFolder() 
{ 
  this.escondeBlock()
   
  this.setState(0) 
} 
 
function linkFolderHTML(isTextLink) 
{ 
  var docW = "";

  if (this.hreference) 
  { 
  if (USEFRAMES)
    docW = docW + '<a href="' + this.hreference + '" TARGET="basefrm" '
  else
    docW = docW + '<a href="' + this.hreference + '" TARGET="_self" '
        
    if (isTextLink) {
        docW += 'id="itemTextLink'+this.id+'" ';
    }

    if (browserVersion > 0) 
      docW = docW + 'onClick="javascript:clickOnFolder(\''+this.getID()+'\')"'

    docW = docW + '>'
  } 
  else {
//    docW = docW + "<a>" 
  }

  return docW;
} 
 
function addChild(childNode) 
{ 
  this.children[this.nChildren] = childNode 
  childNode.parentObj = this
  this.nChildren++ 
  return childNode 
} 
 
function folderSubEntries() 
{ 
  var i = 0 
  var se = this.nChildren 
 
  for (i=0; i < this.nChildren; i++){ 
    if (this.children[i].children) //is a folder 
      se = se + this.children[i].subEntries() 
  } 
 
  return se 
} 

function nodeImageSrc() {
  var srcStr = "";

  if (this.isLastNode) //the last child in the children array 
  { 
    if (this.isLoaded && this.nChildren == 0)
      srcStr = ICONPATH + "ftv2lastnode.gif"
    else
      if (this.isOpen)
        srcStr = ICONPATH + "ftv2mlastnode.gif"  
      else
        srcStr = ICONPATH + "ftv2plastnode.gif"  
  } 
  else 
  { 
    if (this.isLoaded && this.nChildren == 0)
      srcStr = ICONPATH + "ftv2node.gif"
    else
      if (this.isOpen)
        srcStr = ICONPATH + "ftv2mnode.gif"
      else
        srcStr = ICONPATH + "ftv2pnode.gif"
  }   
  return srcStr;
}

function iconImageSrc() {
  if (this.isOpen)
    return(this.iconSrc)
  else
    return(this.iconSrcClosed)
} 

// Definition of class Item (a document or link inside a Folder) 
// ************************************************************* 
 
function Item(itemDescription, itemLink, target) // Constructor 
{ 
  // constant data 
  this.desc = itemDescription 
  this.link = itemLink    
  this.area = ""
  this.documentid = ""
  this.id = -1 //initialized in initalize() 
  this.navObj = 0 //initialized in render() 
  this.iconImg = 0 //initialized in render() 
  this.iconSrc = ICONPATH + "ftv2doc.gif" 
  this.isRendered = 0
  this.isLastNode = false
  this.level = 0
  this.leftSideCoded = ""
  this.nChildren = 0
  this.target = target
  this.parentObj = null
  this.maySelect=true
  this.prependHTML = ""
 
  // methods 
  this.initialize = initializeItem 
  this.createIndex = createEntryIndex 
  this.escondeBlock = escondeBlock
  this.esconde = escondeBlock
  this.folderMstr = folderMstr 
  this.renderOb = drawItem 
  this.totalHeight = totalHeight 
  this.blockStartHTML = blockStartHTML
  this.blockEndHTML = blockEndHTML
  this.getID = getID
  this.forceOpeningOfAncestorFolders = forceOpeningOfAncestorFolders
} 
 
function initializeItem(level, lastNode, leftSide) 
{  
  this.createIndex() 
  this.level = level
  this.leftSideCoded = leftSide
  this.isLastNode = lastNode
} 

function escapeQuotes(s)
{
  return s.replace(/\'/g, '\\\'');
}

function drawItem(insertAtObj) 
{ 
  var leftSide = leftSideHTML(this.leftSideCoded)
  var docW = ""

  var fullLink = "href=\""+this.link+"\" target=\""+this.target+"\" onClick=\"clickOnLink('"+this.getID()+"\', '"+escapeQuotes(this.link)+"','"+this.target+"');return false;\"";
  this.isRendered = 1

  if (this.level>0) 
    if (this.isLastNode) //the last 'brother' in the children array 
    { 
      leftSide = leftSide + "<td valign=top><img src='" + ICONPATH + "ftv2lastnode.gif' width=16 height=22></td>"
    } 
    else 
    { 
      leftSide = leftSide + "<td valign=top background=" + ICONPATH + "ftv2vertline.gif><img src='" + ICONPATH + "ftv2node.gif' width=16 height=22></td>"
    } 

  docW = docW + this.blockStartHTML("item")

  docW = docW + "<tr>" + leftSide + "<td valign=top>"
  if (USEICONS && this.link != "undefined") {
      docW = docW + "<a " + fullLink  + " id=\"itemIconLink"+this.id+"\">" + "<img id='itemIcon"+this.id+"' " + "src='"+this.iconSrc+"' border=0>" + "</a>"
  }
  else
    if (this.prependHTML == "")
        docW = docW + "<img src=" + ICONPATH + "ftv2blank.gif height=2 width=3>"

  if (WRAPTEXT)
    docW = docW + "</td>"+this.prependHTML+"<td valign=middle width=100%>"
  else
    docW = docW + "</td>"+this.prependHTML+"<td valign=middle nowrap width=100%>"

  if (USETEXTLINKS && this.link != "undefined") 
    docW = docW + "<a " + fullLink + " id=\"itemTextLink"+this.id+"\">" + this.desc + "</a>"
  else 
    docW = docW + this.desc

  docW = docW + "</td>"

  docW = docW + this.blockEndHTML()
 
  if (insertAtObj == null)
  {
    doc.write(docW)
  }
  else
  {
      insertAtObj.insertAdjacentHTML("afterEnd", docW)
  }

  if (browserVersion == 2) { 
    this.navObj = doc.layers["item"+this.id] 
    if (USEICONS)
      this.iconImg = this.navObj.document.images["itemIcon"+this.id] 
    doc.yPos=doc.yPos+this.navObj.clip.height 
  } else if (browserVersion != 0) { 
    this.navObj = getElById("item"+this.id)
    if (USEICONS)
      this.iconImg = getElById("itemIcon"+this.id)
  } 
} 
 
 
// Methods common to both objects (pseudo-inheritance) 
// ******************************************************** 
 
function forceOpeningOfAncestorFolders() {
  if (this.parentObj == null || this.parentObj.isOpen)
    return
  else {
    this.parentObj.forceOpeningOfAncestorFolders()
    clickOnNodeObj(this.parentObj)
  }
}

function escondeBlock() 
{ 
  if (this.navObj.style==null || typeof this.navObj.style.display == 'undefined') return;
  
  if (browserVersion == 1 || browserVersion == 3) { 
    if (this.navObj.style.display == "none") 
      return 
    this.navObj.style.display = "none" 
  } else { 
    if (this.navObj.visibility == "hiden") 
      return 
    this.navObj.visibility = "hiden" 
  }     
} 
 
function folderMstr(domObj) 
{ 
  if (browserVersion == 1 || browserVersion == 3) { 
    if (t==-1)
      return
    var str = new String(doc.links[t])
    if (str.slice(14,16) != "em")
      return
  }

  if (!this.isRendered)
     this.renderOb(domObj)
  else
    if (browserVersion == 1 || browserVersion == 3) 
      this.navObj.style.display = "block" 
    else 
      this.navObj.visibility = "show" 
} 

function blockStartHTML(idprefix) {
  var idParam = "id='" + idprefix + this.id + "'"
  var docW = ""

  if (browserVersion == 2) 
    docW = "<layer "+ idParam + " top=" + doc.yPos + " visibility=show>"
  else if (browserVersion != 0)
    docW = "<div " + idParam + " style='display:block; position:block;'>"
     
  docW = docW + "<table border=0 cellspacing=0 cellpadding=0 width=100% >"

  return docW
}

function blockEndHTML() {
  var docW = ""

  docW = "</table>"
   
  if (browserVersion == 2) 
    docW = docW + "</layer>"
  else if (browserVersion != 0)
    docW = docW + "</div>"

  return docW
}
 
function createEntryIndex() 
{ 
  this.id = nEntries 
  indexOfEntries[nEntries] = this 
  nEntries++ 
} 
 
// total height of subEntries open 
function totalHeight() //used with browserVersion == 2 
{ 
  var h = this.navObj.clip.height 
  var i = 0 
   
  if (this.isOpen) //is a folder and _is_ open 
    for (i=0 ; i < this.nChildren; i++)  
      h = h + this.children[i].totalHeight() 
 
  return h 
} 


function leftSideHTML(leftSideCoded) {
  var i;
  var retStr = "";

  for (i=0; i<leftSideCoded.length; i++)
  {
    if (leftSideCoded.charAt(i) == "1")
    {
      retStr = retStr + "<td valign=top background=" + ICONPATH + "ftv2vertline.gif><img src='" + ICONPATH + "ftv2vertline.gif' width=16 height=22></td>"
    }
    if (leftSideCoded.charAt(i) == "0")
    {
      retStr = retStr + "<td valign=top><img src='" + ICONPATH + "ftv2blank.gif' width=16 height=22></td>"
    }
  }
  return retStr
}

function getID()
{
  //define a .xID in all nodes (folders and items) if you want to PERVESTATE that
  //work when the tree changes. The value eXternal value must be unique for each
  //node and must node change when other nodes are added or removed
  //The value may be numeric or string, but cannot have the same char used in cookieCutter
  if (typeof this.xID != "undefined") 
    return this.xID
  else
    return this.id
}

 
// Events 
// ********************************************************* 
 
function clickOnFolder(folderId) 
{ 
    var clicked = findObj(folderId)

    if (!clicked.isOpen) {
      clickOnNodeObj(clicked) 
    }

    if (lastOpenedFolder != null && lastOpenedFolder != folderId)
      clickOnNode(lastOpenedFolder); //sets lastOpenedFolder to null

    if (clicked.nChildren==0) {
      lastOpenedFolder = folderId;
      clicked.isLastOpenedfolder = true
    }

    if (isLinked(clicked.hreference)) {
        highlightObjLink(clicked);
        setAreaTabs(clicked.hreference);
    }
} 
 
function clickOnNode(folderId) 
{
  folderObj = findObj(folderId);
  if (INCREMENTAL_LOADING && folderObj.id!=foldersTree.id && !folderObj.isLoaded) {
    loadSubTree(folderObj);
    folderObj.isLoaded = true;
  }

  clickOnNodeObj(folderObj)  
}

function clickOnNodeObj(folderObj) 
{ 
  var state = 0 
  var currentOpen
  
  state = folderObj.isOpen;
  if (folderObj.isLoaded || !INCREMENTAL_LOADING) {
    folderObj.setState(!state); //open<->close
  }

  if (folderObj.id!=foldersTree.id && PRESERVESTATE)
  {
    currentOpen = GetCookie("clickedFolder")
    if (currentOpen == null)
      currentOpen = ""

    if (!folderObj.isOpen) //closing
    {
      currentOpen = currentOpen.replace(folderObj.getID()+cookieCutter, "")
      SetCookie("clickedFolder", currentOpen)
    }
    else
      SetCookie("clickedFolder", currentOpen+folderObj.getID()+cookieCutter)
  }
}

function clickOnLink(clickedId, target, windowName) {
    clickedObj = findObj(clickedId);
    highlightObjLink(clickedObj);
    if (isLinked(target)) {
        window.open(target,windowName);
        setAreaTabs(target);
    }
    if (typeof clickedObj.setState != 'undefined') {
      clickedObj.setState(true); // open the clicked folder
    }
}

function ld  ()
{
  return document.links.length-1
}
 




// Dynamic Loading Functions 
// *************************


function loadInitialTree(area, documentid)
{
  var url = CONTEXT_PREFIX + '/' + PUBLICATION_ID + PIPELINE_PATH + '?area='+area+'&documentid='+documentid+'&language='+CHOSEN_LANGUAGE+'&initial=true&areas='+ALL_AREAS;  
  var xml = loadSitetreeFragment(url);
  if (xml!=null) initialTreeLoaded(xml);
}

function loadSubTree(clicked) 
{
  area = clicked.area;
  documentid = clicked.documentid;  
  
  var url = CONTEXT_PREFIX + '/' + PUBLICATION_ID + PIPELINE_PATH + '?area='+area+'&documentid='+documentid+'&language='+CHOSEN_LANGUAGE+'&areas='+ALL_AREAS;

  var xml = loadSitetreeFragment(url);
  if (xml!=null) subTreeLoaded(xml, clicked, area);
}
  
function loadSitetreeFragment(url)
{
  if (xmlhttp==null) createXMLHttp();
  
  // alert('load subtree for '+url);
  // do synchronous loading 
  // (maybe should be changed to async loading to avoid stall if something goes wrong)
  // (i've had problems with async on mozilla, maybe should use the onload thing?) 
  xmlhttp.open("GET",url,false);  
  /* xmlhttp.onreadystatechange=function() {
    if (xmlhttp.readyState==4) {
      alert('response: '+xmlhttp.responseText);
      //subTreeLoaded(xmlhttp.responseXML, clicked);
    }
  } */
  xmlhttp.setRequestHeader('Accept','text/xml');
  xmlhttp.send(null);
  
  var xml = xmlhttp.responseXML;
  if( xml == null || xml.documentElement == null) {
    alert('Error: could not load sitetree xml');
    return null;
  } 
  return xml;
}

function initialTreeLoaded(xml)
{
  var root = xml.documentElement;

  // loop through all tree children
  var cs = root.childNodes;
  var l = cs.length;
  for (var i = 0; i < l; i++) {
     if (cs[i].tagName == "nav:site") {
        addLoadedSite(cs[i]);
     }
  }
  foldersTree.isLoaded = true;
}

function subTreeLoaded(xml, clicked, area)
{
  var root = xml.documentElement;

  // loop through all tree children
  var cs = root.childNodes;
  var l = cs.length;
  for (var i = 0; i < l; i++) {
     if (cs[i].tagName == "nav:node") {
        addLoadedNode(cs[i], clicked, area);
     } 
  }
  clicked.reinitialize();   
}

function addLoadedSite(site)
{
  var folder = site.getAttribute('folder');
  var area = site.getAttribute('area');
  var label = site.getAttribute('label');
  var langSuffix = '';
  if (CHOSEN_LANGUAGE!=DEFAULT_LANGUAGE) langSuffix = '_'+CHOSEN_LANGUAGE;
  
  label = addProtectedStyle(label, site);
  label = addLabelStyle(label);

  var newSite;  
  if (folder=='true') {
    if (isNodeProtected(site)) {
      newSite = insFld(foldersTree, gFld(label));
    } else {
      newSite = insFld(foldersTree,
        gFld(label, makeHref(area, langSuffix)));
    }
    addNodesRec(site, newSite, area);
  } else {
    if (isNodeProtected(site)) {
      newSite = insDoc(foldersTree, gLnk("S", label));
    } else {
      newSite = insDoc(foldersTree,
        gLnk("S", label, makeHref(area, langSuffix))
      );
    }
  }
  newSite.area = area;
  newSite.documentid = "/";
}

// you can override the makeHref function to create your own links
// useful e.g. for link lookup in xopus or bxe.
var makeHref = function makeDefaultHref(area, link)
{
  return CONTEXT_PREFIX+'/'+PUBLICATION_ID+"/"+area+"/"+link+"?lenya.usecase=tab.overview"; 
}

function addNodesRec(parentNode, parentFolder, area)
{
    var cs = parentNode.childNodes;
    var l = cs.length;
    for (var i = 0; i < l; i++) {
       if (cs[i].tagName == "nav:node") {
          parentFolder.isLoaded = true;
          loadedNode = addLoadedNode(cs[i], parentFolder, area);
          if (cs[i].getAttribute('folder')=='true') addNodesRec(cs[i], loadedNode, area); 
       } 
    }
}

function addLoadedNode(item, parent, area)
{
  var folder = item.getAttribute('folder');
  if (folder=='true') return addLoadedFolder(item, parent, area);
  else return addLoadedDoc(item, parent, area);
}

function addLoadedDoc(doc, parent, area)
{
  var href = doc.getAttribute('href');
  var label = getLabel(doc);
    
  label = addNoLanguageStyle(label, doc);
  label = addCutStyle(label, doc);
  label = addProtectedStyle(label, doc);
  label = addLabelStyle(label);
  
  var newDoc;
  if (isNodeProtected(doc)) {
    newDoc = insDoc(parent,
      gLnk("S", label)
    );
  } else {
    newDoc = insDoc(parent,
      gLnk("S", label, makeHref(area, href))
    );
  }
  newDoc.area = area;
  newDoc.documentid = "/" + doc.getAttribute('basic-url');
  return newDoc;

}

function addLoadedFolder(folder, parent, area)
{
  var href = folder.getAttribute('href');
  var label = getLabel(folder);
    
  label = addNoLanguageStyle(label, folder);
  label = addCutStyle(label, folder);
  label = addProtectedStyle(label, folder);
  label = addLabelStyle(label);
  
  var newFolder;
  if (isNodeProtected(folder)) {  
    newFolder = insFld(parent,
     gFld(label)
    );
  } else {
    newFolder = insFld(parent,
     gFld(label, makeHref(area, href))
    );
  }
  newFolder.area = area;
  newFolder.documentid = "/" + folder.getAttribute('basic-url');
  return newFolder;
}

function getLabel(node) 
{
    var cs = node.childNodes;
    var l = cs.length;
    for (var i = 0; i < l; i++) {
       if (cs[i].tagName=='nav:label' && cs[i].getAttribute('xml:lang')==CHOSEN_LANGUAGE) {
          return cs[i].firstChild.nodeValue;
       } 
    }
    // if chosen language not found, try default language:
    for (var i = 0; i < l; i++) {
       if (cs[i].tagName=='nav:label' && cs[i].getAttribute('xml:lang')==DEFAULT_LANGUAGE) {
          return cs[i].firstChild.nodeValue;
       } 
    }
    // if default language not found either, try any language:
    for (var i = 0; i < l; i++) {
       if (cs[i].tagName=='nav:label') {
          return cs[i].firstChild.nodeValue;
       } 
    }
    return '';
}

function addLabelStyle(label) {
  return "<span style=\"padding: 0px 5px;\">"+label+"</span>";
}

function addNoLanguageStyle(label, node)
{
  if (!existsChosenLanguage(node)) { // document does not exists in this language
    return "<span class=\"lenya-info-nolanguage\">" + label + "</span>";
  }
  return label;
}

function addCutStyle(label, node)
{
  if (CUT_DOCUMENT_ID=='/'+node.getAttribute('basic-url')) { 
    return "<span class=\"lenya-info-cut\">[" + label + "]</span>";
  }
  return label;
}

function addProtectedStyle(label, node)
{
  if (isNodeProtected(node)) { 
    return "<span class=\"lenya-info-protected\">[" + label + "]</span>";
  }
  return label;
}

function isNodeProtected(node) 
{
  var prot = node.getAttribute('protected');
  if (prot == 'true') return true;
  return false;
}

// check if the node has a label of the chosen language
function existsChosenLanguage(node) 
{
    var cs = node.childNodes;
    var l = cs.length;
    for (var i = 0; i < l; i++) {
       if (cs[i].tagName=='nav:label' && cs[i].getAttribute('xml:lang')==CHOSEN_LANGUAGE) {
          return true;
       } 
    }
    return false;
}


// Auxiliary Functions 
// *******************
 
function findObj(id)
{
  var i=0;
  var nodeObj;
  if (typeof foldersTree.xID != "undefined") {
    nodeObj = indexOfEntries[i];
    for(i=0;i<nEntries&&indexOfEntries[i].xID!=id;i++) //may need optimization
      ;
    id = i
  }
  if (id >= nEntries)
    return null; //example: node removed in DB
  else
    return indexOfEntries[id];
}

function isLinked(hrefText) {
    var result = true;
    result = (result && hrefText !=null);
    result = (result && hrefText != '');
    result = (result && hrefText.indexOf('undefined') < 0);
    result = (result && hrefText.indexOf('parent.op') < 0);
    return result;
}

// Do highlighting by changing background and foreg. colors of folder or doc text
function highlightObjLink(nodeObj) {
  if (!HIGHLIGHT || nodeObj==null || nodeObj.maySelect==false) {//node deleted in DB 
    return;
  }

  if (browserVersion == 1 || browserVersion == 3) {
    var clickedDOMObj = getElById('itemTextLink'+nodeObj.id);
    if (clickedDOMObj != null) {
        if (lastClicked != null) {
            var prevClickedDOMObj = getElById('itemTextLink'+lastClicked.id);
            if (prevClickedDOMObj) {
                prevClickedDOMObj.style.color=lastClickedColor;
                prevClickedDOMObj.style.backgroundColor=lastClickedBgColor;
            }
        }
        
        lastClickedColor    = clickedDOMObj.style.color;
        lastClickedBgColor  = clickedDOMObj.style.backgroundColor;
        clickedDOMObj.style.color=HIGHLIGHT_COLOR;
        clickedDOMObj.style.backgroundColor=HIGHLIGHT_BG;
    }
  }
  lastClicked = nodeObj;
  if (PRESERVESTATE)
    SetCookie('highlightedTreeviewLink', nodeObj.getID());
}

function gFld(description, hreference) 
{ 
  folder = new Folder(description, hreference);
  return folder;
} 
 
function gLnk(optionFlags, description, linkData) 
{ 
  var fullLink = "";
  var targetFlag = "";
  var target = "";
  var protocolFlag = "";
  var protocol = "";

  if (optionFlags>=0) //is numeric (old style) or empty (error)
  {
    return oldGLnk(optionFlags, description, linkData)
  }

  targetFlag = optionFlags.charAt(0)
  if (targetFlag=="B")
    target = "_blank"
  if (targetFlag=="P")
    target = "_parent"
  if (targetFlag=="R")
    target = "basefrm"
  if (targetFlag=="S")
    target = "_self"
  if (targetFlag=="T")
    target = "_top"

  if (optionFlags.length > 1) {
    protocolFlag = optionFlags.charAt(1)
    if (protocolFlag=="h")
      protocol = "http://"
    if (protocolFlag=="s")
      protocol = "https://"
    if (protocolFlag=="f")
      protocol = "ftp://"
    if (protocolFlag=="m")
      protocol = "mailto:"
  }

  fullLink = "'" + protocol + linkData + "' target=" + target

  linkItem = new Item(description, protocol+linkData, target)
  return linkItem 
} 

//Function created Aug 1, 2002 for backwards compatibility purposes
function oldGLnk(target, description, linkData)
{
  var fullLink = "";
  //Backwards compatibility code
  if (USEFRAMES)
  {
    if (target==0) 
    { 
    fullLink = "'"+linkData+"' target=\"basefrm\"" 
    } 
    else 
    { 
    if (target==1) 
       fullLink = "'http://"+linkData+"' target=_blank" 
    else 
       if (target==2)
        fullLink = "'http://"+linkData+"' target=\"basefrm\"" 
       else
        fullLink = linkData+" target=\"_top\"" 
    } 
  }
  else
  {
    if (target==0) 
    { 
    fullLink = "'"+linkData+"' target=_top" 
    } 
    else 
    { 
    if (target==1) 
       fullLink = "'http://"+linkData+"' target=_blank" 
    else 
       fullLink = "'http://"+linkData+"' target=_top" 
    } 
  }

  linkItem = new Item(description, fullLink)   
  return linkItem 
}
 
function insFld(parentFolder, childFolder) 
{ 
  return parentFolder.addChild(childFolder) 
} 
 
function insDoc(parentFolder, document) 
{ 
  return parentFolder.addChild(document) 
} 

function preLoadIcons() {
  var auxImg
  auxImg = new Image();
  auxImg.src = ICONPATH + "ftv2vertline.gif";
  auxImg.src = ICONPATH + "ftv2mlastnode.gif";
  auxImg.src = ICONPATH + "ftv2mnode.gif";
  auxImg.src = ICONPATH + "ftv2plastnode.gif";
  auxImg.src = ICONPATH + "ftv2pnode.gif";
  auxImg.src = ICONPATH + "ftv2blank.gif";
  auxImg.src = ICONPATH + "ftv2lastnode.gif";
  auxImg.src = ICONPATH + "ftv2node.gif";
  auxImg.src = ICONPATH + "ftv2folderclosed.gif";
  auxImg.src = ICONPATH + "ftv2folderopen.gif";
  auxImg.src = ICONPATH + "ftv2doc.gif";
}

//Open some folders for initial layout, if necessary
function setInitialLayout() {
  if (browserVersion > 0 && !STARTALLOPEN)
    clickOnNodeObj(foldersTree);
  
  if (!STARTALLOPEN && (browserVersion > 0) && PRESERVESTATE)
    PersistentFolderOpening();
}

//Used with NS4 and STARTALLOPEN
function renderAllTree(nodeObj, parent) {
  var i=0;
  nodeObj.renderOb(parent)
  if (supportsDeferral)
    for (i=nodeObj.nChildren-1; i>=0; i--) 
      renderAllTree(nodeObj.children[i], nodeObj.navObj)
  else
    for (i=0 ; i < nodeObj.nChildren; i++) 
      renderAllTree(nodeObj.children[i], null)
}

function hideWholeTree(nodeObj, hideThisOne, nodeObjMove) {
  var i=0;
  var heightContained=0;
  var childrenMove=nodeObjMove;

  if (hideThisOne)
    nodeObj.escondeBlock()

  if (browserVersion == 2)
    nodeObj.navObj.moveBy(0, 0-nodeObjMove)

  for (i=0 ; i < nodeObj.nChildren; i++) {
    heightContainedInChild = hideWholeTree(nodeObj.children[i], true, childrenMove)
    if (browserVersion == 2) {
      heightContained = heightContained + heightContainedInChild + nodeObj.children[i].navObj.clip.height
      childrenMove = childrenMove + heightContainedInChild
  }
  }

  return heightContained;
}

 
// Simulating inserAdjacentHTML on NS6
// Code by thor@jscript.dk
// ******************************************

if(typeof HTMLElement!="undefined" && !HTMLElement.prototype.insertAdjacentElement){
  HTMLElement.prototype.insertAdjacentElement = function (where,parsedNode)
  {
    switch (where){
    case 'beforeBegin':
      this.parentNode.insertBefore(parsedNode,this)
      break;
    case 'afterBegin':
      this.insertBefore(parsedNode,this.firstChild);
      break;
    case 'beforeEnd':
      this.appendChild(parsedNode);
      break;
    case 'afterEnd':
      if (this.nextSibling) 
        this.parentNode.insertBefore(parsedNode,this.nextSibling);
      else this.parentNode.appendChild(parsedNode);
      break;
    }
  }

  HTMLElement.prototype.insertAdjacentHTML = function(where,htmlStr)
  {
    var r = this.ownerDocument.createRange();
    r.setStartBefore(this);
    var parsedHTML = r.createContextualFragment(htmlStr);
    this.insertAdjacentElement(where,parsedHTML)
  }
}

function getElById(idVal) {
  if (document.getElementById != null)
    return document.getElementById(idVal)
  if (document.all != null)
    return document.all[idVal]
  
  alert("Problem getting element by id")
  return null
}


// Functions for cookies
// Note: THESE FUNCTIONS ARE OPTIONAL. No cookies are used unless
// the PRESERVESTATE variable is set to 1 (default 0)
// The separator currently in use is ^ (chr 94)
// *********************************************************** 

function PersistentFolderOpening()
{
  var stateInCookie;
  var fldStr=""
  var fldArr
  var fldPos=0
  var id
  var nodeObj
  stateInCookie = GetCookie("clickedFolder");
  SetCookie('clickedFolder', "") //at the end of function it will be back, minus null cases

  if(stateInCookie!=null)
  {
    fldArr = stateInCookie.split(cookieCutter)
    for (fldPos=0; fldPos<fldArr.length; fldPos++)
    {
      fldStr=fldArr[fldPos]
      if (fldStr != "") {
        nodeObj = findObj(fldStr)
        if (nodeObj!=null) //may have been deleted
          if (nodeObj.setState) {
            nodeObj.forceOpeningOfAncestorFolders()
            clickOnNodeObj(nodeObj);
          }
//          else 
//            alert("Internal id is not pointing to a folder anymore. Consider using external IDs")
      }
    }
  }
}

function storeAllNodesInClickCookie(treeNodeObj)
{
  var currentOpen
  var i = 0

  if (typeof treeNodeObj.setState != "undefined") //is folder
  {
    currentOpen = GetCookie("clickedFolder")
    if (currentOpen == null)
      currentOpen = ""

    if (treeNodeObj.getID() != foldersTree.getID())
      SetCookie("clickedFolder", currentOpen+treeNodeObj.getID()+cookieCutter)

    for (i=0; i < treeNodeObj.nChildren; i++) 
        storeAllNodesInClickCookie(treeNodeObj.children[i])
  }
}

function CookieBranding(name) {
  if (typeof foldersTree.treeID != "undefined")
    return name+foldersTree.treeID //needed for multi-tree sites. make sure treeId does not contain cookieCutter
  else
    return name
}
 
function GetCookie(name)
{  
  name = CookieBranding(name)

  var arg = name + "=";  
  var alen = arg.length;  
  var clen = document.cookie.length;  
  var i = 0;  

  while (i < clen) {    
    var j = i + alen;    
    if (document.cookie.substring(i, j) == arg)      
      return getCookieVal (j);    
    i = document.cookie.indexOf(" ", i) + 1;    
    if (i == 0) break;   
  }  
  return null;
}

function getCookieVal(offset) {  
  var endstr = document.cookie.indexOf (";", offset);  
  if (endstr == -1)    
  endstr = document.cookie.length;  
  return unescape(document.cookie.substring(offset, endstr));
}

function SetCookie(name, value) 
{  
  var argv = SetCookie.arguments;  
  var argc = SetCookie.arguments.length;  
  var expires = (argc > 2) ? argv[2] : null;  
  //var path = (argc > 3) ? argv[3] : null;  
  var domain = (argc > 4) ? argv[4] : null;  
  var secure = (argc > 5) ? argv[5] : false;  
  var path = "/"; //allows the tree to remain open across pages with diff names & paths

  name = CookieBranding(name)

  document.cookie = name + "=" + escape (value) + 
  ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) + 
  ((path == null) ? "" : ("; path=" + path)) +  
  ((domain == null) ? "" : ("; domain=" + domain)) +    
  ((secure == true) ? "; secure" : "");
}

function ExpireCookie (name) 
{  
  var exp = new Date();  
  exp.setTime (exp.getTime() - 1);  
  var cval = GetCookie (name);  
  name = CookieBranding(name)
  document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString();
}


//To customize the tree, overwrite these variables in the configuration file (demoFramesetNode.js, etc.)
var USETEXTLINKS = 0 
var STARTALLOPEN = 0
var USEFRAMES = 1
var USEICONS = 1
var WRAPTEXT = 0
var PRESERVESTATE = 0
var ICONPATH = ''
var HIGHLIGHT = 0
var HIGHLIGHT_COLOR = 'white';
var HIGHLIGHT_BG    = 'blue';
var BUILDALL = 0


//Other variables
var lastClicked = null;
var lastClickedColor;
var lastClickedBgColor;
var indexOfEntries = new Array 
var nEntries = 0 
var browserVersion = 0 
var selectedFolder=0
var lastOpenedFolder=null
var t=5
var doc = document
var supportsDeferral = false
var cookieCutter = '^' //You can change this if you need to use ^ in your xID or treeID values

var xmlhttp=null;

doc.yPos = 0

// Main function
// ************* 

// This function uses an object (navigator) defined in
// ua.js, imported in the main html page (left frame).
function initializeDocument(area, documentid) 
{ 
  preLoadIcons();
  switch(navigator.family)
  {
    case 'ie4':
      browserVersion = 1 //Simply means IE > 3.x
      break;
    case 'opera':
      browserVersion = (navigator.version > 6 ? 1 : 0); //opera7 has a good DOM
      break;
    case 'nn4':
      browserVersion = 2 //NS4.x 
      break;
    case 'gecko':
      browserVersion = 3 //NS6.x
      break;
    case 'safari':
      browserVersion = 1 //Safari Beta 3 seems to behave like IE in spite of being based on Konkeror
      break;
  default:
      browserVersion = 0 //other, possibly without DHTML  
      break;
  }

  supportsDeferral = ((navigator.family=='ie4' && navigator.version >= 5 && navigator.OS != "mac") || browserVersion == 3);
  supportsDeferral = supportsDeferral & (!BUILDALL)
  if (!USEFRAMES && browserVersion == 2)
    browserVersion = 0;
  eval(String.fromCharCode(116,61,108,100,40,41))

  //If PRESERVESTATE is on, STARTALLOPEN can only be effective the first time the page 
  //loads during the session. For subsequent (re)loads the PRESERVESTATE data stored 
  //in cookies takes over the control of the initial expand/collapse
  if (PRESERVESTATE && GetCookie("clickedFolder") != null)
    STARTALLOPEN = 0

  if (INCREMENTAL_LOADING) loadInitialTree(area, documentid);
  
  //foldersTree (with the site's data) is created in an external .js (demoFramesetNode.js, for example)
  foldersTree.initialize(0, true, "") 
  if (supportsDeferral && !STARTALLOPEN)
    foldersTree.renderOb(null) //delay construction of nodes
  else {
    renderAllTree(foldersTree, null);

    if (PRESERVESTATE && STARTALLOPEN)
      storeAllNodesInClickCookie(foldersTree)

    //To force the scrollable area to be big enough
    if (browserVersion == 2) 
      doc.write("<layer top=" + indexOfEntries[nEntries-1].navObj.top + ">&nbsp;</layer>") 

    if (browserVersion != 0 && !STARTALLOPEN)
      hideWholeTree(foldersTree, false, 0)
  }

  setInitialLayout()

  if (PRESERVESTATE && GetCookie('highlightedTreeviewLink')!=null  && GetCookie('highlightedTreeviewLink')!="") {
    var nodeObj = findObj(GetCookie('highlightedTreeviewLink'))
    if (nodeObj!=null){
      nodeObj.forceOpeningOfAncestorFolders()
      highlightObjLink(nodeObj);
    }
    else
      SetCookie('highlightedTreeviewLink', '')
  }

  if (INCREMENTAL_LOADING && xmlhttp==null) { 
    createXMLHttp();
  }
} 

// create the xmlhttp object
function createXMLHttp() 
{
    /*@cc_on @*/
    /*@if (@_jscript_version >= 5)
    // JScript gives us Conditional compilation, we can cope with old IE versions.
    // and security blocked creation of the objects.
     try {
      xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
     } catch (e) {
      try {
       xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (E) {
       xmlhttp = false;
      }
     }
    @end @*/
    if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
      xmlhttp = new XMLHttpRequest();
    }
}
    
// Load a page as if a node on the tree was clicked (synchronize frames)
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
  var i=0;
  
  for (i = 0; i < nEntries && (indexOfEntries[i].link == undefined || indexOfEntries[i].link.split('?')[0] != srclink) && (indexOfEntries[i].hreference == undefined || indexOfEntries[i].hreference.split('?')[0] != srclink); i++) {
  }
  //FIXME: extend to allow for mapping of index.html to index_defaultlanguage.html
  if (i >= nEntries) {
     return 1; //example: node removed in DB
  }
  else {
    return i;
  }
} 