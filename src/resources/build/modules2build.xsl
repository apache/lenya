<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id: global-sitemap.xmap 393761 2006-04-13 08:38:00Z michi $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:mod="http://apache.org/lenya/module/1.0"
  xmlns:list="http://apache.org/lenya/module-list/1.0">
  
  <xsl:import href="util.xsl"/>
  
  <xsl:output indent="yes"/>
  
  <xsl:param name="cocoon-xconf"/>
  <xsl:param name="module-schema"/>
  <xsl:param name="copy-modules"/>
  
  <xsl:template match="list:modules">
    <project name="lenya-modules">
      
      <!-- Set up classpath -->
      <path id="classpath">
        <fileset>
          <xsl:attribute name="dir">${lib.dir}</xsl:attribute>
          <include name="*.jar"/>
        </fileset>
        <fileset>
          <xsl:attribute name="dir">${build.dir}/lib</xsl:attribute>
          <include name="*.jar"/>
        </fileset>
        <fileset>
          <xsl:attribute name="dir">${cocoon.webapp.dir}/WEB-INF/lib</xsl:attribute>
          <include name="*.jar"/>
        </fileset>
        <fileset dir="tools/jetty/lib">
          <include name="servlet-*.jar"/>
        </fileset>
      </path>
      
      <target name="compile-modules">
        <xsl:apply-templates select="list:module" mode="call"/>
      </target>
      
      <target name="test-modules">
        <xsl:apply-templates select="list:module" mode="call-test"/>
      </target>
      
      <xsl:apply-templates select="list:module" mode="target"/>
      
    </project>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="call">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call"/>
  </xsl:template>


  <xsl:template match="mod:module" mode="call">
    <antcall target="deploy-module-{mod:id}"/>
  </xsl:template>
  

  <xsl:template match="list:module" mode="call-test">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call-test"/>
  </xsl:template>
  
  
  <xsl:template match="mod:module" mode="call-test">
    <antcall target="test-module-{mod:id}"/>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="target">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="target">
      <xsl:with-param name="src" select="@src"/>
    </xsl:apply-templates>
  </xsl:template>
  
  
  <xsl:template match="mod:module" mode="target">
    <xsl:param name="src"/>
    <xsl:variable name="id" select="mod:id"/>

    <target name="validate-module-{$id}">
      <jing rngfile="{$module-schema}" file="{$src}/module.xml"/>
    </target>
    
    <target name="dependency-warning-{$id}">
      <xsl:if test="mod:published = 'false'">
        <property name="dependentModule" value=""/>
        <echo>
        WARNING: The module '${dependentModule}' depends on the module '<xsl:value-of select="$id"/>'.
        This module is not published, which means that it is not part of the API and can change without notice.
        </echo>
      </xsl:if>
    </target>
    
    <xsl:text>
      
    </xsl:text>
    <xsl:comment>Compile module <xsl:value-of select="$src"/> </xsl:comment>
    <xsl:text>
    </xsl:text>
    
    <xsl:variable name="srcDir"><xsl:value-of select="$src"/>/java/src</xsl:variable>
    <available file="{$srcDir}" property="compile.module.{$id}"/>

    <xsl:variable name="destDir">${build.dir}/modules/<xsl:value-of select="$id"/></xsl:variable>
    <xsl:variable name="debug">${debug}</xsl:variable>
    <xsl:variable name="optimize">${optimize}</xsl:variable>
    <xsl:variable name="deprecation">${deprecation}</xsl:variable>
    <xsl:variable name="target">${target.vm}</xsl:variable>
    <xsl:variable name="nowarn">${nowarn}</xsl:variable>
    
    <target name="compile-module-{$id}" if="compile.module.{$id}">
      
      <path id="module.classpath.{$id}">
        <path refid="classpath"/>
        <fileset includes="lenya-*-api.jar">
          <xsl:attribute name="dir">${build.webapp}/WEB-INF/lib</xsl:attribute>
        </fileset>
        <xsl:for-each select="mod:depends">
          <fileset includes="lenya-module-{@module}.jar">
            <xsl:attribute name="dir">${build.webapp}/WEB-INF/lib</xsl:attribute>
          </fileset>
        </xsl:for-each>
        <fileset dir="{$src}">
          <include name="java/lib/*.jar"/>
        </fileset>
        <fileset>
          <xsl:attribute name="dir">${lib.dir}</xsl:attribute>
          <include name="*.jar"/>
        </fileset>
      </path>
      
      <mkdir dir="{$destDir}"/>
      
      <javac
        destdir="{$destDir}"
        debug="{$debug}"
        optimize="{$optimize}"
        deprecation="{$deprecation}"
        target="{$target}"
        nowarn="{$nowarn}"
        source="1.4">
        <src path="{$srcDir}"/>
        <classpath refid="module.classpath.{$id}"/>
      </javac>
      
      <xsl:variable name="jarfile">${build.webapp}/WEB-INF/lib/lenya-module-<xsl:value-of select="$id"/>.jar</xsl:variable>
      
      <jar jarfile="{$jarfile}" index="true">
        <fileset dir="{$destDir}">
          <exclude name="**/Manifest.mf"/>
        </fileset>
      </jar>

    </target>
    
    <xsl:variable name="dirName">
      <xsl:call-template name="lastStep">
        <xsl:with-param name="path" select="$src"/>
      </xsl:call-template>
    </xsl:variable>
    
    <target name="copy-module-{$id}">
      <xsl:if test="$copy-modules = 'true'">
        <xsl:variable name="todir">${build.webapp}/lenya/modules/<xsl:value-of select="$dirName"/></xsl:variable>
        <copy 
          todir="{$todir}"
          flatten="false">
          <fileset dir="{$src}">
            <exclude name="java/**"/>
            <exclude name="*/java/**"/>
            <exclude name="config/cocoon-xconf/**"/>
            <exclude name="*/config/cocoon-xconf/**"/>
            <exclude name="config/lenya-roles/**"/>
            <exclude name="*/config/lenya-roles/**"/>
            <exclude name="config/sitemap/**"/>
            <exclude name="*/config/sitemap/**"/>
          </fileset>
        </copy>
      </xsl:if>
    </target>
    
    <target name="patch-module-{$id}">
      <xpatch file="{$cocoon-xconf}"
        srcdir="{$src}"
        includes="config/cocoon-xconf/*.xconf, config/cocoon-xconf/*/*.xconf"
        addComments="false"/>
        
      <xsl:variable name="lenya-roles">${build.dir}/impl/org/apache/lenya/lenya.roles</xsl:variable>
      <xpatch file="{$lenya-roles}"
        srcdir="{$src}"
        includes="config/lenya-roles/*.xroles"
        addComments="false"/>
      
      <xsl:variable name="sitemap-xmap">${build.webapp}/sitemap.xmap</xsl:variable>
      <xpatch file="{$sitemap-xmap}"
        srcdir="{$src}" 
        includes="config/sitemap/*.xmap"
        addComments="false"/>
      
    </target>
    
    <target name="dependency-warnings-{$id}">
      <xsl:apply-templates select="mod:depends" mode="dependencyWarning">
        <xsl:with-param name="id" select="$id"/>
      </xsl:apply-templates>
    </target>
    
    <xsl:variable name="dependencyList">
      <xsl:for-each select="mod:depends">
        <xsl:text>deploy-module-</xsl:text><xsl:value-of select="@module"/><xsl:text>, </xsl:text>
      </xsl:for-each>
    </xsl:variable>
    
    <target name="deploy-module-{$id}"
      depends="dependency-warnings-{$id}, {$dependencyList} validate-module-{$id}, compile-module-{$id}, copy-module-{$id}, patch-module-{$id}"/>
      
    <!-- ============================================================ -->
    <!-- Test -->
    <!-- ============================================================ -->
    
    <xsl:variable name="testSrcDir"><xsl:value-of select="$src"/>/java/test</xsl:variable>
    
    <available file="{$testSrcDir}" property="test.module.{$id}"/>
    
    <target name="test-module-{$id}" if="test.module.{$id}">
      
      <xsl:variable name="testDestDir">${build.dir}/modules/<xsl:value-of select="$id"/>-test</xsl:variable>
      
      <mkdir dir="{$testDestDir}"/>
      
      <xsl:variable name="build-webapp">${build.webapp}</xsl:variable>
      <xsl:variable name="build-test">${build.test}</xsl:variable>
      
      <path id="module.test-classpath.{$id}">
        <path refid="module.classpath.{$id}"/>
        <fileset dir="{$build-webapp}/WEB-INF/lib">
          <include name="lenya-module-{$id}.jar"/>
        </fileset>
        <path location="{$build-test}"/>
      </path>
      
      <javac
        destdir="{$testDestDir}"
        debug="{$debug}"
        optimize="{$optimize}"
        deprecation="{$deprecation}"
        target="{$target}"
        nowarn="{$nowarn}"
        source="1.4">
        <src path="{$testSrcDir}"/>
        <classpath refid="module.test-classpath.{$id}"/>
      </javac>
      
      <!-- Copy test resources -->
      <copy todir="{$testDestDir}" filtering="on">
        <fileset dir="{$testSrcDir}" excludes="**.java"/>
      </copy>
      
      <xsl:variable name="loglevel">${junit.test.loglevel}</xsl:variable>
      <xsl:variable name="junit-dir">${junit.dir}</xsl:variable>
      <xsl:variable name="repository-factory">${repository.factory}</xsl:variable>
      <xsl:variable name="contextRoot">${basedir}/build/lenya/webapp</xsl:variable>
      <xsl:variable name="tempDir">${basedir}/build/lenya/temp</xsl:variable>
      
      <junit printsummary="yes" showoutput="true" haltonerror="on" haltonfailure="on">
        <classpath>
          <fileset dir="{$build-webapp}/WEB-INF/lib" includes="*.jar, endorsed/*.jar"/>
          <path location="{$build-webapp}/WEB-INF/classes"/>
          <path location="{$build-test}"/>
          <path location="{$testDestDir}"/>
        </classpath>
        <formatter type="plain" usefile="false" />
        <formatter type="xml" />
        <sysproperty key="junit.test.loglevel" value="{$loglevel}"/>
        <sysproperty key="contextRoot" value="{$contextRoot}"/>
        <sysproperty key="tempDir" value="{$tempDir}"/>
        <sysproperty key="test.repo.webappDirectory" value="{$build-webapp}"/>
        <sysproperty key="test.repo.repositoryFactory" value="{$repository-factory}"/>
        <batchtest todir="{$junit-dir}">
          <fileset dir="{$testDestDir}" includes="**/*.class" excludes="**/Abstract*.class"/>
        </batchtest>
      </junit>
    </target>
    
  </xsl:template>
  
  <xsl:template match="mod:depends" mode="dependencyWarning">
    <xsl:param name="id"/>
    <antcall target="dependency-warning-{@module}">
      <param name="dependentModule" value="{$id}"/>
    </antcall>
  </xsl:template>
  
  
</xsl:stylesheet>