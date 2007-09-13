<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: info.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
    xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
    xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    >

<xsl:import href="../util/page-util.xsl"/>

<xsl:param name="tab"/>
<xsl:param name="area"/>
<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="documentid"/>
<xsl:param name="languageexists"/>


<xsl:template match="lenya-info:info">
<div>
	<!-- 
		These are the tabs. Make sure that each of them has the correct id,
		target and corresponding number in the Tab() call.
	 -->
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">overview</xsl:with-param></xsl:call-template><i18n:text>Overview</i18n:text></a></td>
			<xsl:if test="$languageexists = 'true'">
				<xsl:if test="$documentid != '/'">
					<td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">meta</xsl:with-param></xsl:call-template><i18n:text>Meta</i18n:text></a></td>
					<td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">assets</xsl:with-param></xsl:call-template><i18n:text>Assets</i18n:text></a></td>
					<td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">workflow</xsl:with-param></xsl:call-template><i18n:text>Workflow</i18n:text></a></td>
					<td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">revisions</xsl:with-param></xsl:call-template><i18n:text>Versions</i18n:text></a></td>
				</xsl:if>
				<xsl:choose>
  				<xsl:when test="$area = 'authoring'">
            <td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">ac-authoring</xsl:with-param></xsl:call-template><i18n:text>AC Auth</i18n:text></a></td>
            <td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">ac-live</xsl:with-param></xsl:call-template><i18n:text>AC Live</i18n:text></a></td>
				  </xsl:when>
  				<xsl:when test="$area = 'archive'">
            <td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">ac-archive</xsl:with-param></xsl:call-template><i18n:text>AC Archive</i18n:text></a></td>
				  </xsl:when>
  				<xsl:when test="$area = 'trash'">
            <td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">ac-trash</xsl:with-param></xsl:call-template><i18n:text>AC Trash</i18n:text></a></td>
				  </xsl:when>
        </xsl:choose>
				<td><a><xsl:call-template name="activate"><xsl:with-param name="currenttab">scheduler</xsl:with-param></xsl:call-template><i18n:text>Scheduler</i18n:text></a></td>
			</xsl:if>
		</tr>
	</table>
	

	<!--  
		These are the different content blocks of the tabs, each one needs to
		have the id (starting with "contentblock" and followed by its number) and the 
		class "lenya-tab" to set the look and feel.
	-->
	<div id="contentblock1" class="lenya-tab">
		<xsl:choose>
			<xsl:when test="starts-with($tab, 'ac-')">
        <xsl:apply-templates select="lenya-info:permissions"/>
			</xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
		</xsl:choose>
	</div>
	
</div>
</xsl:template>

<xsl:template name="activate">
<xsl:param name="currenttab"/>
<xsl:variable name="urlhead">?lenya.usecase=info-</xsl:variable> 
<xsl:variable name="urltail">&amp;lenya.step=showscreen</xsl:variable> 
<xsl:attribute name="href"><xsl:value-of select="$urlhead"/><xsl:value-of select="$currenttab"/><xsl:value-of select="$urltail"/></xsl:attribute>
<xsl:attribute name="class">lenya-tablink<xsl:if test="$currenttab = $tab">-active</xsl:if></xsl:attribute>
</xsl:template>

