<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    exclude-result-prefixes="page xhtml"
>

<xsl:param name="publication"/>
<xsl:param name="module"/>
<xsl:param name="name"/>
<xsl:param name="publicationname"/>
<xsl:param name="publicationlanguages"/>

<xsl:template match="/content">
  <html>
    <head>
      <title><xsl:value-of select="$publication"/>&#160;<xsl:value-of select="$name"/>&#160;<i18n:text>Relations</i18n:text></title>
<style type="text/css">
h1{
   margin: 0 0 5px 0;
}
h2{
   margin: 10px 0 5px 0;
}
h3{
   margin: 0 0 5px 0;
}
ol,ul{
   margin: 0 10px 5px 20px;
}
#undo{
   float: right;
}
#legend{
   clear: both;
   margin: 0px 0 10px 0;
}
#all{
   float: left;
   clear: left;
   width: 40%;
   margin: 0 10px 0 20px;
}
#structure{
   float: right;
   width: 55%;
   margin: 0px 0px 10px 0px;
}
#information{
   clear: left;
   width: 40%;
   border: solid 2px #FFCC33;
   margin: 20px 0px 0 0;
}
#instructions{
   clear: both;
   border: solid 1px #000000;
   margin: 20px 0px 0 0;
   padding: 0px 10px 5px 10px;
}
.normal{
   display: inline;
}
.orphan{
   background-color: #FFFF66; 
   display: inline;
}
.duplicate{
   background-color: #FFCCCC; 
   display: inline;
}
.selected{
   background-color: #CCCCCC; 
   display: inline;
}
.selectedtree{
   background-color: #EEEEEE; 
   display: inline;
}
.container{
   margin: 0 0px 0 20px;
   border-left: 1px dotted #CCCCCC;
}
.delete{
   color: white;
   text-decoration: none;
   background-color: #CC0000; 
   margin: 0 0px 0 10px;
   font-size: smaller;
   display: inline;
   padding: 2px;
}
.expand{
   color: black;
   text-decoration: none;
   background-color: #33FF33; 
   margin: 10px 10px 10px 0px;
   font-size: smaller;
   display: inline;
   line-height: 180%;
   padding: 2px;
}
.rootsibling{
   color: black;
   text-decoration: none;
   background-color: #FFCC33; 
   margin: 0 10px 0 20px;
   font-size: smaller;
   display: inline;
}
.sibling{
   color: black;
   text-decoration: none;
   background-color: #FFCC33; 
   margin: 10px 10px 10px 0px;
   font-size: smaller;
   display: inline;
   line-height: 180%;
   padding: 2px;
}
.child{
   color: black;
   text-decoration: none;
   background-color: #FFCC33; 
   margin: 10px 10px 10px 0px;
   font-size: smaller;
   display: inline;
   line-height: 180%;
   padding: 2px;
}
.undo{
   background-color: #CC0000; 
   margin: 0 10px 0 0px;
   font-size: smaller;
   display: inline;
   padding: 2px;
}
.undo a{
   color: white;
   text-decoration: none;
}
.redo{
   background-color: #00CC00; 
   margin: 0 10px 0 0px;
   font-size: smaller;
   display: inline;
   padding: 2px;
}
.redo a{
   color: white;
   text-decoration: none;
}

</style>

<script language="JavaScript">
// Configuration
MAX_UNDOS = 5;
START_COLLAPSED = false;

// StringBuffer
function StringBuffer(){ this.buffer = []; }
StringBuffer.prototype.append = function append(string){
   this.buffer.push(string);
   return this;
};
StringBuffer.prototype.toString = function toString(){ return this.buffer.join(""); };
// UndoStorage
function UndoSystem(maximum){
   this.store = new Array();
   this.max = maximum;
   this.current = 0;
}
<xsl:text disable-output-escaping="yes">
UndoSystem.prototype.add = function add(string){
   if((this.current &gt; 1) &amp;&amp; (this.current &lt; this.store.length)) this.store.length = this.current;
   if(this.current &gt; this.max){
      this.store.splice(0, 1);
   }else{
      this.current++;
   }
   this.store.push(string);
}
UndoSystem.prototype.undo = function undo(increment){
   this.current -= increment;
   if(this.current &lt; 1) this.current = 1;
   return this.store[this.current - 1];
}
UndoSystem.prototype.redo = function redo(increment){
   this.current += increment;
   if(this.current &gt; this.store.length) this.current = this.store.length;
</xsl:text>
   return this.store[this.current -1 ];
}
UndoSystem.prototype.getSize = function getSize(){
   return this.store.length;
}
UndoSystem.prototype.getCurrent = function getCurrent(){
   return this.current;
}

