<?xml version="1.0" encoding="UTF-8"?>

<!--
 $Id: info.xsl,v 1.31 2003/08/29 12:24:54 egli Exp $
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

<xsl:param name="tab"/>

<xsl:template match="lenya-info:info">    
    <html>
<head>
	<title>info</title>
    <xsl:call-template name="include-css">
      <xsl:with-param name="context-prefix" select="publish/context"/>
    </xsl:call-template>
</head>
<body>
	<style>
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
	<!-- 
		These are the tabs. Make sure that each of them has the correct id,
		target and corresponding number in the Tab() call.
	 -->
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">overview</xsl:with-param></xsl:call-template>Overview</a>
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">meta</xsl:with-param></xsl:call-template>Meta</a>
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">assets</xsl:with-param></xsl:call-template>Assets</a>
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">workflow</xsl:with-param></xsl:call-template>Workflow</a>
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">revisions</xsl:with-param></xsl:call-template>Revisions</a>
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">ac-authoring</xsl:with-param></xsl:call-template>AC Auth</a>
	<a><xsl:call-template name="activate"><xsl:with-param name="currenttab">ac-live</xsl:with-param></xsl:call-template>AC Live</a>

	<!--  
		These are the different content blocks of the tabs, each one needs to
		have the id (starting with "contentblock" and followed by its number) and the 
		class "lenya-tab" to set the look and feel.
	-->
	<div id="contentblock1" class="lenya-tab">
		<xsl:choose>
			<xsl:when test="$tab = 'ac-authoring' or $tab = 'ac-live'">
               <xsl:apply-templates select="lenya-info:permissions"/>
			</xsl:when>
			<xsl:otherwise>
               <xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</div>
</body>
</html>
</xsl:template>

<xsl:template name="activate">
<xsl:param name="currenttab"/>
<xsl:variable name="urlhead">?lenya.usecase=info-</xsl:variable> 
<xsl:variable name="urltail">&amp;lenya.step=showscreen&amp;lenya.area=authoring</xsl:variable> 
<xsl:attribute name="href"><xsl:value-of select="$urlhead"/><xsl:value-of select="$currenttab"/><xsl:value-of select="$urltail"/></xsl:attribute>
<xsl:attribute name="class">lenya-tablink<xsl:if test="$currenttab = $tab">-active</xsl:if></xsl:attribute>
</xsl:template>

<xsl:template match="lenya-info:overview">
   <table class="lenya-table-noborder">
   <tr><td>Title:</td><td><xsl:value-of select="dc:title"/></td></tr>
   <tr><td>Abstract:</td><td><xsl:value-of select="lenya-info:abstract"/></td></tr>
   <tr><td>Status:</td><td><xsl:value-of select="lenya-info:status"/></td></tr>
   <tr><td>Current Language:</td><td><xsl:value-of select="dc:language"/></td></tr>
   <tr><td>Available Languages:</td><td><xsl:value-of select="lenya-info:languages"/></td></tr>
   <tr><td>Last edited by:</td><td><xsl:value-of select="lenya-info:lastmodifiedby"/></td></tr>
   <tr><td>Last modified:</td><td><xsl:value-of select="lenya-info:lastmodified"/></td></tr>
   <tr><td>Document ID:</td><td><xsl:value-of select="lenya-info:documentid"/></td></tr>
   </table>
