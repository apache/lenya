<?xml version="1.0" encoding="UTF-8"?>

<!--
  $Id: profile.xsl,v 1.3 2004/02/18 19:26:08 roku Exp $
-->

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
        <xsl:apply-templates select="iprange"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="iprange">
    
    <table class="lenya-noborder">
    <tr>
    <td valign="top">
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>IP Range Profile</i18n:text></div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{/page/continuation}.continuation">
          <table class="lenya-table-noborder">
            
            <xsl:apply-templates select="message"/>
            
            <tr>
              
              <td class="lenya-entry-caption"><i18n:text>IP Range ID</i18n:text></td>
              <td>
                 <xsl:choose>
                   <xsl:when test="@new = 'true'">
                     <input class="lenya-form-element" name="iprange-id" type="text" value="{id}"/>
                   </xsl:when>
                   <xsl:otherwise>
                     <xsl:value-of select="id"/>
                   </xsl:otherwise>
                 </xsl:choose>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Name</i18n:text></td>
              <td>
                <input class="lenya-form-element" name="name" type="text" value="{name}"/>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Description</i18n:text></td>
              <td>
                <input class="lenya-form-element" name="description" type="text" value="{description}"/>
              </td>
            </tr>
<!--            
            <SCRIPT>
            	<xsl:comment>
            	function check_num(obj) {
            	  var value = parseInt(obj.value);
            	  if (value &amp;&amp; 0 &lt;= value &amp;&amp; value &lt;= 255) {
                  obj.value = "" + parseInt(obj.value);
            	  }
            	  else {
            	    obj.value = "0";
            	  }
            	}
            	</xsl:comment>
            </SCRIPT>
-->            
            <tr>
            	<td class="lenya-entry-caption"><i18n:text>Network Address</i18n:text></td>
            	<td>
            		<xsl:apply-templates select="net/number"/>
              </td>
            </tr>
            <tr>
            	<td class="lenya-entry-caption"><i18n:text>Subnet Mask</i18n:text></td>
            	<td>
            		<xsl:apply-templates select="mask/number"/>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input i18n:attr="value"  name="submit" type="submit" value="Save"/>
                &#160;
                <input i18n:attr="value" name="cancel" type="submit" value="Cancel"/>
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
  
  
  <xsl:template match="number">
  	<xsl:if test="position() &gt; 1">.</xsl:if>
  	<input type="text" name="{local-name(..)}-{position()}" size="3" value="{normalize-space(.)}"
  		maxlength="3">
  		<xsl:if test="@error = 'true'">
  			<xsl:attribute name="style">background-color: #FF9999;</xsl:attribute>
  		</xsl:if>
    </input>
<!--  		 onchange="check_num(this)" -->
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
