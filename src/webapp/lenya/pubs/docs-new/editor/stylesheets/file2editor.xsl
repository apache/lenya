<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 >


<!-- your form for editing 'page' doctype content, handles the 'bravo' sub-editor for 'get' and 'new' behaviours -->
	<xsl:template match="document">
		Bravo Editor<br/>
		
				<form method="post" action="../pre(bravo)">
				
				Title <input name="title" size="40"  onchange="changed=true;" value="{normalize-space(header/title)}"/><br/>
                Body<textarea name="content" rows="20" cols="80" xml:space="preserve" onchange="changed=true;"><xsl:apply-templates select="body/*"/></textarea><br/>
          
				Save as<input type="text" name="target" value="new.xml" size="40" title="Note: to make a new directory, prepend the filename with a directory path"/>
				<input class="submit" type="submit" value="submit"/>
					
				</form>	

  </xsl:template>

</xsl:stylesheet>