</xsl:template>

  <xsl:template match="lenya-info:meta">
    <form>
      <table class="lenya-table">
        <tr>
          <th colspan="2">Meta Data</th>
        </tr>
        <tr><td>Title:</td><td><input type="text" id="dc:title" name="properties.save.meta.title" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:title"/></xsl:attribute></input></td></tr>
        <tr><td>Creator:</td><td><input type="hidden" id="dc:creator" name="properties.save.meta.creator" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:creator"/></xsl:attribute></input><xsl:value-of select="dc:creator"/></td></tr>
        <tr><td>Subject:</td><td><input type="text" id="dc:subject" name="properties.save.meta.subject" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:subject"/></xsl:attribute></input></td></tr>
        <tr><td>Description:</td><td><input type="text" id="dc:description" name="properties.save.meta.description" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:description"/></xsl:attribute></input></td></tr>
        <tr><td>Publisher:</td><td><input type="text" id="dc:publisher" name="properties.save.meta.publisher" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:publisher"/></xsl:attribute></input></td></tr>
        <tr><td>Date of creation:</td><td><xsl:value-of select="dc:date"/></td></tr>
        <tr><td>Rights:</td><td><input type="text" id="dc:rights" name="properties.save.meta.rights" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:rights"/></xsl:attribute></input></td></tr>
      </table>
      <input type="hidden" name="properties.save.meta.documentid" value="{lenya-info:documentid}"/>
      <input type="hidden" name="properties.save.meta.area" value="{lenya-info:area}"/>
      <input type="hidden" name="properties.save.meta.language" value="{dc:language}"/>
      <input type="hidden" name="lenya.usecase" value="info-meta"/>
      <input type="hidden" name="lenya.step" value="update"/>
      <input type="hidden" name="task-id" value="save-meta-data"/>
      <input type="submit" value="Update Metadata"/>
    </form>
  </xsl:template>

  <xsl:template match="lenya-info:assets">
    <table class="lenya-table">
      <tr>
        <th colspan="2">Assets</th>
      </tr>
      <xsl:for-each select="lenya-info:asset">
        <tr>
          <td><xsl:value-of select="."/></td>
          <td>
            <form>
              <input type="hidden" name="lenya.usecase" value="asset"/>
              <input type="hidden" name="lenya.step" value="remove"/>
              <input type="hidden" name="task-id" value="remove-asset"/>
              <input type="hidden" name="properties.remove.asset.document-id">
                <xsl:attribute name="value">
                  <xsl:value-of select="../lenya-info:documentid"/>
                </xsl:attribute>
              </input>
              <input type="hidden" name="properties.remove.asset.name">
                <xsl:attribute name="value">
                  <xsl:value-of select="."/>
                </xsl:attribute>
              </input>
              <input type="submit" value="Delete"/>
            </form>
          </td>
        </tr> 
      </xsl:for-each>
    </table>
    <form>
      <input type="hidden" name="lenya.usecase" value="asset"/>
      <input type="hidden" name="lenya.step" value="showscreen"/>
      <input type="hidden" name="insert" value="false"/>
      <input type="hidden" name="properties.asset.document-id">
        <xsl:attribute name="value">
          <xsl:value-of select="lenya-info:documentid"/>
        </xsl:attribute>
      </input>
      <!--       <input type="file" name="asset" size="40"/> -->
      <input type="submit" value="Upload New Asset"/>
    </form>
  </xsl:template>


  <xsl:template match="rc:revisions/XPSRevisionControl">
    <table class="lenya-table-noborder">
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
          
          <xsl:when test="position()>1">
            <xsl:variable name="timeIndex" select="position()"/>
            <tr>
              <td>
                <xsl:element name="a">
                  <xsl:attribute name="href">?lenya.usecase=rollback&amp;lenya.step=rollback&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/></xsl:attribute>Rollback to this version</xsl:element>
                
              </td>
              <td>
                <xsl:element name="a">
                  <xsl:attribute name="href">?lenya.usecase=rollback&amp;lenya.step=view&amp;rollbackTime=<xsl:value-of select="../CheckIn[$timeIndex]/Time"/></xsl:attribute><xsl:attribute name="target">_blank</xsl:attribute>View</xsl:element>
                
              </td>
              <xsl:apply-templates select="Time"/>
              <xsl:apply-templates select="Identity"/>
            </tr>
          </xsl:when>
        </xsl:choose>
        
      </xsl:for-each>
    </table>
  </xsl:template>

<xsl:template match="Time">
	<td align="right"><xsl:value-of select="@humanreadable"/></td>
</xsl:template>

<xsl:template match="Identity">
	<td><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="lenya-info:workflow">
	<xsl:copy-of select="node()"/>
</xsl:template>
	
	
<xsl:template match="lenya-info:permissions">
  <table class="lenya-table-noborder">
  <tr>
    <td>
    	<form method="get" name="form_ssl_{@area}">
				<input type="hidden" name="lenya.usecase" value="info-ac-{@area}"/>
				<input type="hidden" name="lenya.step" value="showscreen"/>
				<input type="hidden" name="change_ssl" value="true"/>
        <input type="checkbox" name="ssl"
        	onclick="document.forms.form_ssl_{@area}.submit()" value="true">
       		<xsl:if test="@ssl = 'true'">
	        	<xsl:attribute name="checked">checked</xsl:attribute>
       		</xsl:if>
        	SSL Encryption
        </input>
    	</form>
    </td>
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
    </table>
</xsl:template>


<xsl:template name="form-add-credential">
	<xsl:param name="area"/>
	<xsl:param name="type"/>
	<xsl:param name="title"/>
	<form method="get">
	<input type="hidden" name="lenya.usecase" value="info-ac-{$area}"/>
	<input type="hidden" name="lenya.step" value="showscreen"/>
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
				<input type="hidden" name="lenya.usecase" value="info-ac-{$area}"/>
				<input type="hidden" name="lenya.step" value="showscreen"/>
  			<input type="hidden" name="accreditable_id" value="{@accreditable-id}"/>
  			<input type="hidden" name="role_id" value="{@role-id}"/>
  			<input type="submit" name="delete_credential_{@accreditable-type}" value="Delete"/>
  		</form>
  		</xsl:if>
  	</td>
  </tr>
</xsl:template>

</xsl:stylesheet> 