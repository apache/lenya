<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title>User Overview: <xsl:value-of select="user/id"/></page:title>
      <page:body>
        <xsl:apply-templates select="message"/>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    
    <table class="lenya-noborder">
      
    <tr>
    <td>
    
    <div class="lenya-box">
      <div class="lenya-box-title">Profile</div>
      <div class="lenya-box-body">
        
          <table class="lenya-table-noborder">
            
            <tr>
              <td class="lenya-entry-caption">User&#160;ID:</td>
              <td><xsl:value-of select="id"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption">Full&#160;Name:</td>
              <td><xsl:value-of select="name"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption">E-Mail:</td>
              <td><xsl:value-of select="email"/></td>
            </tr>
            <tr>
              <td valign="top" class="lenya-entry-caption">Description:</td>
              <td><xsl:value-of select="description"/></td>
            </tr>
            <tr>
              <td/>
              <td>
				        <form method="GET" action="lenya.usecase.change_profile">
				          <input type="submit" value="Edit Profile"/>
				        </form>
              </td>
            </tr>
          </table>
      </div>
    </div>

    <xsl:if test="not(user/@ldap = 'false')">
      <xsl:call-template name="password"/>
    </xsl:if>
    
    <div class="lenya-box">
      <div class="lenya-box-title">Group Affiliation</div>
      <div class="lenya-box-body">
        
          <table class="lenya-table-noborder">
            
            <tr>
              <td class="lenya-entry-caption" valign="top">Groups:</td>
              <td>
                <xsl:apply-templates select="groups"/>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
				        <form method="GET" action="lenya.usecase.change_groups">
				          <input type="submit" value="Edit Group Affiliation"/>
				        </form>
              </td>
            </tr>
          </table>
      </div>
    </div>
            
    </td>
    </tr>
    </table>
    
  </xsl:template>
  
  
  <xsl:template match="groups">
      <xsl:apply-templates select="group">
        <xsl:sort/>
      </xsl:apply-templates>
  </xsl:template>
  
  
  <xsl:template match="group">
    <xsl:if test="position() &gt; 1"><br/></xsl:if>
    <span style="white-space: nowrap">
    <a href="../../groups/{@id}/index.html"><xsl:value-of select="@id"/></a>
    <xsl:if test="normalize-space(.) != ''">
      &#160;(<xsl:value-of select="."/>)
    </xsl:if>
    </span>
  </xsl:template>
  
  
  <xsl:template name="password">
    <div class="lenya-box">
      <div class="lenya-box-title">Password</div>
      <div class="lenya-box-body">
        
          <table class="lenya-table-noborder">
            
            <tr>
              <td class="lenya-entry-caption">User:</td>
              <td>
				        <form method="GET" action="lenya.usecase.change_password_user">
				          <input type="submit" value="Change Password"/>
				        </form>
				      </td>
				    </tr>
            <tr>
              <td class="lenya-entry-caption">Admin:</td>
              <td>
				        <form method="GET" action="lenya.usecase.change_password_admin">
				          <input type="submit" value="Change Password"/>
				        </form>
              </td>
            </tr>
          </table>
      </div>
    </div>
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
