<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="xopus-context"/>

<xsl:template match="markup-languages/xsp-language/target-language[@name = 'java']">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <xsl:comment>Lenya Logicsheets</xsl:comment>
        
    <builtin-logicsheet>
      <parameter name="prefix" value="xsp-scheduler"/>
      <parameter name="uri" value="http://apache.org/cocoon/lenya/xsp/scheduler/1.0"/>
      <parameter name="href" value="resource://org/apache/lenya/cms/cocoon/logicsheets/scheduler.xsl"/>
    </builtin-logicsheet>
    
    <xsl:comment>/ Lenya Logicsheets</xsl:comment>
    
  </xsl:copy>
</xsl:template>


<xsl:template match="source-factories">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <component-instance class="org.apache.lenya.cms.cocoon.source.FallbackSourceFactory" logger="lenya.source.fallback" name="fallback"/>
    <component-instance class="org.apache.lenya.cms.cocoon.source.LenyaSourceFactory" logger="lenya.source.lenya" name="lenya" scheme="context:"/>
  </xsl:copy>
</xsl:template>


<xsl:template match="input-modules">
     
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <component-instance name="defaults" class="org.apache.cocoon.components.modules.input.DefaultsModule">
      <values>
        <skin>lenya-site</skin>
      </values>
    </component-instance>

    <component-instance name="xopus" class="org.apache.cocoon.components.modules.input.DefaultsModule">
      <values>
        <context><xsl:value-of select="$xopus-context"/></context>
      </values>
    </component-instance>

    <component-instance logger="core.modules.input" name="forrest"
      class="org.apache.cocoon.components.modules.input.ChainMetaModule">
      <input-module name="request-param"/>
      <input-module name="request-attr"/>
      <input-module name="session-attr"/>
      <input-module name="defaults"/>
      <input-module name="page-envelope"/>
      <input-module name="access-control"/>
    </component-instance>
      
    <component-instance
      class="org.apache.cocoon.components.modules.input.RealPathModule"
      logger="core.modules.input" name="realpath"/>
      
    <component-instance logger="sitemap.modules.input.page-envelope" name="page-envelope"
        class="org.apache.lenya.cms.cocoon.components.modules.input.PageEnvelopeModule"/>

    <component-instance logger="sitemap.modules.input.dublincore" name="dublincore"
        class="org.apache.lenya.cms.cocoon.components.modules.input.DublinCoreModule"/>

    <component-instance logger="core.modules.input.access-control" name="access-control"
        class="org.apache.lenya.cms.cocoon.components.modules.input.AccessControlModule"/>
    
    <component-instance logger="core.modules.input.workflow" name="workflow"
        class="org.apache.lenya.cms.cocoon.components.modules.input.WorkflowModule"/>
        
    <component-instance logger="core.modules.input.fallback" name="fallback"
        class="org.apache.lenya.cms.cocoon.components.modules.input.PublicationTemplateFallbackModule">
      <directory src="context:///lenya"/>
    </component-instance>

    <component-instance logger="core.modules.input.usecase-fallback" name="usecase-fallback"
        class="org.apache.lenya.cms.cocoon.components.modules.input.UsecaseFallbackModule"/>

	<component-instance logger="core.modules.input.document-url" name="document-url"
        class="org.apache.lenya.cms.cocoon.components.modules.input.DocumentURLModule"/>
                       
    <component-instance logger="core.modules.input.resourceexists" name="resource-exists"
        class="org.apache.lenya.cms.cocoon.components.modules.input.ResourceExistsModule"/>
                
    <component-instance name="date-i18n" logger="core.modules.input" class="org.apache.cocoon.components.modules.input.DateInputModule">
      <format>yyyy-M-dd HH:mm:ss Z</format>
    </component-instance>
    
  <component-instance name="proxy-url" logger="sitemap.modules.input.proxy-url"
      class="org.apache.lenya.cms.cocoon.components.modules.input.ProxyUrlModule"/>

    <component-instance name="resource-type" logger="sitemap.modules.input.resource-type"
      class="org.apache.lenya.cms.cocoon.components.modules.input.ResourceTypeModule"/>
    
  </xsl:copy>
</xsl:template>

