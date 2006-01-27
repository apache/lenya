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


<xsl:template match="input-modules">
     
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>

    <component-instance name="xopus" class="org.apache.cocoon.components.modules.input.DefaultsModule">
      <values>
        <context><xsl:value-of select="$xopus-context"/></context>
      </values>
    </component-instance>
    
  </xsl:copy>
</xsl:template>


<xsl:template match="cocoon">
  <xsl:copy>
  <xsl:copy-of select="@*"/>
  
  <xsl:apply-templates select="*"/>

 
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
    <template-instantiators/>
    <gui-manager/>
    
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
