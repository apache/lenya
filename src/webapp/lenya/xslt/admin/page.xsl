<?xml version="1.0"?>

<!-- $Id: page.xsl,v 1.1 2003/07/03 09:40:13 andreas Exp $ -->

<xsl:stylesheet version="1.0"
	  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	  xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
	  >

<xsl:param name="root"/>

<xsl:template match="page:body">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:call-template name="navigation"/>
    <xsl:apply-templates select="*"/>
  </xsl:copy>
</xsl:template>


<xsl:template name="navigation">
  <div class="lenya-sidebar">
    <div class="lenya-sidebar-heading">Administration</div>
<!--    <div class="lenya-publication-item">Access Control</div>-->
    <ul>
      <li><a href="{$root}/admin/users.html">Users</a></li>
      <li><a href="{$root}/admin/groups.html">Groups</a></li>
      <li><a href="{$root}/admin/machines.html">Machines</a></li>
    </ul>
  </div>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet>
