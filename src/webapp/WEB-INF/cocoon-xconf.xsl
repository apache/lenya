<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: cocoon-xconf.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="xopus-context"/>
<xsl:param name="xopus-path"/>

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

<xsl:template match="input-modules">
     
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <component-instance name="defaults" class="org.apache.cocoon.components.modules.input.DefaultsMetaModule">
      <values>
        <skin>lenya-site</skin>
      </values>
    </component-instance>

    <component-instance name="xopus" class="org.apache.cocoon.components.modules.input.DefaultsMetaModule">
      <values>
        <context><xsl:value-of select="$xopus-context"/></context>
        <path><xsl:value-of select="$xopus-path"/></path>
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
        class="org.apache.lenya.cms.cocoon.components.modules.input.PublicationFallbackModule">
      <directory src="context:///lenya"/>
    </component-instance>

	<component-instance logger="core.modules.input.document-url" name="document-url"
        class="org.apache.lenya.cms.cocoon.components.modules.input.DocumentURLModule"/>
                       
    <component-instance logger="core.modules.input.resourceexists" name="resource-exists"
        class="org.apache.lenya.cms.cocoon.components.modules.input.ResourceExistsModule"/>
                
    <component-instance name="date-i18n" logger="core.modules.input" class="org.apache.cocoon.components.modules.input.DateInputModule">
      <format>yyyy-M-dd HH:mm:ss Z</format>
    </component-instance>
    
    <component-instance name="proxy-url" logger="sitemap.modules.input.proxy-url"
      class="org.apache.lenya.cms.cocoon.components.modules.input.ProxyUrlModule"/>

    <component-instance logger="core.modules.input.content-dir" name="content-dir"
      class="org.apache.lenya.cms.cocoon.components.modules.input.PublicationContentDirModule">
    </component-instance>
    
    <component-instance logger="core.modules.input.shibboleth" name="shib"
      class="org.apache.lenya.ac.shibboleth.ShibbolethModule"/>

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
      class="org.apache.lenya.ac.shibboleth.ShibbolethAuthenticator"
      role="org.apache.lenya.ac.Authenticator">
    <redirect-to-wayf>true</redirect-to-wayf>
  </component>
<!--
    <component logger="lenya.ac.authenticator"
      class="org.apache.lenya.ac.impl.UserAuthenticator"
      role="org.apache.lenya.ac.Authenticator"/>
-->    
    <xsl:comment>
Enable this authenticator and disable the UserAuthenticator for anonymous authentication (useful for client certs, for instance)

&lt;component logger="lenya.ac.authenticator"
      class="org.apache.lenya.ac.impl.AnonymousAuthenticator"
      role="org.apache.lenya.ac.Authenticator"/&gt;      