<xsl:template match="lenya-info:overview">
	<xsl:choose>
	<xsl:when test="dc:title">
   <table class="lenya-table-noborder">
   <tr><td class="lenya-entry-caption"><i18n:text>Title</i18n:text>:</td><td><xsl:value-of select="dc:title"/></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Description</i18n:text>:</td><td><xsl:value-of select="lenya-info:abstract"/></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Workflow State</i18n:text>:</td><td><i18n:text><xsl:apply-templates select="lenya-info:workflow-state"/></i18n:text></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Live</i18n:text>:</td><td><i18n:text><xsl:apply-templates select="lenya-info:is-live"/></i18n:text></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Available Languages</i18n:text>:</td><td><xsl:apply-templates select="lenya-info:languages"/></td></tr>
   <!-- <tr><td>Last edited by:</td><td><xsl:value-of select="lenya-info:lastmodifiedby"/></td></tr> -->
   <tr><td class="lenya-entry-caption"><i18n:text>Last modified</i18n:text>:</td><td><xsl:value-of select="lenya-info:lastmodified"/></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Document ID</i18n:text>:</td><td><xsl:value-of select="lenya-info:documentid"/></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Resource Type</i18n:text>:</td><td><xsl:value-of select="lenya-info:resource-type"/></td></tr>
   <tr><td class="lenya-entry-caption"><i18n:text>Visibility in navigation</i18n:text>:</td><td><i18n:text><xsl:value-of select="lenya-info:visibleinnav"/></i18n:text></td></tr>
   <xsl:if test="lenya-info:href != ''"> 
     <tr><td class="lenya-entry-caption"><i18n:text>Navigation Link</i18n:text>:</td><td><i18n:text><xsl:value-of select="lenya-info:href"/></i18n:text></td></tr>
   </xsl:if>

   <xsl:if test="$area='trash'">
     <tr><td class="lenya-entry-caption"><i18n:text>Original Path</i18n:text>:</td><td><xsl:value-of select="lenya-info:identifier"/></td></tr>
   </xsl:if>

   </table>
  </xsl:when>
  <xsl:when test="$languageexists = 'false'">
  	<i18n:text>This document is not available in this language.</i18n:text><br/><br/>
  	<i18n:text>Available Languages</i18n:text>: <xsl:apply-templates select="lenya-info:languages"/>
  </xsl:when>
  <xsl:otherwise><i18n:text>No overview available.</i18n:text></xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match="lenya-info:workflow-state">
	<xsl:call-template name="overview-workflow"/>
</xsl:template>


<xsl:template match="lenya-info:is-live">
	<xsl:call-template name="overview-workflow"/>
</xsl:template>


<xsl:template name="overview-workflow">
	<xsl:choose>
		<xsl:when test="normalize-space(.) != ''"><xsl:value-of select="normalize-space(.)"/></xsl:when>
		<xsl:otherwise><span style="color: #999999;">[no workflow]</span></xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template match="lenya-info:overview/lenya-info:languages">
	<xsl:apply-templates select="lenya-info:language"/>
</xsl:template>

<xsl:template match="lenya-info:language">
	<xsl:if test="position() > 1"><xsl:text>, </xsl:text></xsl:if>
	<xsl:choose>
		<xsl:when test="normalize-space(../../dc:language) = normalize-space(.)"><xsl:value-of select="normalize-space(.)"/></xsl:when>
		<xsl:otherwise><a href="{@href}?lenya.usecase=info-overview&amp;lenya.step=showscreen"><xsl:value-of select="normalize-space(.)"/></a></xsl:otherwise>
	</xsl:choose>
