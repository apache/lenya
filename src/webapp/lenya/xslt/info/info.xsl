<?xml version="1.0"?>

<!--
 $Id: info.xsl,v 1.7 2003/07/24 13:02:40 andreas Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    >
 <xsl:import href="../util/page-util.xsl"/>
 
 
<xsl:template match="lenya-info:info">    
    <html>
<head>
	<title></title>
	<script language="javascript" type="text/javascript" src="tabs.js"></script>
    <xsl:call-template name="include-css">
      <xsl:with-param name="context-prefix" select="publish/context"/>
    </xsl:call-template>
</head>
<body onload="Tab(1)">
	<!-- 
		These are the tabs. Make sure that each of them has the correct id,
		target and corresponding number in the Tab() call.
	 -->
	<a href="#" onclick="Tab(1)" id="link1" class="lenya-tablink">Overview</a>
	<a href="#" onclick="Tab(2)" id="link2" class="lenya-tablink">Meta</a>
	<a href="#" onclick="Tab(3)" id="link3" class="lenya-tablink">Assets</a>
	<a href="#" onclick="Tab(4)" id="link4" class="lenya-tablink">Workflow</a>
	<a href="#" onclick="Tab(5)" id="link5" class="lenya-tablink">Revisions</a>
	<a href="#" onclick="Tab(6)" id="link6" class="lenya-tablink">AC Auth.</a>
	<a href="#" onclick="Tab(7)" id="link7" class="lenya-tablink">AC Live</a>

	<!--  
		These are the different content blocks of the tabs, each one needs to
		have the id (starting with "contentblock" and followed by its number) and the 
		class "lenya-tab" to set the look and feel.
	-->
	<div id="contentblock1" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="lenya-info:overview"/>
          </table>
		</div>
	<div id="contentblock2" class="lenya-tab">
          <form>
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="lenya-info:meta"/>
          </table>
             <input type="submit" value="Update Metadata"/>
          </form>
		</div>
	<div id="contentblock3" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="lenya-info:assets"/>
          </table>
          <form>
             <input type="file" name="asset" size="40"/>
             <input type="submit" value="Upload Asset"/>
          </form>
	</div>
	<div id="contentblock4" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="wf:version"/>
          </table>
	</div>
	<div id="contentblock5" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="rc:revisions"/>
          </table>
	</div>
	<div id="contentblock6" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="lenya-info:permissions[@area='authoring']"/>
          </table>
	</div>
	<div id="contentblock7" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="lenya-info:permissions[@area='live']"/>
          </table>
	</div>
</body>
</html>
</xsl:template>

<xsl:template match="lenya-info:overview">
   <tr><td>Title:</td><td><xsl:value-of select="dc:title"/></td></tr>
   <tr><td>Abstract:</td><td><xsl:value-of select="lenya-info:abstract"/></td></tr>
   <tr><td>Status:</td><td><xsl:value-of select="lenya-info:status"/></td></tr>
   <tr><td>Language:</td><td><xsl:value-of select="dc:language"/></td></tr>
   <tr><td>Other Languages:</td><td></td></tr>
   <tr><td>Last edited by:</td><td><xsl:value-of select="lenya-info:lastmodifiedby"/></td></tr>
   <tr><td>Last modified:</td><td><xsl:value-of select="lenya-info:lastmodified"/></td></tr>
   <tr><td>Document ID:</td><td><xsl:value-of select="lenya-info:documentid"/></td></tr>
</xsl:template>

<xsl:template match="lenya-info:meta">
   <tr><td>Title:</td><td><input type="text" id="dc:title" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:title"/></xsl:attribute></input></td></tr>
   <tr><td>Creator:</td><td><input type="text" id="dc:creator" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:creator"/></xsl:attribute></input></td></tr>
   <tr><td>Subject:</td><td><input type="text" id="dc:subject" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:subject"/></xsl:attribute></input></td></tr>
   <tr><td>Description:</td><td><input type="text" id="dc:description" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:description"/></xsl:attribute></input></td></tr>
   <tr><td>Date:</td><td><xsl:value-of select="dc:date"/></td></tr>
   <tr><td>Rights:</td><td><input type="text" id="dc:rights" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:rights"/></xsl:attribute></input></td></tr>
</xsl:template>


<xsl:template match="lenya-info:assets">
 <!--   <xsl:for-each select="lenya-info:asset>-->
     <tr><td>asset.gif<xsl:value-of select="."/></td><td><a href="">delete</a></td></tr> 
    <!-- </xsl:for-each>-->
</xsl:template>


<xsl:template match="rc:revisions">
<tr><td>1.0</td><td>Felix Maeder</td><td>2003-04-01 18:01</td><td><a href="#">view</a></td><td><a href="#">restore</a></td></tr>
</xsl:template>


<xsl:template match="wf:version">
<tr><td><xsl:value-of select="@state"/></td><td><xsl:value-of select="@user"/></td><td><xsl:value-of select="@date"/></td></tr>
</xsl:template>


<xsl:template match="lenya-info:permissions">

  <tr>
    <td><input type="checkbox" name="ssl"/> SSL Encryption</td>
	</tr>
	<tr>
	<td>
	<table class="lenya-table">
		<tr>
			<th colspan="2">Access Object</th>
			<th colspan="2">Role</th>
		</tr>
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="type">user</xsl:with-param>
				<xsl:with-param name="title">User</xsl:with-param>
			</xsl:call-template>
		</tr>
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="type">group</xsl:with-param>
				<xsl:with-param name="title">Group</xsl:with-param>
			</xsl:call-template>
		</tr>
		<xsl:if test="@area = 'live'">
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="type">iprange</xsl:with-param>
				<xsl:with-param name="title">IP&#160;range</xsl:with-param>
			</xsl:call-template>
		</tr>
		</xsl:if>
	</table>
	</td>
	</tr>	
	
	
</xsl:template>


<xsl:template name="form-add-credential">
	<xsl:param name="type"/>
	<xsl:param name="title"/>
	<td><xsl:value-of select="$title"/>:</td>
	<td><xsl:apply-templates select="//lenya-info:items[@type = $type]"/></td>
	<td>
		<xsl:apply-templates select="//lenya-info:items[@type = 'role']">
			<xsl:with-param name="accreditable" select="$type"/>
		</xsl:apply-templates>
	</td>
	<td><input type="submit" name="add_credential_{$type}" value="Add"/></td>
</xsl:template>


<xsl:template match="lenya-info:items[@type='user' or @type='group' or @type='iprange']">
  <select name="{@type}">
    <xsl:apply-templates/>
  </select>
</xsl:template>


<xsl:template match="lenya-info:items[@type='role']">
	<xsl:param name="accreditable"/>
  <select name="role-{$accreditable}">
    <xsl:apply-templates/>
  </select>
</xsl:template>


	<xsl:template match="lenya-info:item">
  <option name="{@id}">
    <xsl:variable name="name" select="normalize-space(.)"/>
    <xsl:value-of select="@id"/>&#160;
    <xsl:if test="$name != ''"><em>(<xsl:value-of select="$name"/>)</em></xsl:if>
  </option>
</xsl:template>

</xsl:stylesheet> 