// Init
var selected = "";
var selectedInstance = 0;
var allCount = 0;
var unids = [];
var allResources= new Object();
var allType = new Object();
var allId = new Object();
var used = new Object();
var structure = new StringBuffer();  // Changes to String after initialization
var undos = new UndoSystem(MAX_UNDOS);
var collapsed = new Object();
var forceCollapse = START_COLLAPSED;
// All
<xsl:apply-templates select="index" mode="js"/>

// Structure
// &lt; begin children, &gt; end children
<xsl:apply-templates select="resources" mode="js"/>

undos.add('!0!' + structure);
// Display Functions
function drawUndo(){
   number = undos.getSize();
   current = undos.getCurrent();
<xsl:text disable-output-escaping="yes">
   if(2 &gt; number) return;
   buffer = new StringBuffer();
   for(u = current - 1; u &gt; 0; u--){
      buffer.append('&lt;div class="undo"&gt;&lt;a href="javascript:undo(' + u + ');"&gt;UNDO ' + u + '&lt;a&gt;&lt;/div&gt;');
   }
   for(u = 1; u &lt;= (number - current); u++){
      buffer.append('&lt;div class="redo"&gt;&lt;a href="javascript:redo(' + u + ');"&gt;REDO ' + u + '&lt;a&gt;&lt;/div&gt;');
   }
   undoDiv = getElementForId("undo");
   undoDiv.innerHTML = buffer.toString();

}
function getSpan(unid, useUsed, inSelection, level){
   // Decide class
   className = 'normal';
   usedCount = 0;
   if(used[unid]) usedCount = used[unid];
   if(unid == selected){
      if(useUsed){
         if(usedCount == selectedInstance){
            className= 'selected';
         }else{
            className= 'selectedtree';
         }
      }else{
         if(selectedInstance == 0){
            className = 'selected';
         }else{
            className = 'selectedtree';
         }
      }
   }else{
      if(inSelection != -1){
         className = 'selectedtree';
      }else{
         if(usedCount &gt; 2) usedCount = 2;
         switch(usedCount){
            case 0:
               className ='orphan';
               break;
            case 1:
                break;
            case 2:
               className ='duplicate';
               break;
            // default:
         }
      }
   }
   // Build String
   buffer = new StringBuffer();
//   for(i = 0; i &lt; level; i++){
//       buffer.append('&#160;&#160;&#160;&#160;');
//   }
   buffer.append('&lt;div class="'+ className+ '"&gt;');
   buffer.append('&lt;a href="javascript:select(\'' + unid);
   if(useUsed){
      buffer.append('!'+ used[unid]);
   }
   buffer.append('\');"&gt;' + allId[unid] + '&lt;a&gt; (' + allType[unid] + ')');
   buffer.append('&lt;/div&gt;');
   if(useUsed){
      buffer.append('&lt;a href="javascript:remove(\'' + unid + '!'+ used[unid] +'\');" class="delete"&gt;Remove&lt;a&gt;');
   }
   return buffer.toString();
</xsl:text>
}

function resetUsed(){
   for(u in used){
      used[u] = 0;
   }
}

