<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:mod="http://apache.org/lenya/module/1.0"
  xmlns:list="http://apache.org/lenya/module-list/1.0">
  
  <xsl:output indent="yes"/>
  
  <xsl:param name="cocoon-xconf"/>

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
      
      <xsl:apply-templates select="list:module" mode="target"/>
      
    </project>
  </xsl:template>
  
  
  <xsl:template match="list:module" mode="call">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="call"/>
  </xsl:template>
  
  
  <xsl:template match="mod:module" mode="call">
    <antcall target="deploy-module-{mod:id}"/>
  </xsl:template>
  

  <xsl:template match="list:module" mode="target">
    <xsl:apply-templates select="document(concat(@src, '/module.xml'))/mod:module" mode="target">
      <xsl:with-param name="src" select="@src"/>
    </xsl:apply-templates>
  </xsl:template>
  
  
  <xsl:template match="mod:module" mode="target">
    <xsl:param name="src"/>
    <xsl:variable name="id" select="mod:id"/>
    
    <xsl:text>
      
    </xsl:text>
    <xsl:comment>Compile module <xsl:value-of select="$src"/> </xsl:comment>
    <xsl:text>
    </xsl:text>
    
    <xsl:variable name="srcDir"><xsl:value-of select="$src"/>/java/src</xsl:variable>
    <available file="{$srcDir}" property="compile.module.{$id}"/>
    
    <target name="compile-module-{$id}" if="compile.module.{$id}">
      
      <xsl:variable name="destDir">${build.dir}/modules/<xsl:value-of select="$id"/></xsl:variable>
      <xsl:variable name="debug">${debug}</xsl:variable>
      <xsl:variable name="optimize">${optimize}</xsl:variable>
      <xsl:variable name="deprecation">${deprecation}</xsl:variable>
      <xsl:variable name="target">${target.vm}</xsl:variable>
      <xsl:variable name="nowarn">${nowarn}</xsl:variable>
      
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
    
    <xsl:variable name="dependencyList">
      <xsl:for-each select="mod:depends">
        <xsl:text>deploy-module-</xsl:text><xsl:value-of select="@module"/><xsl:text>, </xsl:text>
      </xsl:for-each>
    </xsl:variable>
    
    <target name="deploy-module-{$id}"
      depends="{$dependencyList} compile-module-{$id}, copy-module-{$id}, patch-module-{$id}"/>
    
  </xsl:template>
  
  
  <xsl:template name="lastStep">
    <xsl:param name="path"/>
    <xsl:choose>
      <xsl:when test="contains($path, '/')">
        <xsl:call-template name="lastStep">
          <xsl:with-param name="path" select="substring-after($path, '/')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$path"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>