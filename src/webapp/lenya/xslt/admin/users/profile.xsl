<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="UTF-8" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text><xsl:value-of select="title"/></i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    
    <table class="lenya-noborder">
    <tr>
    <td valign="top">
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>User Data</i18n:text></div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{/page/continuation}.continuation">
          <table class="lenya-table-noborder">
            
            <xsl:apply-templates select="messages"/>
            <xsl:apply-templates select="ldapid"/>
            <xsl:apply-templates select="id"/>
            <xsl:apply-templates select="fullname"/>
            <xsl:apply-templates select="email"/>
            <xsl:apply-templates select="description"/>
            
            <xsl:if test="@new = 'true' and not(@ldap = 'true')">
              <tr><td colspan="2">&#160;</td></tr>
              <xsl:apply-templates select="password"/>
              <xsl:apply-templates select="confirm-password"/>
            </xsl:if>
            
            <tr>
              <td/>
              <td>
                <input i18n:attr="value" name="submit" type="submit" value="Save"/>
                &#160;
                <input i18n:attr="value" name="cancel" type="submit" value="Cancel"/>
              </td>
            </tr>
            <tr>
                <td class="lenya-entry-caption">
                    <span class="lenya-required">*</span>&#160;<i18n:text>required fields</i18n:text>
                </td>
            </tr>                
          </table>
        </form>
      </div>
    </div>
    
    </td>
    </tr>
    </table>
    
  </xsl:template>
  
  
  <xsl:template match="id">
		<tr>
			
			<td class="lenya-entry-caption"><i18n:text>CMS User ID</i18n:text>&#160;<span class="lenya-admin-required">*</span></td>
			<td>
				 <xsl:choose>
					 <xsl:when test="../@new = 'true'">
						 <input class="lenya-form-element" name="userid" type="text" value="{normalize-space(.)}"/>
					 </xsl:when>
					 <xsl:otherwise>
						 <xsl:value-of select="."/>
					 </xsl:otherwise>
				 </xsl:choose>
			</td>
		</tr>
  </xsl:template>
  
  
  <xsl:template match="ldapid">
		<tr>
			<td class="lenya-entry-caption"><i18n:text>LDAP ID</i18n:text>&#160;<span class="lenya-admin-required">*</span></td>
			<td>
				<input class="lenya-form-element" name="fullname" type="text" value="{normalize-space(.)}"/>
			</td>
		</tr>
  </xsl:template>
  
  
  <xsl:template match="fullname">
    <xsl:if test="not(../@ldap = 'true')">
      <tr>
        <td class="lenya-entry-caption"><i18n:text>Name</i18n:text>&#160;&#160;</td>
        <td>
          <input class="lenya-form-element" name="fullname" type="text" value="{normalize-space(.)}"/>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="email">
		<tr>
			<td class="lenya-entry-caption"><i18n:text>Email</i18n:text>&#160;<span class="lenya-admin-required">*</span></td>
			<td>
				<input class="lenya-form-element" name="email" type="text" value="{normalize-space(.)}"/>
			</td>
		</tr>
  </xsl:template>
  
  
	<xsl:template match="description">
		<tr>
			<td class="lenya-entry-caption"><i18n:text>Description</i18n:text>&#160;&#160;</td>
			<td>
				<textarea class="lenya-form-element" name="description"><xsl:value-of select="normalize-space(.)"/>&#160;</textarea>
			</td>
		</tr>
	</xsl:template>  
	
	
	<xsl:template match="password">
		<tr>
			<td class="lenya-entry-caption"><i18n:text>Password</i18n:text>&#160;<span class="lenya-admin-required">*</span></td>
			<td>
				<input type="password" class="lenya-form-element" name="new-password" value="{normalize-space(.)}"/>
			</td>
		</tr>
	</xsl:template>  
	
	
	<xsl:template match="confirm-password">
		<tr>
			<td class="lenya-entry-caption"><i18n:text>Confirm password</i18n:text>&#160;<span class="lenya-admin-required">*</span></td>
			<td>
				<input type="password" class="lenya-form-element" name="confirm-password" value="{normalize-space(.)}"/>
			</td>
		</tr>
	</xsl:template>  
	
	<xsl:template match="messages">
    <xsl:if test="message">
      <tr>
        <td colspan="2"><xsl:apply-templates/></td>
      </tr>
    </xsl:if>
	</xsl:template>
	
  <xsl:template match="message">
    <xsl:if test="preceding-sibling::message"><br/></xsl:if>
    <span class="lenya-form-message-{@type}"><xsl:value-of select="."/></span>
  </xsl:template>
  
</xsl:stylesheet>