// #############################################################3
function drawStructure(){
   resetUsed();
   structureString = structure.toString();
   structureBuffer = new StringBuffer();
<xsl:text disable-output-escaping="yes">
   structureBuffer.append('&lt;a href="javascript:sibling(\'root\');" class="rootsibling"&gt;Sibling&lt;a&gt;&lt;br&gt;');
   parts = new Array();
   parts = structureString.split('&lt;');
   siblings = new Array();
   childs = new Array();
   siblings.push("");
   childs.push("");
   level = -1;
   inSelection = -1;
   inCollapsed = -1;
   unid ="root";
   for(p in parts){
      level++;
      part = parts[p];
      startName = 0;
      if(part.charAt(startName) == '&gt;'){ // No children
         if(-1 == inCollapsed){
            structureBuffer.append(siblings.pop());
            siblings.push("");
            structureBuffer.append(childs.pop());
         }
      }else{
         if("root" != unid){
            if(forceCollapse) collapsed[unid + '!'+ used[unid]] = 1;
            if(-1 == inCollapsed){
               expandTitle = "Collapse";
               if(collapsed[unid + '!'+ used[unid]]){
                  expandTitle = "Expand";
                  inCollapsed = level;
               }
               structureBuffer.append('&lt;a href="javascript:toggle(\'' + unid + '!'+ used[unid] +'\');" class="expand"&gt;'+expandTitle+'&lt;a&gt;');
               if(-1 != inCollapsed){
                  structureBuffer.append(siblings.pop());
                  siblings.push("");
                  childs.pop();
               }
            }
         }
         if(-1 == inCollapsed) structureBuffer.append(childs.pop());
      }
      while(part.charAt(startName) == '&gt;'){
         level--;
         startName++;
         if(level &lt; inCollapsed){
            inCollapsed = -1;
         }
         if(-1 == inCollapsed){
            structureBuffer.append(siblings.pop());
            structureBuffer.append('&lt;/div&gt;');
         }
      }
      if(startName &lt; part.length){
         unid = part.substring(startName);
         used[unid]++;
         if(level &lt;= inSelection){
            inSelection = -1;
         }
         if((selected == unid) &amp;&amp; (used[unid] == selectedInstance)){
           inSelection = level;
         }
         if(-1 == inCollapsed){
            structureBuffer.append('&lt;div class="container"&gt;');
            structureBuffer.append(getSpan(unid, true, inSelection, level) + '&lt;br&gt;');
            if(-1 == inSelection){
               siblings.push('&lt;a href="javascript:sibling(\'' + unid + '!'+ used[unid] +'\');" class="sibling"&gt;Sibling&lt;a&gt;');
               childs.push('&lt;a href="javascript:child(\'' + unid + '!'+ used[unid] +'\');" class="child"&gt;Child&lt;a&gt;');
            }else{
               siblings.push("");  //Marks end of DIV for selected elements.  "&#160;" will force empty line, but looks silly.
               childs.push("");
            }
         }
</xsl:text>
      }
   }
   structureDiv = getElementForId('structure');
   structureDiv.innerHTML = structureBuffer.toString();
   forceCollapse = false;
}