<xsl:template match="cocoon">
  <xsl:copy>
  <xsl:copy-of select="@*"/>
  <xsl:attribute name="user-roles">/WEB-INF/classes/org/apache/lenya/lenya.roles</xsl:attribute>
  
    <!--+
      | Entity resolution catalogs
      |
      | The default catalog is distributed at /WEB-INF/entities/catalog
      | This is the contextual pathname for Cocoon resources.
      | You can override this path, if necessary, using the "catalog" parameter:
      |
      |    <parameter name="catalog" value="/WEB-INF/entities/catalog"/>
      |
      | However, it is probably desirable to leave this default catalog config
      | and declare your own local catalogs, which are loaded in addition to
      | the system catalog.
      |
      | There are various ways to do local configuration (see "Entity Catalogs"
      | documentation). One way is via the CatalogManager.properties file.
      | As an additional method, you can specify the "local-catalog" parameter here.
      |
      | local-catalog:
      |   The full filesystem pathname to a single local catalog file.
      |
      |  <parameter name="local-catalog" value="/usr/local/sgml/mycatalog"/>
      |
      | verbosity:
      | The level of messages for status/debug (messages go to standard output)
      | The following messages are provided ...
      |  0 = none
      |  1 = ? (... not sure yet)
      |  2 = 1+, Loading catalog, Resolved public, Resolved system
      |  3 = 2+, Catalog does not exist, resolvePublic, resolveSystem
      |  10 = 3+, List all catalog entries when loading a catalog
      |    (Cocoon also logs the "Resolved public" messages.)
      |
      |     <parameter name="verbosity" value="2"/>
      +-->

  <xsl:apply-templates select="*"/>

  <accreditable-managers>
    <component-instance logger="lenya.ac.accreditablemanager"
        class="org.apache.lenya.ac.file.FileAccreditableManager" name="file"/>
  </accreditable-managers>

  <authorizers>
    <component-instance name="policy"
        class="org.apache.lenya.ac.impl.PolicyAuthorizer"
        logger="lenya.ac.authorizer.policy"/>
    <component-instance name="workflow"
        class="org.apache.lenya.cms.ac.workflow.WorkflowAuthorizer"
        logger="lenya.ac.authorizer.workflow"/>
    <component-instance name="usecase"
        class="org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer"
        logger="lenya.ac.authorizer.usecase"/>
  </authorizers>
  
  <policy-managers>
    <component-instance name="document"
        class="org.apache.lenya.cms.ac.DocumentPolicyManagerWrapper"
        logger="lenya.ac.policymanager.document"/>
    <component-instance name="file"
        class="org.apache.lenya.ac.file.FilePolicyManager"
        logger="lenya.ac.policymanager.file"/>
    <component-instance name="sitemap"
        class="org.apache.lenya.cms.ac.SitemapPolicyManager"
        logger="lenya.ac.policymanager.sitemap"/>
  </policy-managers>
  
  <component logger="lenya.ac.accesscontroller.bypassable"
      class="org.apache.lenya.ac.impl.BypassableAccessController"
      role="org.apache.lenya.ac.AccessController/bypassable">
    <public>.*[.]css|.*[.]jpg|.*[.]gif|.*[.]png|.*[.]rng|.*[.]xsl</public>
  </component>
  
  <access-controller-resolvers>
    <component-instance name="publication"
        class="org.apache.lenya.cms.ac.PublicationAccessControllerResolver"
        logger="lenya.ac.accesscontrollerresolver.publication">
    </component-instance>
    <component-instance name="global"
        class="org.apache.lenya.ac.impl.ConfigurableAccessControllerResolver"
        logger="lenya.ac.accesscontrollerresolver.global">
      <access-controller type="global"/>
    </component-instance>
    <component-instance name="composable"
        class="org.apache.lenya.ac.impl.ComposableAccessControllerResolver"
        logger="lenya.ac.accesscontrollerresolver.composable">
      <resolver type="publication"/>
<!--      <resolver type="global"/>-->
    </component-instance>
  </access-controller-resolvers>
  
  <component logger="lenya.ac.authenticator"
      class="org.apache.lenya.ac.impl.UserAuthenticator"
      role="org.apache.lenya.ac.Authenticator"/>
      
<xsl:comment>
Enable this authenticator and disable the UserAuthenticator for anonymous authentication (useful for client certs, for instance)