</xsl:template>


  <xsl:template match="lenya-info:meta">
    <form name="meta-form">
      <table class="lenya-table-noborder">
	<xsl:if test="lenya-info:exception-user != ''">
	  <tr><td colspan="2" class="lenya-form-message-error">Could not update the meta data as the document has been checked out by <xsl:value-of select="lenya-info:exception-user"/> since <xsl:value-of select="lenya-info:exception-date"/>.</td></tr>
	</xsl:if>
        <tr><td class="lenya-entry-caption"><i18n:text>Title</i18n:text>:</td><td><input type="text" id="dc:title" name="properties.save.meta.title" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:title"/></xsl:attribute></input></td></tr>
        <tr><td class="lenya-entry-caption"><i18n:text>Subject</i18n:text>:</td><td><input type="text" id="dc:subject" name="properties.save.meta.subject" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:subject"/></xsl:attribute></input></td></tr>
        <tr><td class="lenya-entry-caption"><i18n:text>Description</i18n:text>:</td><td><textarea id="dc:description" name="properties.save.meta.description" rows="3" class="lenya-form-element"><xsl:value-of select="dc:description"/></textarea></td></tr>
        <tr><td class="lenya-entry-caption"><i18n:text>Publisher</i18n:text>:</td><td><input type="text" id="dc:publisher" name="properties.save.meta.publisher" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:publisher"/></xsl:attribute></input></td></tr>
        <tr><td class="lenya-entry-caption"><i18n:text>Rights</i18n:text>:</td><td><input type="text" id="dc:rights" name="properties.save.meta.rights" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:rights"/></xsl:attribute></input></td></tr>
        <tr><td class="lenya-entry-caption"><i18n:text>Creation Date</i18n:text>:</td><td><xsl:value-of select="dc:date"/></td></tr>
        <tr><td class="lenya-entry-caption"><i18n:text>Creator</i18n:text>:</td><td><input type="hidden" id="dc:creator" name="properties.save.meta.creator" class="lenya-form-element"><xsl:attribute name="value"><xsl:value-of select="dc:creator"/></xsl:attribute></input><xsl:value-of select="dc:creator"/></td></tr>
        <tr><td/><td><br/>
              <xsl:choose>
                <xsl:when test="$area = 'authoring'"><input i18n:attr="value" type="submit" value="Save" name="Save"/></xsl:when>
                <xsl:otherwise><input i18n:attr="value" type="submit" disabled="disabled" value="Save" name="Save"/></xsl:otherwise>
              </xsl:choose>              
        </td></tr>
      </table>
      <input type="hidden" name="properties.save.meta.documentid" value="{lenya-info:documentid}"/>
      <input type="hidden" name="properties.save.meta.area" value="{lenya-info:area}"/>
      <input type="hidden" name="properties.save.meta.language" value="{dc:language}"/>
      <input type="hidden" name="lenya.usecase" value="info-meta"/>
      <input type="hidden" name="lenya.step" value="update"/>
      <input type="hidden" name="lenya.event" value="edit"/>
      <input type="hidden" name="task-id" value="save-meta-data"/>
    </form>
  </xsl:template>

  <xsl:template match="lenya-info:assets">
    <table class="lenya-table">
      <tr>
        <th><i18n:text>Assets</i18n:text></th>
        <th><i18n:text>Preview</i18n:text></th>
        <th><i18n:text>Title</i18n:text></th>
        <th><i18n:text>File Size</i18n:text></th>
        <th><i18n:text>Dimension w x h</i18n:text></th>
        <th><i18n:text>Creation Date</i18n:text></th>
        <th></th>
      </tr>
      <xsl:for-each select="lenya-info:asset">
        <xsl:sort select="dc:title"/>
        <tr>
          <td><xsl:value-of select="dc:source"/></td>
          <td>        
            <xsl:if test="dc:format = 'image/jpeg' or dc:format = 'image/gif' or  dc:format = 'image/png'">
                <img src="{$contextprefix}/{$publicationid}/authoring/{$documentid}/{dc:source}" style="height: 32px; vertical-align: middle;"/>&#160;
            </xsl:if>
           </td>
          <td><xsl:value-of select="dc:title"/></td>
          <td align="right"><xsl:value-of select="dc:extent"/> kB</td>
          <td align="right">
            <xsl:if test="lenya:imagewidth != ''">
              <xsl:value-of select="lenya:imagewidth"/> x <xsl:value-of select="lenya:imageheight"/> 
	        </xsl:if>
          </td>
          <td align="right"><xsl:value-of select="dc:date"/></td>
          <td>
            <form name="asset-form">
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
                  <xsl:value-of select="dc:source"/>
                </xsl:attribute>
              </input>
              <xsl:choose>
                <xsl:when test="$area = 'authoring'"><input i18n:attr="value" type="submit" value="Delete" name="Delete"/></xsl:when>
                <xsl:otherwise><input i18n:attr="value" type="submit" disabled="disabled" value="Delete" name="Delete"/></xsl:otherwise>
              </xsl:choose>              
            </form>
          </td>
        </tr> 
      </xsl:for-each>
    </table>
    <form name="new-asset-form">
      <input type="hidden" name="lenya.usecase" value="asset"/>
      <input type="hidden" name="lenya.step" value="showscreen"/>
      <input type="hidden" name="insert" value="false"/>
      <input type="hidden" name="properties.asset.document-id">
        <xsl:attribute name="value">
          <xsl:value-of select="lenya-info:documentid"/>
        </xsl:attribute>
      </input>
      <xsl:choose>
         <xsl:when test="$area = 'authoring'"><input i18n:attr="value" type="submit" value="New Asset" name="NewAsset" /></xsl:when>
         <xsl:otherwise><input i18n:attr="value" type="submit" disabled="disabled" value="New Asset" name="NewAsset"/></xsl:otherwise>
       </xsl:choose>              
    </form>
  </xsl:template>


  <xsl:template match="rc:revisions/XPSRevisionControl">
    <table class="lenya-table-noborder">
      <xsl:for-each select="CheckIn">
        
        <xsl:choose>
          
          <xsl:when test="position()=1">
            <tr>
              <td><i18n:text>Current Version</i18n:text></td>
              <td>&#160;</td>
              <xsl:apply-templates select="Time"/>
              <xsl:apply-templates select="Identity"/>
            </tr>
          </xsl:when>
          
          <xsl:when test="position()>1">
              <xsl:apply-templates select="Backup"/>
          </xsl:when>
        </xsl:choose>
        
      </xsl:for-each>
    </table>
  </xsl:template>