function drawAll(){
   allBuffer = new StringBuffer();
   for(u in unids){
<xsl:text disable-output-escaping="yes">
      allBuffer.append(getSpan(unids[u], false, -1, 0) + '&lt;br&gt;');
   }
</xsl:text>
   allDiv = getElementForId('all');
   allDiv.innerHTML = allBuffer.toString();
}
function redraw(){
   drawStructure();
   drawAll();
   drawUndo();
}
function setInformation(string){
   informationDiv = getElementForId("information");
   informationDiv.innerHTML = string;
}
function select(key){
   parts = key.split('!', 2);
   selected = parts[0];
   selectedInstance = 0;
<xsl:text disable-output-escaping="yes">
   if(parts.length &gt; 1) selectedInstance = parts[1];
   setInformation('ID: ' + allId[selected] + '&lt;br&gt;Type: ' + allType[selected] + '&lt;br&gt;UNID: ' + selected + allResources[selected] + "&lt;br&gt;Appearances: " + used[selected]);
   redraw();
}
function sibling(key){
  move(key, 'sibling');
}
function child(key){
  move(key, 'child');
}
function remove(key){
   //oldSelected = selected;
   //oldSelectedInstance = selectInstance;
   parts = key.split('!', 2);
   selected = parts[0];
   selectedInstance = 0;
   if(parts.length &gt; 1) selectedInstance = parts[1];
   move('root', 'delete');
   //selected = oldSelected;
   selectedInstance = 0;
   return;  // Some browsers navigate to return value if no return statement.
}
function move(key, action){
   if(0 == selected.length){
      setInformation('The requested action requires a Resource to be selected.');
      return;
   }
   resetUsed();
   parts = key.split('!', 2);
   target = parts[0];
   if(('root' == target) &amp;&amp; (0 == selectedInstance)) {
      structure = selected + '&lt;&gt;' + structure;
      redraw();
      return;
   }
   targetInstance = 0;
   if(parts.length &gt; 1) targetInstance = parts[1];
   selection = new StringBuffer();
   beforeTarget = new StringBuffer();
   afterTarget = new StringBuffer();
   structureString = structure.toString();
   parts = new Array();
   parts = structureString.split('&lt;');
   level = -1;
   inSelection = -1;
   inTarget = -1;
   if('root' == target){
      inTarget = -2;
   }
   if(0 == selectedInstance){
      selection.append(selected + '&lt;&gt;');
   }
   for(p in parts){
      if(-1 != level){
         if(-1 == inSelection){
            if(-2 == inTarget){
               afterTarget.append('&lt;');
            }else{
               beforeTarget.append('&lt;');
               if(('child' == action) &amp;&amp; (inTarget &gt; -1)){
                  inTarget = -2;
               }
            }
         }else{
            selection.append('&lt;');
         }
      }
      level++;
      part = parts[p];
      startName = 0;
      while(part.charAt(startName) == '&gt;'){
         if(-1 == inSelection){
            if(-2 == inTarget){
               afterTarget.append('&gt;');
            }else{
               beforeTarget.append('&gt;');
            }
         }else{
            selection.append('&gt;');
         }
         level--;
         if(level &lt;= inSelection){
            inSelection = -1;
         }
         if(level &lt;= inTarget){
            inTarget = -2;
         }
         startName++;
      }
      if(startName &lt; part.length){
         unid = part.substring(startName);
         used[unid]++;
         if((selected == unid) &amp;&amp; (used[unid] == selectedInstance)){
           inSelection = level;
         }
         if((target == unid) &amp;&amp; (used[unid] == targetInstance)){
           inTarget = level;
         }
         if(inSelection == -1){
            if(inTarget == -2){
               afterTarget.append(unid);
            }else{
               beforeTarget.append(unid);
            }
         }else{
            selection.append(unid);
         }
      }
   }
   if('delete' == action){
      structure = beforeTarget.toString() + afterTarget.toString();
   }else{
      structure = beforeTarget.toString() + selection.toString() + afterTarget.toString();
   }
   undos.add(selected + '!' + selectedInstance + '!' + structure);

   structureField = getElementForId("relations");
   structureField.value = structure;

   
   // Collapses are saved by position which may not be accurate after changing the structure
   // if a Resource is moved before or after an entry for the same Resource and only one is collapsed 
   // expandAllFunction(); // Prevents issue but annoys user.
   redraw();
}
function useUndo(string){
   parts = string.split('!', 3);
   if(3 != parts.length) return;
   selected = parts[0];
   selectedInstance = parts[1];
   structure = parts[2];
   redraw();
}
function undo(increment){
   useUndo(undos.undo(increment));
}
function redo(increment){
   useUndo(undos.redo(increment));
}
function toggle(key){
   if(collapsed[key]){
      delete collapsed[key];
   }else{
      collapsed[key] = 1;
   }
   drawStructure();
}
function expandAllFunction(){
   for(c in collapsed) delete collapsed[c];
}
function expandAll(){
   expandAllFunction();
   drawStructure();
}
function collapseAll(){
   forceCollapse = true;
   drawStructure();
}

// ## Helper Functions
function addUsed(unid){
   if(used[unid]){
      used[unid]++;
   }else{
      used[unid] = 1;
   }
}
function getElementForId(id){
   if(document.getElementById) return document.getElementById(id);
   if(document.all) return document.all(id);
   if(document.layers) return document.layers(id);
}
</xsl:text></script>
    </head>	
    <body>
<div id="undo">&#160;</div>
<h1><xsl:value-of select="$publication"/>&#160;<xsl:value-of select="$name"/>&#160;<i18n:text>Structure</i18n:text></h1>
<div id="legend">
<!-- TODO: use i18n for labels. -->
<div class="normal"> NORMAL </div>
<div class="orphan"> ORPHAN </div>
<div class="duplicate"> DUPLICATE </div>
<div class="selected"> SELECTED </div>
<div class="selectedtree"> SUBTREE </div>
<div class="expand"><a href="javascript:expandAll();">Expand All</a></div>
<div class="expand"><a href="javascript:collapseAll();">Collapse All</a></div>

