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
      <page:title>Edit User</page:title>
      <page:body>
	<h1>Edit User</h1>
	
	<xsl:apply-templates select="body"/>
	<xsl:apply-templates select="user"/>
	
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="user">
    <form method="post">
      <xsl:attribute name="action"></xsl:attribute>
      <table>
	<tr>
	  <td>User ID</td>
	  <td>
	    <input type="text" name="user-id">
	      <xsl:attribute name="value">
		<xsl:value-of select="id"/>
	      </xsl:attribute>
	    </input>
	  </td>
	</tr>
	<tr>
	  <td>Full Name</td>
	  <td>
	    <input type="text" name="fullName">
	      <xsl:attribute name="value">
		<xsl:value-of select="fullName"/>
	      </xsl:attribute>
	    </input>
	  </td>
	</tr>
	<tr>
	  <td>Email</td>
	  <td>
	    <input type="text" name="email">
	      <xsl:attribute name="value">
		<xsl:value-of select="email"/>
	      </xsl:attribute>
	    </input>
	  </td>
	</tr>
	<tr>
	  <td>Password</td>
	  <td>
	    <input type="password" name="password"/>
	  </td>
	</tr>
	<tr>
	  <td>Confirm Password</td>
	  <td>
	    <input type="password" name="confirm-password"/>
	  </td>
	</tr>
	<tr>
	  <td>Group</td>
	  <td>
	    <select name="groups" size="1">
	      <xsl:apply-templates select="groups"/>
	    </select>
	  </td>
	</tr>
      </table>
      <input type="submit" value="Save"/>
    </form>
  </xsl:template>
  
  <xsl:template match="groups">
    <xsl:apply-templates select="group"/>
  </xsl:template>
  
  <xsl:template match="group">
    <option>
      <xsl:value-of select="."/>
    </option>
  </xsl:template>

</xsl:stylesheet>