<xsl:template match="Backup">
            <tr>
              <td>
                <xsl:element name="a">
                  <xsl:if test="$area = 'authoring'"><xsl:attribute name="href">?lenya.usecase=rollback&amp;lenya.step=rollback&amp;rollbackTime=<xsl:value-of select="../Time"/></xsl:attribute></xsl:if>
                  <i18n:text>Rollback to this version</i18n:text>
                </xsl:element>
              </td>
              <td>
                <xsl:element name="a">
                 <xsl:attribute name="href">?lenya.usecase=rollback&amp;lenya.step=view&amp;rollbackTime=<xsl:value-of select="../Time"/></xsl:attribute><xsl:attribute name="target">_blank</xsl:attribute>
                 <i18n:text>View</i18n:text></xsl:element>
              </td>
              <xsl:apply-templates select="../Time"/>
              <xsl:apply-templates select="../Identity"/>
            </tr>
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
        <input type="checkbox" name="ssl" onclick="document.forms.form_ssl_{@area}.submit()" value="true">
           <xsl:if test="@ssl = 'true' or @ancestor-ssl = 'true'">
            <xsl:attribute name="checked">checked</xsl:attribute>
            </xsl:if>
            <xsl:if test="@ancestor-ssl = 'true'">
              <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
          <i18n:text>SSL Encryption</i18n:text>
        </input>
      </form>
    </td>
	</tr>
	<tr>
	<td>
	<table class="lenya-table">
		<tr>
			<th colspan="2"><i18n:text>Access Object</i18n:text></th>
			<th colspan="2"><xsl:if test="@area = 'authoring'"><i18n:text>Role</i18n:text></xsl:if>&#160;</th>
		</tr>
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="larea" select="@area"/>
				<xsl:with-param name="type">user</xsl:with-param>
				<xsl:with-param name="title">User</xsl:with-param>
			</xsl:call-template>
		</tr>
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="larea" select="@area"/>
				<xsl:with-param name="type">group</xsl:with-param>
				<xsl:with-param name="title">Group</xsl:with-param>
			</xsl:call-template>
		</tr>
		<xsl:if test="@area = 'live'">
		<tr>
			<xsl:call-template name="form-add-credential">
				<xsl:with-param name="larea" select="@area"/>
				<xsl:with-param name="type">iprange</xsl:with-param>
				<xsl:with-param name="title">IP Range</xsl:with-param>
			</xsl:call-template>
		</tr>
		</xsl:if>
		
		<xsl:apply-templates select="lenya-info:credential">
          <xsl:with-param name="larea" select="@area"/>
		</xsl:apply-templates>
	</table>
	
	</td>
	</tr>	
    </table>
</xsl:template>


