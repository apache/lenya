<?xml version="1.0"?>

 <xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:session="http://www.apache.org/xsp/session/2.0"
     xmlns:page="http://www.lenya.org/2003/cms-page"
     >
 
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
 
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>User Administration</page:title>
      <page:body>
	<h1>User Administration</h1>
	
	<xsl:apply-templates select="body"/>
	<xsl:apply-templates select="users"/>
	
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="users">
    <table>
      <tr><td>User ID</td><td>Full Name</td><td>Groups</td></tr>
      <xsl:apply-templates select="user"/>
    </table>
  </xsl:template>
  
  <xsl:template match="user">
    <tr>
      <td><xsl:value-of select="id"/></td>
      <td><xsl:value-of select="fullName"/></td>
      <xsl:apply-templates select="groups"/>
      <td>
	<form method="GET">
	  <input type="hidden" name="lenya.usecase" value="user-modify"/>
	  <input type="hidden" name="lenya.step" value="showscreen"/>
	  <input type="hidden" name="user-id">
	    <xsl:attribute name="value"><xsl:value-of select="id"/></xsl:attribute>
	  </input>
	  <input type="submit" value="Edit"/>
	</form>
      </td>
      <td>
	<form method="GET">
	  <input type="hidden" name="lenya.usecase" value="user-delete"/>
	  <input type="hidden" name="lenya.step" value="showscreen"/>
	  <input type="hidden" name="user-id">
	    <xsl:attribute name="value"><xsl:value-of select="id"/></xsl:attribute>
	  </input>
	  <input type="submit" value="Delete"/>
	</form>
      </td>
    </tr>

  </xsl:template>
  
  <xsl:template match="groups">
    <td>
      <xsl:apply-templates select="group"/>
    </td>
  </xsl:template>
  
  <xsl:template match="group">
    <xsl:value-of select="."/>
    <xsl:if test="position() != last()">, <xsl:text/></xsl:if>
  </xsl:template>

</xsl:stylesheet>
