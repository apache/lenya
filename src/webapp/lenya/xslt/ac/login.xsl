<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  <xsl:param name="publication_name"/>
  <xsl:param name="publication_id"/>
  
  <xsl:variable name="copyright">copyright &#169; 2003 Apache Lenya, Apache Software Foundation</xsl:variable>
  <xsl:variable name="prefix">/<xsl:value-of select="$publication_id"/></xsl:variable>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title>
        <xsl:value-of select="$publication_name"/> - <xsl:call-template name="html-title"/>
      </page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
        <p>
          <font face="verdana" size="-2">
            <xsl:value-of select="$copyright"/>
          </font>
        </p>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template name="html-title">LOGIN</xsl:template>
  
  
  <xsl:template match="login">
    <p>
        <xsl:apply-templates select="authentication_failed"/>
        <xsl:apply-templates select="protected_destination"/>
        <xsl:apply-templates select="no_protected_destination"/>
        <xsl:apply-templates select="current_username"/>
        <xsl:apply-templates select="no_username_yet"/>
        <xsl:apply-templates select="authenticator"/>
        <xsl:apply-templates select="no_authenticator_id_yet"/>
    </p>
        <p>
          <b>NOTE:</b> try user &quot;lenya&quot; and password &quot;levi&quot;
        </p>
    <div class="lenya-box">
      <div class="lenya-box-title">Login</div>
      <div class="lenya-box-body">
        <form method="post">
          <xsl:attribute name="action">
            <xsl:value-of select="context"/>
            <xsl:value-of select="$prefix"/>/do-login</xsl:attribute>
          <table class="lenya-table-noborder">
            <tr>
              <td>Username:</td>
              <td>
                <input class="lenya-form-element" name="username" type="text"/>
              </td>
            </tr>
            <tr>
              <td>Password:</td>
              <td>
                <input class="lenya-form-element" name="password" type="password"/>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input type="submit" value="login"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
  </xsl:template>
  
  
  <xsl:template match="current_username">
    <br/>Current username: <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="authenticator">
    <br/>Last Authenticator: <xsl:value-of select="name"/> (<xsl:value-of select="@type"/>)
  </xsl:template>
    
    
  <xsl:template match="no_username_yet">
    <br/>No username yet
  </xsl:template>
    
    
  <xsl:template match="no_authenticator_id_yet">
    <br/>No authenticator id yet
  </xsl:template>
    
    
  <xsl:template match="protected_destination">
    <br/>Request for protected uri: <a>
      <xsl:attribute name="href">
        <xsl:apply-templates/>
      </xsl:attribute>
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  
  
  <xsl:template match="no_protected_destination">
    <br/>
    <font color="red">Exception:</font> No protected destination
  </xsl:template>
    
    
  <xsl:template match="authentication_failed">
    <br/>
    <font color="red">Authentication failed</font>
  </xsl:template>
  
  
</xsl:stylesheet>