<xsl:template name="form-add-credential">
	<xsl:param name="larea"/>
	<xsl:param name="type"/>
	<xsl:param name="title"/>
	<xsl:variable name="visitor-role" select="//lenya-info:visitor-role"/>
	<xsl:choose>
  	<xsl:when test="$visitor-role">
    	<form method="get" name="add-credential-form">
    	<input type="hidden" name="lenya.usecase" value="info-ac-{$larea}"/>
    	<input type="hidden" name="lenya.step" value="showscreen"/>
    	<td><i18n:text><xsl:value-of select="$title"/></i18n:text>:</td>
    	<td><xsl:apply-templates select="//lenya-info:items[@type = $type]"/></td>
    	<td>
    		<xsl:choose>
    			<xsl:when test="$larea != 'live'">
    				<xsl:apply-templates select="//lenya-info:items[@type = 'role']"/>
    			</xsl:when>
    			<xsl:otherwise>
    				<input type="hidden" name="role_id" value="{//lenya-info:visitor-role}"/>
    			</xsl:otherwise>
    		</xsl:choose>
    	</td>
    	<td>
    		<input i18n:attr="value" type="submit" name="add_credential_{$type}" value="Add">
    			<xsl:if test="not(//lenya-info:items[@type = $type]/lenya-info:item) or ($area = 'live')">
    				<xsl:attribute name="disabled">disabled</xsl:attribute>
    			</xsl:if>
    		</input>
    	</td>
    	</form>
  	</xsl:when>
  	<xsl:otherwise>
  	  <i18n:text>No visitor role found which can be assigned in the live area</i18n:text>.
  	</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template match="lenya-info:items[@type='user' or @type='group' or @type='iprange']">
  <select name="accreditable_id" style="width: 200px;">
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
    <i18n:text><xsl:value-of select="@id"/></i18n:text>&#160;
    <xsl:if test="$name != ''"><em>(<xsl:value-of select="$name"/>)</em></xsl:if>
  </option>
</xsl:template>


<xsl:template match="lenya-info:credential">
	<xsl:param name="larea"/>
	<xsl:variable name="color">
		<xsl:choose>
			<xsl:when test="@type = 'parent'">#666666;</xsl:when>
			<xsl:otherwise>#000000;</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
  <tr>
  	<td>
  	  <xsl:if test="@type = 'parent' and not(preceding-sibling::lenya-info:credential[@type = 'parent'])"><i18n:text>Inherited Rights</i18n:text>:</xsl:if>
  	</td>
  	<td>
      <span style="color: {normalize-space($color)}">
        <i18n:text><xsl:value-of select="@accreditable-id"/></i18n:text>
  	    <xsl:if test="@accreditable-name != ''">
  		  (<i18n:text><xsl:value-of select="@accreditable-name"/></i18n:text>)
  	    </xsl:if>
      </span>
  	</td>
  	<td>
      <xsl:if test="$larea != 'live'">
        <span style="color: {$color}"><i18n:text><xsl:value-of select="@role-id"/></i18n:text>
          <xsl:if test="@role-name != ''">(<xsl:value-of select="@role-name"/>)</xsl:if>
        </span>
      </xsl:if>
  	</td>
  	<td>
  		<xsl:if test="not(@type = 'parent')">
  		<form name="info-credential-form">
				<input type="hidden" name="lenya.usecase" value="info-ac-{$larea}"/>
				<input type="hidden" name="lenya.step" value="showscreen"/>
  			<input type="hidden" name="accreditable_id" value="{@accreditable-id}"/>
  			<input type="hidden" name="role_id" value="{@role-id}"/>
  			<input i18n:attr="value" type="submit" name="delete_credential_{@accreditable-type}" value="Delete">
    			<xsl:if test="$area = 'live'">
    				<xsl:attribute name="disabled">disabled</xsl:attribute>
    			</xsl:if>
    		</input>
  		</form>
  		</xsl:if>
  	</td>
  </tr>
</xsl:template>

<xsl:template match="lenya-info:scheduler">
	<xsl:copy-of select="node()"/>
</xsl:template>

</xsl:stylesheet> 
