<?xml version="1.0" encoding="UTF-8"?>

<!--
 $Id: rename.xsl,v 1.1 2003/06/05 14:12:21 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
    xmlns:dc="http://dc.org/2003/"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    
<xsl:template match="lenya-info:info">    
    <html>
<head>
	<title></title>
	<script language="javascript" type="text/javascript" src="tabs.js"></script>
	<link rel="StyleSheet" href="default.css" type="text/css" />
</head>
<body onload="Tab(1)">
	<!-- 
		These are the tabs. Make sure that each of them has the correct id,
		target and corresponding number in the domTab() call.
	 -->
	<a href="#" onclick="Tab(1)" id="link1" class="lenya-tablink">Overview</a>
	<a href="#" onclick="Tab(2)" id="link2" class="lenya-tablink">Meta</a>
	<a href="#" onclick="Tab(3)" id="link3" class="lenya-tablink">Assets</a>
	<a href="#" onclick="Tab(4)" id="link4" class="lenya-tablink">Workflow</a>
	<a href="#" onclick="Tab(5)" id="link5" class="lenya-tablink">Revisions</a>

	<!--  
		These are the different content blocks of the tabs, each one needs to
		have the id (starting with "opt" and followed by its number) and the 
		class "tab" to set the look and feel.
	-->
	<div id="contentblock1" class="lenya-tab">
          <table>
              <xsl:apply-templates select="lenya-info:overview"/>
          </table>
		</div>
	<div id="contentblock2" class="lenya-tab">
          <form>
          <table>
              <xsl:apply-templates select="lenya-info:meta"/>
          </table>
             <input type="submit" value="Update Metadata"/>
          </form>
		</div>
	<div id="contentblock3" class="lenya-tab">
          <table>
              <xsl:apply-templates select="lenya-info:assets"/>
          </table>
          <form>
             <input type="file" name="asset" size="40" />
             <input type="submit" value="Upload Asset"/>
          </form>
	</div>
	<div id="contentblock4" class="lenya-tab">
          <table>
              <xsl:apply-templates select="wf"/>
          </table>
	</div>
	<div id="contentblock5" class="lenya-tab">
          <table>
              <xsl:apply-templates select="rc"/>
          </table>
	</div>
</body>
</html>
</xsl:template>

<xsl:template match="lenya-info:overview">
   <tr><td>Title:</td><td><xsl:valueof select="dc:title"/></td></tr>
   <tr><td>Abstract:</td><td><xsl:valueof select="lenya-info:abstract"/></td></tr>
   <tr><td>Status:</td><td><xsl:valueof select="lenya-info:status"/></td></tr>
   <tr><td>Language:</td><td><xsl:valueof select="dc:language"/></td></tr>
   <tr><td>Other Languages:</td><td></td></tr>
   <tr><td>Last edited by:</td><td><xsl:valueof select="lenya-info:lastmodifiedby"/></td></tr>
   <tr><td>Last modified:</td><td><xsl:valueof select="lenya-info:lastmodified"/></td></tr>
   <tr><td>Document ID:</td><td><xsl:valueof select="lenya-info:documentid"/></td></tr>
</xsl:template>

<xsl:template match="lenya-info:meta">
   <tr><td>Title:</td><td><input type="text" id="dc:title"><xsl:attribute name="value"><xsl:valueof select="dc:title"/></xsl:attribute></input></td></tr>
   <tr><td>Creator:</td><td><input type="text" id="dc:creator"><xsl:attribute name="value"><xsl:valueof select="dc:creator"/></xsl:attribute></input></td></tr>
   <tr><td>Subject:</td><td><input type="text" id="dc:subject"><xsl:attribute name="value"><xsl:valueof select="dc:subject"/></xsl:attribute></input></td></tr>
   <tr><td>Description:</td><td><input type="text" id="dc:description"><xsl:attribute name="value"><xsl:valueof select="dc:description"/></xsl:attribute></input></td></tr>
   <tr><td>Date:</td><td><xsl:valueof select="dc:date"/></td></tr>
   <tr><td>Rights:</td><td><input type="text" id="dc:rights"><xsl:attribute name="value"><xsl:valueof select="dc:rights"/></xsl:attribute></input></td></tr>
</xsl:template>

<xsl:template match="lenya-info:assets">
   <xsl:foreach select="lenya-info:asset>
      <tr><td><xsl:valueof select="."/></td><td><a href="">delete</a></td></tr>
   </xsl:foreach>
</xsl:template>

<xsl:template match="rc">
<tr><td>1.0</td><td>Felix Maeder</td><td>2003-04-01 18:01</td><td>view restore</td></tr>
</xsl:template>

<xsl:template match="wf">
<tr><td>Editing</td><td>Felix Maeder</td><td>2003-04-01 18:01</td></tr>
</xsl:template>

</xsl:stylesheet> 