<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:session="http://www.apache.org/xsp/session/2.0">

<xsl:import href="../util/page-util.xsl"/>

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="publication_name"/>
<xsl:param name="publication_id"/>

<xsl:variable name="copyright">copyright &#169; 2003 Apache Lenya, Apache Software Foundation</xsl:variable>
<xsl:variable name="prefix">/<xsl:value-of select="$publication_id"/></xsl:variable>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="page">
  <html>
   <head>
    <title><xsl:value-of select="$publication_name"/> - <xsl:call-template name="html-title"/></title>
    <xsl:call-template name="include-css">
      <xsl:with-param name="context-prefix" select="concat(body/login/context, $prefix)"/>
    </xsl:call-template>
    </head>
    <body bgcolor="#ffffff">
     <h1><xsl:value-of select="$publication_name"/></h1>

     <xsl:apply-templates select="body"/>

     <p>
     <font face="verdana" size="-2">
       <xsl:value-of select="$copyright"/>
     </font>
     </p>
    </body>
  </html>
</xsl:template>

<xsl:template name="html-title">
LOGIN
</xsl:template>

<xsl:template match="login">
<font face="verdana">
<b>LOGIN</b>

<xsl:apply-templates select="authentication_failed"/>

<xsl:apply-templates select="protected_destination"/>
<xsl:apply-templates select="no_protected_destination"/>

<xsl:apply-templates select="current_username"/>
<xsl:apply-templates select="no_username_yet"/>

<xsl:apply-templates select="authenticator"/>
<xsl:apply-templates select="no_authenticator_id_yet"/>

<p>
<div class="menu">
<b>NOTE:</b> try user "lenya" and password "levi"
</div>
</p>

<form method="post">
<xsl:attribute name="action"><xsl:value-of select="context"/><xsl:value-of select="$prefix"/>/do-login</xsl:attribute>
<table>
<tr><td>Username:</td><td><input type="text" name="username"/></td></tr>
<tr><td>Password:</td><td><input type="password" name="password"/></td></tr>
<tr><td/><td><input type="submit" value="login"/></td></tr>
</table>
</form>
</font>
</xsl:template>

<xsl:template match="current_username">
  <br />Current username: <xsl:apply-templates/>
</xsl:template>

<xsl:template match="authenticator">
  <br />Last Authenticator: <xsl:value-of select="name"/> (<xsl:value-of select="@type"/>)
</xsl:template>

<xsl:template match="no_username_yet">
  <br />No username yet
</xsl:template>

<xsl:template match="no_authenticator_id_yet">
  <br />No authenticator id yet
</xsl:template>

<xsl:template match="protected_destination">
  <br />Request for protected uri: <a><xsl:attribute name="href"><xsl:apply-templates/></xsl:attribute><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="no_protected_destination">
  <br /><font color="red">Exception:</font> No protected destination
</xsl:template>

<xsl:template match="authentication_failed">
  <br /><font color="red">Authentication failed</font>
</xsl:template>

</xsl:stylesheet>
