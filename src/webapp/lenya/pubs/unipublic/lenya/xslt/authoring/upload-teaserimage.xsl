<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    <html>
      <head>
	<title>Image Upload</title>
<link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
      </head>
      <body>
	<xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="upload-image">
    <h1>Upload a Teaser Image</h1>
    <p>Please browse for an image (80 x 60 pixel) on your harddisk. </p>
    <form action="{request-uri}" method="post" enctype="multipart/form-data">
      <input type="hidden" name="xpath" value="{xpath}"/>
      <input type="hidden" name="documentid" value="{documentid}"/>
      <input type="hidden" name="lenya.usecase" value="{usecase}"/>
      <input type="hidden" name="lenya.step" value="upload"/>
      <input type="hidden" name="publisher" value="{current_username}"/>
      <input type="hidden" name="referer" value="{referer}"/>
      <input type="hidden" name="identifier" value="teaser"/>
      <table border="0">
	<tr>
	  <td>Browse File&#160;</td><td><input type="file" name="uploadFile" size="80"/></td>
	</tr>
	<tr><td>&#160;</td></tr>
	<tr>
	  <td><input type="submit" value="Upload"/><input type="reset" value="Reset"/></td>
	</tr>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="exception">
    <font color="red">EXCEPTION</font><br />
    Go <a href="{../referer}">back</a> to page.<br />
    <p>
      For further details please take a look at the log-files
      of Cocoon. In most cases it's one of the two possible exceptions:
      <ol>
	<li>The id is not allowed to have whitespaces</li>
	<li>The id is already in use</li>
      </ol>
    </p>
  </xsl:template>
  
</xsl:stylesheet>  
