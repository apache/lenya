<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : cocoon-xconf.xsl
    Created on : 6. März 2003, 11:39
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">


<xsl:template match="markup-languages/xsp-language/target-language[@name = 'java']">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    
    <xsl:comment>Lenya Logicsheets</xsl:comment>
    
    <builtin-logicsheet>
      <parameter name="prefix" value="xsp-lenya"/>
      <parameter name="uri" value="http://www.lenya.org/xsp/lenya/1.0"/>
      <parameter name="href" value="resource://org/lenya/cms/cocoon/logicsheets/util.xsl"/>
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
        <skin>forrest-site</skin>
      </values>
    </component-instance>

    <component-instance logger="core.modules.input" name="forrest" class="org.apache.cocoon.components.modules.input.ChainMetaModule">
      <input-module name="request-param"/>
      <input-module name="request-attr"/>
      <input-module name="session-attr"/>
      <input-module name="defaults"/>
    </component-instance>
      
    <component-instance
      class="org.apache.cocoon.components.modules.input.RealPathModule"
      logger="core.modules.input" name="realpath"/>
      
  </xsl:copy>
</xsl:template>


<xsl:template match="cocoon">
  <xsl:copy>
  <xsl:copy-of select="@*"/>
  
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
    <parameter name="catalog" value="/lenya/pubs/forrest/resources/schema/catalog.xcat"/>
    <parameter name="local-catalog" value="D:\Development\src\xml-forrest\src\documentation\resources\schema\catalog.xcat"/>
    <parameter name="verbosity" value="1"/>
  </entity-resolver>

  <xsl:apply-templates select="*[local-name() != 'entity-resolver']"/>
  </xsl:copy>
</xsl:template>



<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


   
</xsl:stylesheet> 
