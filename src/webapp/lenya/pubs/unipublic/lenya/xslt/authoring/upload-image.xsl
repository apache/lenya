<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    <html>
      <head>
	<title>Image Upload</title>
      </head>
      <body>
	<xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="upload-image">
    <h1>Image Upload</h1>
    <p>Please browse for an image (gif, jpg) on your harddisk.</p>
    <form action="{request-uri}" method="post" enctype="multipart/form-data">
      <input type="hidden" name="xpath" value="{xpath}"/>
      <input type="hidden" name="documentid" value="{documentid}"/>
      <input type="hidden" name="usecase" value="{usecase}"/>
      <input type="hidden" name="step" value="upload"/>
      <input type="hidden" name="publisher" value="{current_username}"/>
      <input type="hidden" name="referer" value="{referer}"/>
      <input type="hidden" name="insertBefore" value="{insertBefore}"/>
      <table border="0">
	<tr>
	  <td>Browse File&#160;</td><td><input type="file" name="uploadFile"/></td>
	</tr>
	<tr>
	  <td>Identifier&#160;</td><td><input type="text" name="identifier"/>&#160;&#160;<i>(optional)</i></td>
	</tr>
	<tr><td>&#160;</td>
	<td><font size="-1">Will be used as filename. If you leave this field empty the name of the uploaded file will be used. Make sure that the filename resp. the identifier does not contain any white spaces or special characters like +, &amp;, /, etc.</font></td>
	</tr>
	<tr><td>&#160;</td></tr>
	<tr><td colspan="2"><h3>Metadata (Dublin Core)</h3></td></tr>
	<tr>
  <td>Author (Creator)&#160;</td><td><input type="text" name="creator" size="40"/>&#160;&#160;<i>(optional)</i></td>
	</tr>
	<tr>
	  <td>Alt-Text (Image Title)&#160;</td><td><input type="text" name="title" size="40"/>&#160;&#160;<i>(optional)</i></td>
	</tr>
<!--
	<tr>
	  <td>Subject (Keywords)&#160;</td><td><input type="text" name="subject" size="40"/></td>
	</tr>
-->
	<tr>
	  <td>Caption (Image Description)&#160;</td><td><input type="text" name="description" size="40"/>&#160;&#160;<i>(optional)</i></td>
	</tr>
<!--
	<tr>
	  <td>Rights (Copyright)&#160;</td><td><input type="text" name="rights" size="40"/></td>
	</tr>
-->
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
