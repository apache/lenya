<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  >
  
  <xsl:param name="action" select="'removelabel'"/>
  
  <xsl:variable name="document-id"><xsl:value-of select="/usecase:removelabel/usecase:document-id"/></xsl:variable>
  <xsl:variable name="language"><xsl:value-of select="/usecase:removelabel/usecase:language"/></xsl:variable>
  <xsl:variable name="label"><xsl:value-of select="/usecase:removelabel/usecase:label"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/usecase:removelabel/usecase:task-id"/></xsl:variable>
  
  <xsl:template match="/usecase:removelabel">
    
    <page:page>
      <page:title>Remove Language</page:title>
      <page:body>
	<p>
	  <form action="">
	    
	    <input type="hidden" name="lenya.usecase" value="removelabel"/>
	    <input type="hidden" name="lenya.step" value="removelabel"/>
	    <input type="hidden" name="task-id" value="{$task-id}"/>
	    <xsl:call-template name="task-parameters">
	      <xsl:with-param name="prefix" select="''"/>
	    </xsl:call-template>
	    
	    <div class="menu">Do you really want to remove the "<xsl:value-of select="$language"/>" language version from document '<xsl:value-of select="$document-id"/>' (cannot be undone)?</div>
	    
	    <input type="submit" value="YES, remove label"/>
            &#160;&#160;&#160;<input type="button" onClick="location.href='{referer}';" value="Cancel"/>
	  </form>
	</p>
	
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template name="task-parameters">
    <xsl:param name="prefix" select="'task.'"/>
    <input type="hidden" name="{$prefix}properties.remove.label.document-id" value="{$document-id}"/>
    <input type="hidden" name="{$prefix}properties.remove.label.label-name" value="{$label}"/>
    <input type="hidden" name="{$prefix}properties.remove.label.language" value="{$language}"/>
  </xsl:template>
  
</xsl:stylesheet>  
