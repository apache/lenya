<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : tabs.xsl
    Created on : 10. April 2003, 17:26
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    
<xsl:import href="../../../../../xslt/navigation/tabs.xsl"/>

<xsl:template match="nav:site">
    
  <div id="tabs">
  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
    <xsl:call-template name="pre-separator"/>
    <xsl:for-each select="nav:node">
      <xsl:if test="position() &gt; 1">
        <xsl:call-template name="separator"/>
      </xsl:if>
      
      <xsl:choose>
        <xsl:when test="descendant-or-self::nav:node[@current = 'true']">
          <xsl:call-template name="tab-selected"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="tab"/>
        </xsl:otherwise>
      </xsl:choose>
        
    </xsl:for-each>
    <xsl:call-template name="post-separator"/>
    </tr>
  </table>
  </div>

</xsl:template>


<xsl:template name="tab">
  <td><div class="tab"><xsl:call-template name="label"/></div></td>
</xsl:template>


<xsl:template name="tab-selected">
  <td><div class="tab-selected"><xsl:call-template name="label"/></div></td>
</xsl:template>


<xsl:template name="separator">
    <td><div class="tab-separator">&#160;</div></td>
</xsl:template>


<xsl:template name="pre-separator">
    <td><div class="tab-pre-separator">&#160;</div></td>
</xsl:template>


<xsl:template name="post-separator">
    <td class="tab-post-separator"><div class="tab-separator">&#160;</div></td>
</xsl:template>


</xsl:stylesheet> 
