<?xml version="1.0"?>

<!--
 $Id: info.xsl,v 1.10 2003/07/29 16:28:02 andreas Exp $
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
<!--	<a href="#" onclick="Tab(3)" id="link3" class="lenya-tablink">Assets</a>
	<a href="#" onclick="Tab(4)" id="link4" class="lenya-tablink">Workflow</a> -->
	<a href="#" onclick="Tab(3)" id="link3" class="lenya-tablink">Revisions</a>
	<a href="#" onclick="Tab(4)" id="link4" class="lenya-tablink">AC Auth.</a>
	<a href="#" onclick="Tab(5)" id="link5" class="lenya-tablink">AC Live</a>

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
<!--	<div id="contentblock3" class="lenya-tab">
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
	</div> -->
	<div id="contentblock3" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="rc:revisions/XPSRevisionControl"/>
          </table>
	</div>
	<div id="contentblock4" class="lenya-tab">
          <table class="lenya-table-noborder">
              <xsl:apply-templates select="lenya-info:permissions[@area='authoring']"/>
          </table>
	</div>
	<div id="contentblock5" class="lenya-tab">
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


<xsl:template match="rc:revisions/XPSRevisionControl">
		<xsl:for-each select="CheckIn">
			
			<xsl:choose>
				
				<xsl:when test="position()=1">
					<tr>
						<td>Current version</td>
						<td>&#160;</td>
						<xsl:apply-templates select="Time"/>
						<xsl:apply-templates select="Identity"/>
					</tr>
				</xsl:when>

				<!-- Note, important: The timestamp we're inserting into the anchor
				     in each row is actually the one from the *previous* version, thus the
					 position()-1 calculation. This is because in order to roll back
					 to a given version, we need to reactivate the backup file which was
					 written *before* that version was checked in.
				 --> 

				<xsl:when test="position()>1">
					<xsl:variable name="timeIndex" select="position() - 1"/>
					<tr>
						<td>
							<xsl:element name="a">
							<xsl:attribute name="href">?lenya.usecase=&amp;lenya.step=rollback&amp;documentid=&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/></xsl:attribute>Rollback to this version</xsl:element>

						</td>
						<td>
							<xsl:element name="a">
							<xsl:attribute name="href">?lenya.usecase=&amp;lenya.step=view&amp;documentid=&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/></xsl:attribute><xsl:attribute name="target">_blank</xsl:attribute>View</xsl:element>

						</td>
						<xsl:apply-templates select="Time"/>
						<xsl:apply-templates select="Identity"/>
					</tr>
				</xsl:when>
			</xsl:choose>
		
		</xsl:for-each>
</xsl:template>

<xsl:template match="Time">
	<td align="right"><xsl:value-of select="@humanreadable"/></td>
</xsl:template>

<xsl:template match="Identity">
	<td><xsl:apply-templates/></td>
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
			<th colspan="2"><xsl:if test="@area = 'authoring'">Role</xsl:if>&#160;</th>
		</tr>
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="area" select="@area"/>
				<xsl:with-param name="type">user</xsl:with-param>
				<xsl:with-param name="title">User</xsl:with-param>
			</xsl:call-template>
		</tr>
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="area" select="@area"/>
				<xsl:with-param name="type">group</xsl:with-param>
				<xsl:with-param name="title">Group</xsl:with-param>
			</xsl:call-template>
		</tr>
		<xsl:if test="@area = 'live'">
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="area" select="@area"/>
				<xsl:with-param name="type">iprange</xsl:with-param>
				<xsl:with-param name="title">IP&#160;range</xsl:with-param>
			</xsl:call-template>
		</tr>
		</xsl:if>
		
		<xsl:apply-templates select="lenya-info:credential">
      <xsl:with-param name="area" select="@area"/>
		</xsl:apply-templates>
	</table>
	
	</td>
	</tr>	
	
	
</xsl:template>


<xsl:template name="form-add-credential">
	<xsl:param name="area"/>
	<xsl:param name="type"/>
	<xsl:param name="title"/>
	<form method="get">
	<input type="hidden" name="lenya.usecase" value="info"/>
	<input type="hidden" name="lenya.step" value="showscreen"/>
	<input type="hidden" name="area" value="{$area}"/>
	<td><xsl:value-of select="$title"/>:</td>
	<td><xsl:apply-templates select="//lenya-info:items[@type = $type]"/></td>
	<td>
		<xsl:choose>
			<xsl:when test="$area = 'authoring'">
				<xsl:apply-templates select="//lenya-info:items[@type = 'role']"/>
			</xsl:when>
			<xsl:otherwise>
				<input type="hidden" name="role_id" value="visitor"/>
			</xsl:otherwise>
		</xsl:choose>
	</td>
	<td>
		<input type="submit" name="add_credential_{$type}" value="Add">
			<xsl:if test="not(//lenya-info:items[@type = $type]/lenya-info:item)">
				<xsl:attribute name="disabled">disabled</xsl:attribute>
			</xsl:if>
		</input>
	</td>
	</form>
</xsl:template>


<xsl:template match="lenya-info:items[@type='user' or @type='group' or @type='iprange']">
  <select name="accreditable_id" class="lenya-form-element">
    <xsl:apply-templates/>
  </select>
</xsl:template>


<xsl:template match="lenya-info:items[@type='role']">
  <select name="role_id">
    <xsl:apply-templates/>
  </select>
</xsl:template>


<xsl:template match="lenya-info:item">
  <option value="{@id}">
    <xsl:variable name="name" select="normalize-space(.)"/>
    <xsl:value-of select="@id"/>&#160;
    <xsl:if test="$name != ''"><em>(<xsl:value-of select="$name"/>)</em></xsl:if>
  </option>
</xsl:template>


<xsl:template match="lenya-info:credential">
	<xsl:param name="area"/>
	<xsl:variable name="color">
		<xsl:choose>
			<xsl:when test="@type = 'parent'">#666666;</xsl:when>
			<xsl:otherwise>#000000;</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
  <tr>
  	<td/>
  	<td>
  		<span style="color: {normalize-space($color)}">
  		<xsl:value-of select="@accreditable-id"/>
  	  <xsl:if test="@accreditable-name != ''">
  		  (<xsl:value-of select="@accreditable-name"/>)
  	  </xsl:if>
  	  </span>
  	</td>
  	<td>
  		<xsl:if test="$area = 'authoring'">
  		<span style="color: {$color}">
  		<xsl:value-of select="@role-id"/>
  	  <xsl:if test="@role-name != ''">
  		  (<xsl:value-of select="@role-name"/>)
  	  </xsl:if>
  	  </span>
  	  </xsl:if>
  	</td>
  	<td>
  		<xsl:if test="not(@type = 'parent')">
  		<form>
				<input type="hidden" name="lenya.usecase" value="info"/>
				<input type="hidden" name="lenya.step" value="showscreen"/>
				<input type="hidden" name="area" value="{$area}"/>
  			<input type="hidden" name="accreditable_id" value="{@accreditable-id}"/>
  			<input type="hidden" name="role_id" value="{@role-id}"/>
  			<input type="submit" name="delete_credential_{@accreditable-type}" value="Delete"/>
  		</form>
  		</xsl:if>
  	</td>
  </tr>
</xsl:template>

</xsl:stylesheet> 