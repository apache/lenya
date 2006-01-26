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
    <component-instance class="org.apache.lenya.cms.cocoon.source.ZipSourceFactory" logger="lenya.source.zip" name="zip"/>
    <component-instance class="org.apache.lenya.cms.cocoon.source.FallbackSourceFactory" logger="lenya.source.fallback" name="fallback"/>
    <component-instance class="org.apache.lenya.cms.cocoon.source.TemplateFallbackSourceFactory" logger="lenya.source.templatefallback" name="template-fallback"/>
    <component-instance class="org.apache.lenya.cms.cocoon.source.LenyaSourceFactory" logger="lenya.source.lenya" name="lenya" scheme="context:"/>
    <component-instance class="org.apache.lenya.cms.cocoon.source.ContentSourceFactory" logger="lenya.source.content" name="content"/>
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

    <component-instance logger="sitemap.modules.input.custom-metadata" name="custom-metadata"
        class="org.apache.lenya.cms.cocoon.components.modules.input.CustomMetaDataModule"/>

    <component-instance logger="sitemap.modules.input.doc-info" name="doc-info"
        class="org.apache.lenya.cms.cocoon.components.modules.input.DocumentInfoModule"/>

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
      <format>yyyy-MM-dd HH:mm:ss Z</format>
    </component-instance>
    
    <component-instance name="date-iso8601-rfc822" logger="core.modules.input" class="org.apache.lenya.cms.cocoon.components.modules.input.DateConverterModule">
      <src-pattern>yyyy-MM-dd HH:mm:ss Z</src-pattern>
      <pattern>EEE, dd MMM yyyy HH:mm:ss Z</pattern>
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

  <repository-factory>
    <repository-factory class="org.apache.lenya.cms.jcr.jackrabbit.JackrabbitRepositoryFactory"/>
  </repository-factory>
  
  <resource-types/>
  <usecases/>

  <component role="org.apache.cocoon.components.cron.CronJob/usecase"
             class="org.apache.lenya.cms.usecase.scheduling.UsecaseCronJob"
             logger="cron.usecase"/>

  <component role="org.apache.lenya.cms.cocoon.components.context.ContextUtility"
             logger="lenya.cocoon.components"
             class="org.apache.lenya.cms.cocoon.components.context.ContextUtility"/>
             
  <site-managers>
    <component-instance name="simple" logger="lenya.site"
                        class="org.apache.lenya.cms.site.simple.SimpleSiteManager"/>
  </site-managers>
  
  <document-builders>
    <component-instance name="default" logger="lenya.publication"
                        class="org.apache.lenya.cms.publication.DefaultDocumentBuilder"/>
  </document-builders>
  
  <template-instantiators/>
  <gui-manager/>
  
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