</div>
<div id="all">This editor requires JavaScript.  If you are seeing this message:<ul>
<li>The Structure is very large. Please wait.</li>
<li>JavaScript is disabled in the browser.</li>
<li>JavaScript does not work properly in the browser.  (This editor was tested with Mozilla 1.7, Mozilla Firefox 2.0, and Microsoft Internet Explorer 6.0.)</li>
<li>The computer is slow or busy doing something else.</li>
</ul>
</div>
<div id="structure">&#160;</div>
<div id="information">&#160;</div>
<div id="save">
<form method="post" action="/{$publication}/{$module}/save/{$name}">
<input type="hidden" name="id" value="{$name}"/>
<input type="hidden" name="relations" id="relations"/>
<input type="submit" value="Save Structure"/>
</form>
</div>
<div id="instructions">
<h2>Instructions</h2>
This editor has 7 areas. <ol>
<li>Title - Name of Publication and Structure being edited.</li>
<li>Legend - Explanation of background colors<ul>
<li>Normal - Resources appearing once and only once in the Structure.</li>
<li>Orphan - Resources not appearing in the Structure and so only appears in All Resources.</li>
<li>Duplicate - Resources appearing more than once in the Structure.  Select and check Information for the exact number.</li>
<li>Selected - The current selection.</li>
<li>Subtree - All resources that will move with the selection, and other appearances of the selected resource (which will not move unless under the selection.)</li>
</ul></li>
<li>All Resources - Clicking the ID chooses the resource to be ADDED.</li>
<li>Information - Orange box describing the selected Resource including the Titles of existing Translations.</li>
<li>Structure - Hierarchical list on right side.  Clicking an ID chooses a Resource (and all descendants) to be MOVED.  Includes most actions.</li>
<li>Undo - Appears in upper right corner after an action.</li>
<li>Instructions - Everything needed to use this editor.</li>
</ol>
<h3>Actions</h3><ul>
<li>Remove - Removes the resource and all descendants from the Structure.</li>
<li>Sibling - Adds or moves the selected Resource as a sibling of the parent Resource.</li>
<li>Child - Adds or moves the selected Resource as a child of the parent Resource.</li>
<li>Undo/Redo - Clicking an UNDO button will revert to a previous state and displays REDO buttons.  Clicking a REDO button undoes the UNDO.  A new action removes the REDO buttons.</li>
<li>Save - Saves the Structure and returns to the previous page.</li>
<li>Cancel - Returns to the previous page without saving changes.</li>

</ul>
Note that Sibling and Child actions depend on where the selected Resource was clicked.  If the Resource was chosen from the All Resources list, the Resource will be ADDED.  If the Resource was chosen from the Structure list, the Resource and its descendants will be MOVED.  Copying a portion of the structure has not been implemented.
</div>
<script language="JavaScript">
   redraw();
   structureField = getElementForId("relations");
   structureField.value = structure;
</script>
</body></html>
</xsl:template>

<!-- All: JavaScript -->
<xsl:template match="index" mode="js">
<xsl:apply-templates select="resource" mode="js">
   <xsl:sort select="@id"/>
</xsl:apply-templates>
</xsl:template>

<xsl:template match="resource" mode="js">
unids[allCount++] = "<xsl:value-of select="@unid"/>";
used["<xsl:value-of select="@unid"/>"] = 0;
allId["<xsl:value-of select="@unid"/>"] = '<xsl:value-of select="@id"/>';
allType["<xsl:value-of select="@unid"/>"] = '<xsl:value-of select="@type"/>';
allResources["<xsl:value-of select="@unid"/>"] = '<xsl:apply-templates select="translation" mode="js"/>';
</xsl:template>

<xsl:template match="translation" mode="js"><br/>&#160; &#160;<xsl:value-of select="@language"/>:<xsl:value-of select="@title"/></xsl:template>

<!-- Structure: JavaScript -->
<xsl:template match="resources" mode="js"><xsl:apply-templates select="resource" mode="structurejs"/></xsl:template>

<xsl:template match="resource" mode="structurejs">
addUsed("<xsl:value-of select="@unid"/>");
structure.append("<xsl:value-of select="@unid"/><xsl:text disable-output-escaping="yes">&lt;</xsl:text>");
<xsl:apply-templates select="resource" mode="structurejs"/>
<xsl:text disable-output-escaping="yes">structure.append("&gt;");</xsl:text>
</xsl:template>

</xsl:stylesheet> 
