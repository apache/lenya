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
        
    <component-instance logger="core.modules.input.sitetree" name="sitetree"
        class="org.apache.lenya.cms.cocoon.components.modules.input.SitetreeModule"/>
        
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
      
  <component logger="lenya.ac.cache"
     	class="org.apache.lenya.ac.cache.SourceCacheImpl"
     	role="org.apache.lenya.ac.cache.SourceCache"/>
     	
  <component logger="lenya.uriparameterizer"
      class="org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizerImpl"
      role="org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer"/>
      
  <component logger="lenya.publication.templatemanager"
      class="org.apache.lenya.cms.publication.templating.PublicationTemplateManagerImpl"
      role="org.apache.lenya.cms.publication.templating.PublicationTemplateManager"/>

  <component logger="lenya.usecase.unitofwork"
      class="org.apache.lenya.cms.usecase.UnitOfWork"
      role="org.apache.lenya.cms.usecase.UnitOfWorkImpl"/>
      
  <usecases>
    <component-instance name="ac.login" logger="lenya.ac" class="org.apache.lenya.cms.ac.usecases.Login"/>
    <component-instance name="ac.logout" logger="lenya.ac" class="org.apache.lenya.cms.ac.usecases.Logout"/>
    
    <component-instance name="admin.addUser" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.AddUser"/>
    <component-instance name="admin.userProfile" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.UserProfile"/>
    <component-instance name="admin.changePassword" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.UserPassword"/>
    <component-instance name="admin.userGroups" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.UserGroups"/>
    <component-instance name="admin.deleteUser" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.DeleteUser"/>
    <component-instance name="admin.addGroup" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.AddGroup"/>
    <component-instance name="admin.groupProfile" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.GroupProfile"/>
    <component-instance name="admin.groupMembers" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.GroupMembers"/>
    <component-instance name="admin.deleteGroup" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.DeleteGroup"/>
    <!--
    <component-instance name="admin.addIPRange" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.AddIPRange"/>
    <component-instance name="admin.ipRangeProfileProfile" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.IPRangeProfile"/>
    <component-instance name="admin.deleteIPRange" logger="lenya.admin" class="org.apache.lenya.cms.ac.usecases.DeleteIPRange"/>
    -->

    <component-instance name="search.search" logger="lenya.search" class="org.apache.lenya.cms.search.usecases.Search"/>
    
    <component-instance name="site.create" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.CreateDocument"/>
    <component-instance name="site.createLanguage" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.CreateLanguage"/>
    <component-instance name="site.delete" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Delete"/>
    <component-instance name="site.deleteLanguage" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.DeleteLanguage"/>
    <component-instance name="site.changeLabel" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.ChangeLabel"/>
    <component-instance name="site.changeNodeID" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.ChangeNodeID"/>
    <component-instance name="site.cut" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Cut"/>
    <component-instance name="site.copy" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Copy"/>
    <component-instance name="site.paste" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Paste"/>
    <component-instance name="site.nudge" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Nudge"/>
    
    <component-instance name="tab.acArchive" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl"/>
    <component-instance name="tab.acAuthoring" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl"/>
    <component-instance name="tab.acLive" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl"/>
    <component-instance name="tab.acTrash" logger="lenya.site" class="org.apache.lenya.cms.ac.usecases.AccessControl"/>
    <component-instance name="tab.assets" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Assets"/>
    <component-instance name="tab.meta" logger="lenya.site" class="org.apache.lenya.cms.metadata.usecases.Metadata"/>
    <component-instance name="tab.overview" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Overview"/>
    <component-instance name="tab.revisions" logger="lenya.site" class="org.apache.lenya.cms.site.usecases.Revisions"/>
    <component-instance name="tab.workflow" logger="lenya.site" class="org.apache.lenya.cms.workflow.usecases.History"/>
  </usecases>

  <component role="org.apache.cocoon.components.cron.CronJob/usecase"
             class="org.apache.lenya.cms.usecase.scheduling.UsecaseCronJob"
             logger="cron.usecase"/>
             
  </xsl:copy>

</xsl:template>

<!-- increase the free memory to prevent out of memory errors -->
<xsl:template match="store-janitor/parameter[@name = 'freememory']">
  <parameter name="freememory" value="10000000"/>
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