&lt;component logger="lenya.ac.authenticator"
      class="org.apache.lenya.ac.impl.AnonymousAuthenticator"
      role="org.apache.lenya.ac.Authenticator"/&gt;      
</xsl:comment>
 
  <component logger="lenya.ac.cache"
     	class="org.apache.lenya.ac.cache.SourceCacheImpl"
     	role="org.apache.lenya.ac.cache.SourceCache"/>
     	
  <component logger="lenya.publication.templatemanager"
      class="org.apache.lenya.cms.publication.templating.PublicationTemplateManagerImpl"
      role="org.apache.lenya.cms.publication.templating.PublicationTemplateManager"/>

  <usecases>

     <!--+
         | AC Usecases
	 +-->

    <component-instance name="ac.login" logger="lenya.ac" class="org.apache.lenya.cms.ac.usecases.Login">
      <view template="usecases/ac/login.jx"/>
    </component-instance>

    <component-instance name="ac.logout" logger="lenya.ac" class="org.apache.lenya.cms.ac.usecases.Logout">
      <view template="usecases/ac/logout.jx"/>
    </component-instance>
    
    <!--+
        | Admin area usecases
        +-->

    <component-instance name="admin.users" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.Users">
      <view template="usecases/admin/users.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.users"/>
    </component-instance>
    <component-instance name="admin.user" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.User">
      <view template="usecases/admin/user.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.users"/>
    </component-instance>
    <component-instance name="admin.addUser" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.AddUser">
      <view template="usecases/admin/addUser.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.user"/>
    </component-instance>
    <component-instance name="admin.userProfile" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.UserProfile">
      <view template="usecases/admin/userProfile.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.user"/>
    </component-instance>
    <component-instance name="admin.changePassword" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.UserPassword">
      <view template="usecases/admin/changePassword.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.user"/>
    </component-instance>
    <component-instance name="admin.userGroups" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.UserGroups">
      <view template="usecases/admin/userGroups.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.user"/>
    </component-instance>
    <component-instance name="admin.deleteUser" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.DeleteUser">
      <view template="usecases/admin/deleteUser.jx" menu="true">
        <tab group="admin" name="users"/>
      </view>
      <exit usecase="admin.users"/>
    </component-instance>
    <component-instance name="admin.groups" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.Groups">
      <view template="usecases/admin/groups.jx" menu="true">
        <tab group="admin" name="groups"/>
      </view>
      <exit usecase="admin.groups"/>
    </component-instance>
    <component-instance name="admin.addGroup" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.AddGroup">
      <view template="usecases/admin/addGroup.jx" menu="true">
        <tab group="admin" name="groups"/>
      </view>
      <exit usecase="admin.group"/>
    </component-instance>
    <component-instance name="admin.group" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.Group">
      <view template="usecases/admin/group.jx" menu="true">
        <tab group="admin" name="groups"/>
      </view>
      <exit usecase="admin.groups"/>
    </component-instance>
    <component-instance name="admin.groupProfile" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.GroupProfile">
      <view template="usecases/admin/groupProfile.jx" menu="true">
        <tab group="admin" name="groups"/>
      </view>
      <exit usecase="admin.group"/>
    </component-instance>
    <component-instance name="admin.groupMembers" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.GroupMembers">
      <view template="usecases/admin/groupMembers.jx" menu="true">
        <tab group="admin" name="groups"/>
      </view>
      <exit usecase="admin.group"/>
    </component-instance>
    <component-instance name="admin.deleteGroup" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.DeleteGroup">
      <view template="usecases/admin/deleteGroup.jx" menu="true">
        <tab group="admin" name="groups"/>
      </view>
      <exit usecase="admin.groups"/>
    </component-instance>
    <component-instance name="admin.ipRanges" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.IPRanges">
      <view template="usecases/admin/ipRanges.jx" menu="true">
        <tab group="admin" name="ipRanges"/>
      </view>
      <exit usecase="admin.ipRanges"/>
    </component-instance>
    <component-instance name="admin.addIPRange" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.AddIPRange">
      <view template="usecases/admin/addIPRange.jx" menu="true">
        <tab group="admin" name="ipRanges"/>
      </view>
      <exit usecase="admin.ipRanges"/>
    </component-instance>
    <component-instance name="admin.ipRange" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.IPRange">
      <view template="usecases/admin/ipRange.jx" menu="true">
        <tab group="admin" name="ipRanges"/>
      </view>
      <exit usecase="admin.ipRanges"/>
    </component-instance>
    <component-instance name="admin.ipRangeProfile" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.IPRangeProfile">
      <view template="usecases/admin/ipRangeProfile.jx" menu="true">
        <tab group="admin" name="ipRanges"/>
      </view>
      <exit usecase="admin.ipRange"/>
    </component-instance>
    <component-instance name="admin.ipRangeGroups" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.IPRangeGroups">
      <view template="usecases/admin/ipRangeGroups.jx" menu="true">
        <tab group="admin" name="ipRanges"/>
      </view>
      <exit usecase="admin.ipRange"/>
    </component-instance>
    <component-instance name="admin.deleteIPRange" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.DeleteIPRange">
      <view template="usecases/admin/deleteIPRange.jx" menu="true">
        <tab group="admin" name="ipRanges"/>
      </view>
      <exit usecase="admin.ipRanges"/>
    </component-instance>
    <component-instance name="admin.emptyTrash" logger="lenya.admin" class="org.apache.lenya.cms.site.usecases.EmptyTrash">
      <view template="usecases/admin/emptyTrash.jx" menu="true">
        <tab group="admin" name="trash"/>
      </view>
      <exit usecase="admin.emptyTrash"/>
    </component-instance>
    <component-instance name="admin.serverStatus" logger="lenya.admin" class="org.apache.lenya.cms.usecase.DummyUsecase">
      <view template="usecases/admin/serverStatus.jx" menu="true">
        <tab group="admin" name="serverStatus"/>
      </view>
      <exit usecase="admin.serverStatus"/>
    </component-instance>
    <component-instance name="admin.sessions" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.SessionViewer">
      <view template="usecases/admin/sessions.jx" menu="true">
        <tab group="admin" name="sessions"/>
      </view>
      <exit usecase="admin.sessions"/>
    </component-instance>
    <component-instance name="admin.siteOverview" logger="lenya.admin" class="org.apache.lenya.cms.site.usecases.SiteOverview">
      <view template="usecases/admin/siteOverview.jx" menu="true">
        <tab group="admin" name="siteOverview"/>
      </view>
      <exit usecase="admin.siteOverview"/>
    </component-instance>
    
    <!--+
        | Site area usecases
        +-->

    <component-instance name="site.create" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.CreateDocument">
      <view template="usecases/site/create.jx"/>
    </component-instance>
    <component-instance name="site.createLanguage" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.CreateLanguage">
      <view template="usecases/site/createLanguage.jx"/>
    </component-instance>
    <component-instance name="site.delete" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Delete">
      <view template="usecases/site/delete.jx"/>
    </component-instance>
    <component-instance name="site.deleteLanguage" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.DeleteLanguage">
      <view template="usecases/site/deleteLanguage.jx"/>
    </component-instance>
    <component-instance name="site.changeLabel" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.ChangeLabel">
      <view template="usecases/site/changeLabel.jx"/>
    </component-instance>
    <component-instance name="site.changeNodeID" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.ChangeNodeID">
      <view template="usecases/site/changeNodeID.jx"/>
    </component-instance>
    <component-instance name="site.cut" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Cut">
      <view template="usecases/site/cut.jx"/>
    </component-instance>
    <component-instance name="site.copy" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Copy">
      <view template="usecases/site/copy.jx"/>
    </component-instance>
    <component-instance name="site.paste" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Paste">
      <view template="usecases/site/paste.jx"/>
    </component-instance>
    <component-instance name="site.archive" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Archive">
      <view template="usecases/site/archive.jx"/>
    </component-instance>
    <component-instance name="site.restore" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Restore">
      <view template="usecases/site/restore.jx"/>
    </component-instance>
    
    <component-instance name="tab.acArchive" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl">
      <view template="usecases/tab/acArchive.jx" menu="true">
        <tab group="site" name="acArchive"/>
      </view>
      <exit usecase="tab.acArchive"/>
    </component-instance>
    <component-instance name="tab.acAuthoring" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl">
      <view template="usecases/tab/acAuthoring.jx" menu="true">
        <tab group="site" name="acAuthoring"/>
      </view>
      <parameter name="acArea" value="authoring"/>
      <exit usecase="tab.acAuthoring"/>
    </component-instance>
    <component-instance name="tab.acLive" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl">
      <view template="usecases/tab/acLive.jx" menu="true">
        <tab group="site" name="acLive"/>
      </view>
      <parameter name="acArea" value="live"/>
      <exit usecase="tab.acLive"/>
    </component-instance>
    <component-instance name="tab.acTrash" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl">
      <view template="usecases/tab/acTrash.jx" menu="true">
        <tab group="site" name="acTrash"/>
      </view>
      <exit usecase="tab.acTrash"/>
    </component-instance>
    <component-instance name="tab.assets" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Assets">
      <view template="usecases/tab/assets.jx" menu="true">
        <tab group="site" name="assets"/>
      </view>
      <exit usecase="tab.assets"/>
    </component-instance>
    <component-instance name="tab.meta" logger="lenya.site" class="org.apache.lenya.cms.metadata.usecases.Metadata">
      <view template="usecases/tab/meta.jx" menu="true">
        <tab group="site" name="meta"/>
      </view>
      <exit usecase="tab.meta"/>
    </component-instance>
    <component-instance name="tab.overview" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Overview">
      <view template="usecases/tab/overview.jx" menu="true">
        <tab group="site" name="overview"/>
      </view>
      <exit usecase="tab.overview"/>
    </component-instance>
    <component-instance name="tab.revisions" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Revisions">
      <view template="usecases/tab/revisions.jx" menu="true">
        <tab group="site" name="revisions"/>
      </view>
      <exit usecase="tab.revisions"/>
    </component-instance>
    <component-instance name="tab.workflow" logger="lenya.site" class="org.apache.lenya.cms.workflow.usecases.History">
      <view template="usecases/tab/workflow.jx" menu="true">
        <tab group="site" name="workflow"/>
      </view>
      <exit usecase="tab.workflow"/>
    </component-instance>
    <component-instance name="tab.scheduler" logger="lenya.site" class="org.apache.lenya.cms.usecase.scheduling.ManageJobs">
      <view template="usecases/tab/scheduler.jx" menu="true">
        <tab group="site" name="scheduler"/>
      </view>
      <exit usecase="tab.scheduler"/>
    </component-instance>
    
    <component-instance name="publication.createPublicationFromTemplate" logger="lenya.publication"
                        class="org.apache.lenya.cms.publication.usecases.CreatePublicationFromTemplate">
      <view template="usecases/publication/createPublicationFromTemplate.jx"/>
    </component-instance>

    <component-instance name="publication.edit" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.EditDocument">
      <parameter name="sourceUri" value="cocoon:/request2document"/>
    </component-instance>
    <component-instance name="edit.insertImage" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.InsertAsset">
      <view template="usecases/edit/insertAsset.jx" menu="false">
        <parameter name="callbackFunction" value="insertImage"/>
      </view>
      <parameter name="mimeTypePrefix" value="image/"/>
      <parameter name="asset-usecase" value="tab.assets"/>
    </component-instance>
    <component-instance name="edit.insertAsset" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.InsertAsset">
      <view template="usecases/edit/insertAsset.jx" menu="false">
        <parameter name="callbackFunction" value="insertAsset"/>
      </view>
      <parameter name="asset-usecase" value="tab.assets"/>
    </component-instance>
    <component-instance name="edit.forms" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.forms.FormsEditor">
      <transaction policy="pessimistic"/>
      <view template="usecases/edit/forms/forms.jx" menu="false"/>
    </component-instance>
    <component-instance name="edit.oneform" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.forms.OneFormEditor">
      <transaction policy="pessimistic"/>
      <view template="usecases/edit/forms/oneform.jx" menu="false"/>
    </component-instance>

    <component-instance name="edit.bxe" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.bxe.BXE">
      <transaction policy="pessimistic"/>
      <view template="usecases/edit/bxe/bxe.jx" menu="false"/>
    </component-instance>

    <component-instance name="edit.kupu" logger="lenya.publication"
                        class="org.apache.lenya.cms.editors.kupu.Kupu">
      <transaction policy="pessimistic"/>
      <view template="usecases/edit/kupu/kupu.jx" menu="false"/>
    </component-instance>
    
    <component-instance name="rollback" logger="lenya.publication"
                        class="org.apache.lenya.cms.site.usecases.Rollback">
      <transaction policy="pessimistic"/>
    </component-instance>
  </usecases>
  
  <resource-types/>

  <component role="org.apache.cocoon.components.cron.CronJob/usecase"
             class="org.apache.lenya.cms.usecase.scheduling.UsecaseCronJob"
             logger="cron.usecase"/>
             
  <site-managers>
    <component-instance name="simple" logger="lenya.site"
                        class="org.apache.lenya.cms.site.simple.SimpleSiteManager"/>
  </site-managers>
  
  <document-builders>
    <component-instance name="default" logger="lenya.publication"
                        class="org.apache.lenya.cms.publication.DefaultDocumentBuilder"/>
  </document-builders>
  
  <template-instantiators/>
  
  <gui-manager>
    <tab-group name="admin">
      <tab name="users" label="Users" usecase="admin.users"/>
      <tab name="groups" label="Groups" usecase="admin.groups"/>
      <tab name="ipRanges" label="IP Ranges" usecase="admin.ipRanges"/>
      <tab name="trash" label="Delete Trash" usecase="admin.emptyTrash"/>
      <tab name="serverStatus" label="Server Status" usecase="admin.serverStatus"/>
      <tab name="sessions" label="Sessions" usecase="admin.sessions"/>
      <tab name="siteOverview" label="Overview" usecase="admin.siteOverview"/>
    </tab-group>
    <tab-group name="site">
      <tab name="overview" label="Overview" usecase="tab.overview"/>
      <tab name="meta" label="Meta" usecase="tab.meta"/>
      <tab name="assets" label="Assets" usecase="tab.assets"/>
      <tab name="workflow" label="Workflow" usecase="tab.workflow"/>
      <tab name="revisions" label="Revisions" usecase="tab.revisions"/>
      <tab name="acAuthoring" label="AC Auth" usecase="tab.acAuthoring">
        <parameter name="acArea" value="authoring"/>
      </tab>
      <tab name="acTrash" label="AC Trash" usecase="tab.acTrash">
        <parameter name="acArea" value="trash"/>
      </tab>
      <tab name="acArchive" label="AC Archive" usecase="tab.acArchive">
        <parameter name="acArea" value="archive"/>
      </tab>
      <tab name="acLive" label="AC Live" usecase="tab.acLive"/>
      <tab name="scheduler" label="Scheduler" usecase="tab.scheduler"/>
    </tab-group>
  </gui-manager>
  
  <!-- move scheduler to the end, datasources have to be available -->
  <xsl:apply-templates select="component[@role = 'org.apache.cocoon.components.cron.JobScheduler']" mode="scheduler"/>
             
  </xsl:copy>

