<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:dir="http://apache.org/cocoon/directory/2.0">

<!-- Displays the directory listing -->
  <xsl:template match="dir:directory">

   <a href="../" title="View Parent Directory listing"><b>Parent</b></a>
   <br/>
   
   <table>
  	<xsl:apply-templates/>
   </table>
   
  </xsl:template>

<!-- Displays sub-directories -->
  <xsl:template match="dir:directory/dir:directory">
   <tr>
     <td><a href="{@name}/"><img border="0" src="../images/dir.gif"/><xsl:value-of select="@name"/></a></td>
     <td></td>
     <td></td>
     <td><xsl:value-of select="@date"/></td>
   </tr>
  </xsl:template>


	
<!-- Displays files in the directory listing -->
  <xsl:template match="dir:file">
   <tr>
   
     <td><a href="../get(bravo)/{@name}"><img border="0" src="../images/see.gif"/><xsl:value-of select="@name"/></a></td>
     <td><xsl:value-of select="dir:xpath/title"/></td>
     <td><a href="../see(bravo)/{@name}"> (preview) </a></td>
     <td><xsl:value-of select="@date"/></td>
    
   </tr>
   
  </xsl:template>


</xsl:stylesheet>