</xsl:comment>
 
 <component logger="lenya.ac.cache"
     	class="org.apache.lenya.ac.cache.SourceCacheImpl"
     	role="org.apache.lenya.ac.cache.SourceCache"/>
     	
  <component logger="lenya.uriparameterizer"
      class="org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizerImpl"
      role="org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer"/>
      
  <component logger="lenya.contextutil"
    role="org.apache.lenya.cms.cocoon.components.context.ContextUtility"
    class="org.apache.lenya.cms.cocoon.components.context.ContextUtility"/>
  
  <component logger="lenya.ac.attributeruleevaluator"
    role="org.apache.lenya.ac.AttributeRuleEvaluatorFactory"
    class="org.apache.lenya.ac.impl.antlr.AntlrEvaluatorFactory"/>
    
  <!-- Shibboleth -->
    
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.AssertionConsumerService"
    class="org.apache.shibboleth.impl.AssertionConsumerServiceImpl"/>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.AttributeRequestService"
    class="org.apache.shibboleth.impl.AttributeRequestServiceImpl"/>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.ShibbolethManager"
    class="org.apache.shibboleth.impl.ShibbolethManagerImpl"/>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.ShibbolethModule"
    class="org.apache.shibboleth.impl.ShibbolethModuleImpl">
    
    <WayfServer>https://localhost:8443/shibboleth-wayf/WAYF</WayfServer>
    
    <!-- Please fill-in your providerId. For Shibboleth 1.1, leave providerId empty. -->
    <ProviderId>http://sp.shibtest.org/shibboleth</ProviderId>
    <!-- 
      Location of your list of your supported Home Organisations. May be either an absolute file,
      a file relative to the webapp root or an URI to a file on a web server.
    -->
    <Metadata>context://WEB-INF/metadata.xml</Metadata>
    <!-- 
      Location of your Attribute Acceptance Policy (AAP). May be either an absolute file,
      a file relative to the webapp root or an URI to a file on a web server.
      Leave empty if you do not have any policy.
    -->
    <AAP>context://WEB-INF/AAP.xml</AAP>
    <!-- ReloadDelayMinutes sets the period of time to check for changes in the sites and AAP file.
      Set 0 to disable checks alltogether.
    -->
    <ReloadDelayMinutes>60</ReloadDelayMinutes>
    <!-- Wether to check if the certificate used to sign the AuthorizationStatement was -->
    <!-- issued by an recognised Certificate Authority as defined by the ssl-truststore parameter -->
    <!-- of opensaml.properties file. -->
    <CheckCertificateValidity>true</CheckCertificateValidity>
    <!-- Wether to check IssuerIP on assertions. This may cause problems with proxies that -->
    <!-- change the client's IP inbetween two subsequent requests. -->
    <CheckIssuerIP>true</CheckIssuerIP>
    
    <!-- 
      enable sending language with specified parameter name in the AAI get request.
      Used to display a localized AAI login page.
    -->
    <UseLanguageInRequest>false</UseLanguageInRequest>
    <LanguageParamName>YOUR_PARAM_NAME</LanguageParamName>
  </component>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.util.AttributeTranslator"
    class="org.apache.shibboleth.util.impl.AttributeTranslatorImpl">
    <!--
      Attributes to translate for easier reading/handling within Lenya.
      Attributes will be available by their translated name (outName) within Lenya.
    -->
    <Attribute inName="urn:mace:dir:attribute-def:eduPersonEntitlement" outName="eduPersonEntitlement" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonUniqueID" outName="swissEduPersonUniqueID" />
    <Attribute inName="urn:mace:dir:attribute-def:sn" outName="surname" />
    <Attribute inName="urn:mace:dir:attribute-def:givenName" outName="givenName" />
    <Attribute inName="urn:mace:dir:attribute-def:mail" outName="mail" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonHomeOrganization" outName="swissEduPersonHomeOrganization" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonHomeOrganizationType" outName="swissEduPersonHomeOrganizationType" />
    <Attribute inName="urn:mace:dir:attribute-def:eduPersonAffiliation" outName="eduPersonAffiliation" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonStudyBranch1" outName="swissEduPersonStudyBranch1" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonStudyBranch2" outName="swissEduPersonStudyBranch2" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonStudyBranch3" outName="swissEduPersonStudyBranch3" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonStudyLevel" outName="swissEduPersonStudyLevel" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:swissEduPersonStaffCategory" outName="swissEduPersonStaffCategory" />
    <Attribute inName="urn:mace:switch.ch:attribute-def:eduPersonOrgUnitDN" outName="eduPersonOrgUnitDN" />
    <Attribute inName="urn:mace:dir:attribute-def:eduPersonScopedAffiliation" outName="eduPersonScopedAffiliation" />
    <Attribute inName="urn:mace:dir:attribute-def:postalAddress" outName="postalAddress" />
    <Attribute inName="urn:mace:dir:attribute-def:swissEduPersonGender" outName="swissEduPersonGender" />
    <Attribute inName="urn:mace:dir:attribute-def:employeeNumber" outName="employeeNumber" />
    <Attribute inName="urn:mace:dir:attribute-def:ou" outName="organizationalUnit" />
    <Attribute inName="urn:mace:dir:attribute-def:eduPersonPrincipalName" outName="eduPersonPrincipalName"/>

    <!-- Mac OS X LDAP attributes -->
    <Attribute inName="apple-generateduid" outName="apple-generateduid"/>
    <Attribute inName="apple-user-authenticationhint" outName="apple-user-authenticationhint"/>
    <Attribute inName="apple-user-picture" outName="apple-user-picture"/>
    <Attribute inName="cn" outName="cn"/>
    <Attribute inName="gidNumber" outName="gidNumber"/>
    <Attribute inName="homeDirectory" outName="homeDirectory"/>
    <Attribute inName="loginShell" outName="loginShell"/>
    <Attribute inName="objectClass" outName="objectClass"/>
    <Attribute inName="uid" outName="uid"/>
    <Attribute inName="uidNumber" outName="uidNumber"/>
    <Attribute inName="userPassword" outName="userPassword"/>
  </component>
  <!--
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.util.CredentialsManager"
    class="org.apache.shibboleth.util.CredentialsManager">
    <KeyStore>
      <Location>file:///Users/nobby/src/shibboleth/keys/idp.example.org.jks</Location>
      <Type>JKS</Type>
      <StorePassword>f800sl</StorePassword>
      <KeyPassword>f800sl</KeyPassword>
    </KeyStore>
    <TrustStore>
      <Location>file:///Users/nobby/src/shibboleth/keys/idp.example.org.jks</Location>
      <Type>JKS</Type>
      <StorePassword>f800sl</StorePassword>
    </TrustStore>
  </component>
  -->
  
    <component logger="lenya.ac.shibboleth"
      role="org.apache.shibboleth.util.CredentialsManager"
      class="org.apache.shibboleth.util.impl.CredentialsManagerImpl">
      <KeyStore>
        <Location>file:///Users/nobby/src/shibtest/pki/idp.shibtest.org.jks</Location>
        <Type>JKS</Type>
        <StorePassword>shibtest</StorePassword>
        <KeyPassword>shibtest</KeyPassword>
      </KeyStore>
      <!--
        TrustStore is optional. If you do not want to verify Server Certs, remove the entire
        <TrustStore /> element.
      -->
      <TrustStore>
        <Location>file:///Users/nobby/src/shibtest/pki/idp.shibtest.org.jks</Location>
        <Type>JKS</Type>
        <StorePassword>shibtest</StorePassword>
      </TrustStore>
    </component>
        
  <!-- Defines the attribute which allows to uniquely identify an authenticated person -->
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.util.UniqueIdentifierMapper"
    class="org.apache.shibboleth.util.impl.UniqueIdentifierMapperImpl">
    <!-- The default unique identifier is mandatory -->
    <!--
      <Default uidAttribute="defaultUID" />
    <Default uidAttribute="urn:mace:dir:attribute-def:eduPersonAffiliation" />
    -->
    <Default uidAttribute="urn:mace:dir:attribute-def:eduPersonPrincipalName" />
    
    <!-- You may specify unique identifiers on a per-site basis -->
    <!-- OriginSite siteName="examplesite" uidAttribute="exampleuid" -->
  </component>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.util.UserFieldsMapping"
    class="org.apache.shibboleth.util.impl.UserFieldsMappingImpl">
    <!-- Shibboleth attributes to Lenya user profile mapping. -->
    <FirstName>urn:mace:dir:attribute-def:givenName</FirstName>
    <LastName>urn:mace:dir:attribute-def:sn</LastName>
    <EMail>urn:mace:dir:attribute-def:mail</EMail>
  </component>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.shibboleth.saml.ArtifactMapperImpl"
    class="org.apache.shibboleth.saml.ArtifactMapperImpl"/>
  
  <component logger="lenya.ac.shibboleth"
    role="org.apache.lenya.ac.AttributeDefinition"
    class="org.apache.lenya.ac.shibboleth.ShibbolethAttributeDefinition"/>
    
  </xsl:copy>
  
</xsl:template>


<!-- increase the free memory to prevent out of memory errors -->
<xsl:template match="store-janitor/parameter[@name = 'freememory']">
  <parameter name="freememory" value="10000000"/>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