</xsl:template>

<!-- increase the free memory to prevent out of memory errors -->
<xsl:template match="store-janitor/parameter[@name = 'freememory']">
  <parameter name="freememory" value="10000000"/>
</xsl:template>


<xsl:template match="component[@role = 'org.apache.cocoon.components.cron.JobScheduler']"/>


<xsl:template match="component[@role = 'org.apache.cocoon.components.cron.JobScheduler']" mode="scheduler">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


<!-- JDBC store for scheduler -->
<xsl:template match="component[@role = 'org.apache.cocoon.components.cron.JobScheduler']/store">
  <store type="tx" delegate="org.quartz.impl.jdbcjobstore.StdJDBCDelegate">
    <datasource provider="excalibur">LenyaScheduler</datasource>
  </store>
</xsl:template>


<xsl:template match="datasources">
  <xsl:copy>
    <jdbc logger="core.datasources.lenya.scheduler" name="LenyaScheduler">
      <pool-controller max="10" min="5">
        <!-- use custom keep-alive query because HSQL does not accept 'SELECT 1' -->
        <keep-alive>SELECT 1 FROM QRTZ_LOCKS</keep-alive>
      </pool-controller>
      <dburl>jdbc:hsqldb:hsql://localhost:9002</dburl>
      <user>sa</user>
      <password/>
    </jdbc>
  </xsl:copy>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
