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

<!-- $Id: global-sitemap.xmap 393761 2006-04-13 08:38:00Z michi $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:mod="http://apache.org/lenya/module/1.0"
  xmlns:list="http://apache.org/lenya/module-list/1.0">
  
  <xsl:import href="util.xsl"/>
  
  <xsl:output indent="yes"/>
  
  <xsl:param name="cocoon-xconf"/>
  <xsl:param name="module-schema"/>
  <xsl:param name="copy-modules"/>
  
  
  <xsl:template name="separator">
    <xsl:if test="following-sibling::list:module">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>
  
  
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
      
      <xsl:variable name="compileDependencyList">
        <xsl:for-each select="list:module">
          <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call"/>
          <xsl:call-template name="separator"/>
        </xsl:for-each>
      </xsl:variable>
      <target name="compile-modules" depends="{$compileDependencyList}"/>
      
      <xsl:variable name="testDependencyList">
        <xsl:for-each select="list:module">
          <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="patch-test"/>
          <xsl:call-template name="separator"/>
        </xsl:for-each>
      </xsl:variable>
      
      <target name="patch-modules-test" depends="{$testDependencyList}"/>
      
      <target name="javadocs-modules">
        <xsl:apply-templates select="list:module" mode="call-javadocs"/>
      </target>
      
      <target name="test-modules" depends="patch-modules-test">
        <xsl:apply-templates select="list:module" mode="call-test"/>
      </target>
      
      <target name="test-modules-canoo">
        <xsl:apply-templates select="list:module" mode="call-test-canoo"/>
      </target>
      
      <xsl:apply-templates select="list:module" mode="target"/>
      
    </project>
  </xsl:template>
  
  
  <xsl:template match="mod:module" mode="call">
    <xsl:text>deploy-module-</xsl:text><xsl:value-of select="mod:id"/>
  </xsl:template>
  

  <xsl:template match="mod:module" mode="patch-test">
    <xsl:text>patch-module-test-</xsl:text><xsl:value-of select="mod:id"/>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="call-test">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call-test"/>
  </xsl:template>
  
  <xsl:template match="mod:module" mode="call-test">
    <antcall target="test-module-{mod:id}"/>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="call-test-canoo">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call-test-canoo"/>
  </xsl:template>
  
  <xsl:template match="mod:module" mode="call-test-canoo">
    <antcall target="canoo-module-{mod:id}"/>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="call-javadocs">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call-javadocs"/>
  </xsl:template>
  
  <xsl:template match="mod:module" mode="call-javadocs">
    <antcall target="javadocs-module-{mod:id}"/>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="target">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="target">
      <xsl:with-param name="src" select="@src"/>
    </xsl:apply-templates>
  </xsl:template>
  
  
  <xsl:template match="mod:module" mode="target">
    <xsl:param name="src"/>
    <xsl:variable name="id" select="mod:id"/>
    <xsl:variable name="shortname" select="substring(mod:id, string-length(mod:package) + 2)"/>

    <target name="validate-module-{$id}">
      <jing rngfile="{$module-schema}" file="{$src}/module.xml"/>
    </target>
    
    <xsl:text>
      
    </xsl:text>
    <xsl:comment>Compile module <xsl:value-of select="$src"/> </xsl:comment>
    <xsl:text>
    </xsl:text>
    
    <available file="{$src}/java/src" property="compile.module.{$id}"/>
    
    <xsl:variable name="destDirPublic">${build.dir}/modules/<xsl:value-of select="$id"/>/java/classes/api</xsl:variable>
    <xsl:variable name="destDirPrivate">${build.dir}/modules/<xsl:value-of select="$id"/>/java/classes/impl</xsl:variable>
    
    <path id="module.classpath.{$id}.api">
      <path refid="classpath"/>
      <fileset dir="${{build.webapp}}/WEB-INF/lib" includes="lenya-*-api.jar"/>
      <xsl:for-each select="mod:depends">
        <fileset dir="${{build.webapp}}/WEB-INF/lib" includes="lenya-module-{@module}-api.jar"/>
      </xsl:for-each>
      <fileset dir="{$src}" includes="java/lib/*.jar"/>
      <fileset dir="${{lib.dir}}" includes="*.jar"/>
    </path>
    
    <path id="module.classpath.{$id}.impl">
      <path refid="module.classpath.{$id}.api"/>
      <fileset dir="${{build.webapp}}/WEB-INF/lib" includes="lenya-module-{$id}-api.jar"/>
    </path>
    
    <target name="compile-module-{$id}" if="compile.module.{$id}">
      
      <mkdir dir="{$destDirPublic}"/>
      
      <xsl:if test="mod:export[@package]">
        <javac
          destdir="{$destDirPublic}"
          debug="${{debug}}"
          optimize="${{optimize}}"
          deprecation="${{deprecation}}"
          target="${{target.vm}}"
          nowarn="${{nowarn}}"
          source="${{src.java.version}}">
          <src path="{$src}/java/src"/>
          <xsl:for-each select="mod:export[@package]">
            <include name="{translate(@package, '.', '/')}/*.java"/>
          </xsl:for-each>
          <classpath refid="module.classpath.{$id}.api"/>
        </javac>
        
        <jar jarfile="${{build.webapp}}/WEB-INF/lib/lenya-module-{$id}-api.jar" index="true">
          <fileset dir="{$destDirPublic}">
            <exclude name="**/Manifest.mf"/>
          </fileset>
        </jar>
      </xsl:if>

      <mkdir dir="{$destDirPrivate}"/>
      <javac
        destdir="{$destDirPrivate}"
        debug="${{debug}}"
        optimize="${{optimize}}"
        deprecation="${{deprecation}}"
        target="${{target.vm}}"
        nowarn="${{nowarn}}"
        source="${{src.java.version}}">
        <src path="{$src}/java/src"/>
        <xsl:for-each select="mod:export[@package]">
          <exclude name="{translate(@package, '.', '/')}/*.java"/>
        </xsl:for-each>
        <classpath refid="module.classpath.{$id}.impl"/>
      </javac>
      
      <jar jarfile="${{build.webapp}}/WEB-INF/lib/lenya-module-{$id}-impl.jar" index="true">
        <fileset dir="{$destDirPrivate}">
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
        <copy 
          todir="${{build.webapp}}/lenya/modules/{$dirName}"
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
        
      <xpatch file="${{build.dir}}/impl/org/apache/lenya/lenya.roles"
        srcdir="{$src}"
        includes="config/lenya-roles/*.xroles"
        addComments="false"/>
      
      <xpatch file="${{build.webapp}}/sitemap.xmap"
        srcdir="{$src}" 
        includes="config/sitemap/*.xmap"
        addComments="false"/>
      
    </target>
    
    <xsl:variable name="dependencyList">
      <xsl:for-each select="mod:depends">
        <xsl:text>deploy-module-</xsl:text><xsl:value-of select="@module"/><xsl:text>, </xsl:text>
      </xsl:for-each>
    </xsl:variable>
    
    <target name="deploy-module-{$id}"
      depends="{$dependencyList} validate-module-{$id}, compile-module-{$id}, copy-module-{$id}, patch-module-{$id}"/>
    
    <!-- ============================================================ -->
    <!-- Javadocs -->
    <!-- ============================================================ -->
    
    <!-- Set a variable if javadoc is already up-to-date -->
    <target name="javadocs-module-check-{$id}">
      <uptodate property="javadocs.notrequired.module.{$id}" targetfile="${{dist.bin.javadocs}}/packages.html" >
        <srcfiles dir="{$src}/java/src" includes="**/*.java"/>
      </uptodate>
    </target>
    
    <target name="javadocs-module-{$id}"
            if="compile.module.{$id}"
            unless="javadocs.notrequired.module.{$id}">
      <javadoc packagenames="${{packages}}"
        destdir="${{dist.bin.javadocs}}/modules/{$shortname}"
        author="true"
        version="true"
        use="false"
        noindex="true"
        breakiterator="true"
        windowtitle="${{Name}} API - Version ${{version}}"
        doctitle="${{Name}}"
        bottom="Copyright &#169; ${year} Apache Software Foundation. All Rights Reserved."
        stylesheetfile="${{src.resource.dir}}/javadoc.css"
        source="${{src.java.version}}">
        <!-- sources -->
        <sourcepath>
          <pathelement path="{$src}/java/src"/>
        </sourcepath>
        
        <!-- pass ant in the classpath to avoid class not found errors -->
        <classpath refid="module.classpath.{$id}.api"/>
      </javadoc>
    </target>
    
    <!-- ============================================================ -->
    <!-- Test -->
    <!-- ============================================================ -->
    
    <available file="{$src}/java/test" property="test.module.{$id}"/>
    
    <xsl:variable name="testDependencyList">
      <xsl:for-each select="mod:depends">
        <xsl:text>patch-module-test-</xsl:text><xsl:value-of select="@module"/>
        <xsl:if test="following-sibling::mod:depends">
          <xsl:text>, </xsl:text>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    
    <target name="patch-module-test-{$id}">
      <xsl:if test="normalize-space($testDependencyList) != ''">
        <xsl:attribute name="depends"><xsl:value-of select="$testDependencyList"/></xsl:attribute>
      </xsl:if>
      <mkdir dir="${{build.dir}}/modules/{$id}"/>
      <xslt basedir="{$src}"
        includes="config/cocoon-xconf/*.xconf, config/cocoon-xconf/*/*.xconf"
        destdir="${{build.dir}}/modules/{$id}"
        style="${{src.resource.dir}}/test/xpatch2testpatch.xsl"
        extension=".xtest"/>
      <xpatch file="${{build.test}}/org/apache/lenya/cms/LenyaTestCase.xtest"
        srcdir="${{build.dir}}/modules/{$id}"
        includes="config/cocoon-xconf/*.xtest, config/cocoon-xconf/*/*.xtest"
        addComments="false"/>
    </target>
    
    <target name="test-module-{$id}" if="test.module.{$id}" depends="compile-module-{$id}">

      <xsl:variable name="testDestDir">${build.dir}/modules/<xsl:value-of select="$id"/>/java/test</xsl:variable>
      
      <mkdir dir="{$testDestDir}"/>
      
      <path id="module.test-classpath.{$id}">
        <path refid="module.classpath.{$id}.impl"/>
        <fileset dir="${{build.webapp}}/WEB-INF/lib">
          <include name="lenya-module-{$id}-impl.jar"/>
        </fileset>
        <path location="${{build.test}}"/>
      </path>
      
      <javac
        destdir="{$testDestDir}"
        debug="${{debug}}"
        optimize="${{optimize}}"
        deprecation="${{deprecation}}"
        target="${{target.vm}}"
        nowarn="${{nowarn}}"
        source="${{src.java.version}}">
        <src path="{$src}/java/test"/>
        <classpath refid="module.test-classpath.{$id}"/>
      </javac>
      
      <!-- Copy test resources -->
      <copy todir="{$testDestDir}" filtering="on">
        <fileset dir="{$src}/java/test" excludes="**/*.java"/>
      </copy>
      
      <junit printsummary="yes" showoutput="true" haltonerror="on" haltonfailure="on">
        <classpath>
          <fileset dir="${{build.webapp}}/WEB-INF/lib" includes="*.jar, endorsed/*.jar"/>
          <path location="${{build.webapp}}/WEB-INF/classes"/>
          <path location="${{build.test}}"/>
          <path location="{$testDestDir}"/>
        </classpath>
        <formatter type="plain" usefile="false" />
        <formatter type="xml" />
        <jvmarg value="-Djava.endorsed.dirs='${{basedir}}/build/lenya/webapp/WEB-INF/lib/endorsed'"/>
        <sysproperty key="junit.test.loglevel" value="${{junit.test.loglevel}}"/>
        <sysproperty key="contextRoot" value="${{basedir}}/build/lenya/webapp"/>
        <sysproperty key="tempDir" value="${{basedir}}/build/lenya/temp"/>
        <sysproperty key="test.repo.webappDirectory" value="${{build.webapp}}"/>
        <sysproperty key="test.repo.repositoryFactory" value="${{repository.factory}}"/>
        <batchtest todir="${{junit.dir}}">
          <fileset dir="{$testDestDir}" includes="**/*Test.class" excludes="**/Abstract*.class"/>
        </batchtest>
      </junit>
    </target>
    
    <!-- ============================================================ -->
    <!-- Canoo WebTest -->
    <!-- ============================================================ -->
    
    <available file="{$src}/test/canoo/test.xml" property="canoo.module.{$id}"/>
    
    <target name="canoo-module-{$id}" if="canoo.module.{$id}">
      <ant dir="{$src}/test/canoo" antfile="test.xml" inheritall="true"/>
    </target>
      
  </xsl:template>
  
  <xsl:template match="mod:depends" mode="dependencyWarning">
    <xsl:param name="id"/>
    <antcall target="dependency-warning-{@module}">
      <param name="dependentModule" value="{$id}"/>
    </antcall>
  </xsl:template>
  
  
</xsl:stylesheet>
