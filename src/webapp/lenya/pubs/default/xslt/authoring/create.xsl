<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    <html>
      <head>
	<link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
      </head>
      <body>
	<xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="parent-child">
    <h1>New Document</h1>
    
    <xsl:apply-templates select="exception"/>
    
    <xsl:if test="not(exception)">
      <p>
	<form method="POST" 
	  action="{/parent-child/referer}">
	  <input type="hidden" name="parentid" value="{/parent-child/parentid}"/>
	  <input type="hidden" name="lenya.usecase" value="create"/>
	  <input type="hidden" name="lenya.step" value="create"/>
	  <input type="hidden" name="childtype" value="branch"/>
	  <input type="hidden" name="doctype" value="{/parent-child/doctype}"/>
	  <table>
	    <tr>
	      <td>parentid:</td><td>hidden value="<xsl:value-of select="/parent-child/parentid"/>"</td>
	    </tr>
	    <tr>
	      <td>id:</td><td><input type="text" name="childid"/></td>
	    </tr>
	    <tr>
	      <td>name:</td><td><input type="text" name="childname"/></td>
	    </tr>
	  </table>
	  <input type="submit" value="Create"/>&#160;&#160;&#160;
	  <input type="button" onClick="location.href='{/parent-child/referer}';" value="Cancel"/>
	</form>
      </p>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="exception">
    <font color="red">EXCEPTION</font><br />
    Go <a href="{../referer}">back</a> to page.<br />
    <p>
      Exception handling isn't very good at the moment. 
      For further details please take a look at the log-files
      of Cocoon. In most cases it's one of the two possible exceptions:
      <ol>
	<li>The id is not allowed to have whitespaces</li>
	<li>The id is already in use</li>
      </ol>
      Exception handling will be improved in the near future.
    </p>
  </xsl:template>
  
</xsl:stylesheet>  
