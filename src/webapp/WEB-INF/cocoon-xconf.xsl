<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : cocoon-xconf.xsl
    Created on : 6. März 2003, 11:39
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="forrest-publication"/>
<xsl:param name="hsqldb-server-port"/>

<xsl:template match="markup-languages/xsp-language/target-language[@name = 'java']">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <xsl:comment>Lenya Logicsheets</xsl:comment>
    
    <builtin-logicsheet>
      <parameter name="prefix" value="xsp-lenya"/>
      <parameter name="uri" value="http://apache.org/cocoon/lenya/xsp/1.0"/>
      <parameter name="href" value="resource://org/apache/lenya/cms/cocoon/logicsheets/util.xsl"/>
    </builtin-logicsheet>
    
    <builtin-logicsheet>
      <parameter name="prefix" value="xsp-scheduler"/>
      <parameter name="uri" value="http://apache.org/cocoon/lenya/xsp/scheduler/1.0"/>
      <parameter name="href" value="resource://org/apache/lenya/cms/cocoon/logicsheets/scheduler.xsl"/>
    </builtin-logicsheet>
    
    <xsl:comment>/ Lenya Logicsheets</xsl:comment>
    
  </xsl:copy>
</xsl:template>

<xsl:template match="input-modules">
    
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <component-instance name="defaults"       class="org.apache.cocoon.components.modules.input.DefaultsMetaModule">
      <values>
        <skin>lenya-site</skin>
      </values>
    </component-instance>

    <component-instance logger="core.modules.input" name="forrest" class="org.apache.cocoon.components.modules.input.ChainMetaModule">
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

    <component-instance logger="core.modules.input.access-control" name="access-control"
        class="org.apache.lenya.cms.cocoon.components.modules.input.AccessControlModule"/>
    
    <component-instance logger="core.modules.input.workflow" name="workflow"
        class="org.apache.lenya.cms.cocoon.components.modules.input.WorkflowModule"/>
        
    <component-instance logger="core.modules.input.sitetree" name="sitetree"
        class="org.apache.lenya.cms.cocoon.components.modules.input.SitetreeModule"/>
        
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
  <entity-resolver class="org.apache.cocoon.components.resolver.ResolverImpl" logger="core.resolver">
    <parameter name="catalog" value="{$forrest-publication}/resources/schema/catalog.xcat"/>
    <parameter name="verbosity" value="1"/>
  </entity-resolver>

  <xsl:apply-templates select="*[local-name() != 'entity-resolver']"/>

  <accreditable-managers>
    <component-instance logger="lenya.ac.accreditablemanager"
        class="org.apache.lenya.cms.ac2.file.FileAccreditableManager" name="file"/>
  </accreditable-managers>

  <authorizers>
    <component-instance class="org.apache.lenya.cms.ac2.PolicyAuthorizer" logger="lenya.ac.authorizer.policy" name="policy"/>
    <component-instance class="org.apache.lenya.cms.ac2.workflow.WorkflowAuthorizer" logger="lenya.ac.authorizer.workflow" name="workflow"/>
    <component-instance class="org.apache.lenya.cms.ac2.usecase.UsecaseAuthorizer" logger="lenya.ac.authorizer.usecase" name="usecase"/>
  </authorizers>
  
  <policy-managers>
    <component-instance class="org.apache.lenya.cms.ac2.file.FilePolicyManager" logger="lenya.ac.policymanager.file" name="file">
      <parameter name="directory" value="context:///lenya/config/ac/policies"/>
    </component-instance>
    <component-instance class="org.apache.lenya.cms.ac2.file.PublicationFilePolicyManager" logger="lenya.ac.policymanager.publication" name="publication-file">
      <parameter name="directory" value="context:///"/>
    </component-instance>
    <component-instance class="org.apache.lenya.cms.ac2.sitemap.SitemapPolicyManager" logger="lenya.ac.policymanager.sitemap" name="sitemap"/>
  </policy-managers>
  
  <component logger="lenya.ac.accesscontroller.global"
      class="org.apache.lenya.cms.ac2.BypassableAccessController"
      role="org.apache.lenya.cms.ac2.AccessController/global">
    <accreditable-manager type="file">
      <parameter name="directory" value="context:///lenya/config/ac/passwd"/>
    </accreditable-manager>
    <policy-manager type="file"/>
    <authorizer type="policy"/>
    <public>.*switch-user|.*logout|.*[.]css|.*[.]jpg|.*[.]gif</public>
  </component>
  
  <component logger="lenya.ac.accesscontroller.publicationfile"
      class="org.apache.lenya.cms.ac2.BypassableAccessController"
      role="org.apache.lenya.cms.ac2.AccessController/publication-file">
    <accreditable-manager type="file"/>
    <policy-manager type="publication-file"/>
    <authorizer type="policy"/>
    <authorizer type="workflow"/>
    <authorizer type="usecase"/>
    <public>.*switch-user|.*logout|.*[.]css|.*[.]jpg|.*[.]gif</public>
  </component>
  
  <component logger="lenya.ac.accesscontroller.sitemap"
      class="org.apache.lenya.cms.ac2.BypassableAccessController"
      role="org.apache.lenya.cms.ac2.AccessController/sitemap">
    <accreditable-manager type="file"/>
    <policy-manager type="sitemap"/>
    <authorizer type="policy"/>
    <authorizer type="workflow"/>
    <authorizer type="usecase"/>
    <public>.*switch-user|.*logout|.*[.]css|.*[.]jpg|.*[.]gif</public>
  </component>
  
  <access-controller-resolvers>
    <component-instance logger="lenya.ac.accesscontrollerresolver.publication"
        class="org.apache.lenya.cms.ac2.PublicationAccessControllerResolver"
        name="publication">
    </component-instance>
    <component-instance logger="lenya.ac.accesscontrollerresolver.global"
        class="org.apache.lenya.cms.ac2.ConfigurableAccessControllerResolver"
        name="global">
      <access-controller type="global"/>
    </component-instance>
    <component-instance logger="lenya.ac.accesscontrollerresolver.composable"
        class="org.apache.lenya.cms.ac2.ComposableAccessControllerResolver"
        name="composable">
      <resolver type="publication"/>
<!--      <resolver type="global"/>-->
    </component-instance>
  </access-controller-resolvers>
  
  <component logger="lenya.ac.authenticator"
      class="org.apache.lenya.cms.ac2.UserAuthenticator"
      role="org.apache.lenya.cms.ac2.Authenticator"/>
      
  <component logger="lenya.ac.cache"
     	class="org.apache.lenya.cms.ac2.cache.SourceCacheImpl"
     	role="org.apache.lenya.cms.ac2.cache.SourceCache"/>
     	
  <component logger="lenya.uriparameterizer"
      class="org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizerImpl"
      role="org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer"/>
      
  </xsl:copy>
</xsl:template>

<!-- increase the free memory to prevent out of memory errors -->
<xsl:template match="store-janitor/parameter[@name = 'freememory']">
  <parameter name="freememory" value="10000000"/>
</xsl:template>

<xsl:template match="dburl">
<dburl>jdbc:hsqldb:hsql://localhost:<xsl:value-of select="$hsqldb-server-port"/></dburl>
</xsl:template>

<xsl:template match="hsqldb-server/parameter[@name = 'port']">
  <parameter name="port" value="{$hsqldb-server-port}"/>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


   
</xsl:stylesheet> 
