<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
    xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
    >
 
<xsl:import href="../util/page-util.xsl"/>
<xsl:import href="../scheduler/common.xsl"/>

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="action" select="'publish'"/>
<xsl:param name="lenya.event"/>

<xsl:variable name="separator" select="','"/>

<xsl:variable name="uris"><xsl:value-of select="/usecase:publish/usecase:uris"/></xsl:variable>
<xsl:variable name="sources"><xsl:value-of select="/usecase:publish/usecase:sources"/></xsl:variable>
<xsl:variable name="document-id"><xsl:value-of select="/usecase:publish/usecase:document-id"/></xsl:variable>
<xsl:variable name="document-language"><xsl:value-of select="/usecase:publish/usecase:language"/></xsl:variable>
<xsl:variable name="task-id"><xsl:value-of select="/usecase:publish/usecase:task-id"/></xsl:variable>

<xsl:template match="/usecase:publish">

  <page:page>
    <page:title>Publish</page:title>
    <page:body>
      <form name="form_publish">
        
        <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
        <input type="hidden" name="lenya.step" value="publish"/>
        <input type="hidden" name="task-id" value="{$task-id}"/>
        
				<input type="hidden" name="properties.publish.sources" value="{$sources}"/>
				<input type="hidden" name="properties.publish.documentid" value="{$document-id}"/>
				<input type="hidden" name="properties.export.uris" value="{$uris}"/>
				<input type="hidden" name="properties.publish.language" value="{$document-language}"/>
        
        <div class="menu">Do you really want to publish the following source<xsl:text/>
          <xsl:if test="contains($sources, $separator)">s</xsl:if>
          <xsl:text/>?
        </div>
        
        <ul>
          <xsl:call-template name="print-list">
            <xsl:with-param name="list-string" select="$sources"/>
          </xsl:call-template>
        </ul>
        
        <div class="menu">And do you really want to publish the following uri<xsl:text/>
          <xsl:if test="contains($uris, $separator)">s</xsl:if>
          <xsl:text/>?
        </div>
        
        <ul>
          <xsl:call-template name="print-list">
            <xsl:with-param name="list-string" select="$uris"/>
          </xsl:call-template>
        </ul>
        
        <not:notification>
        	<not:preset>
        		<xsl:apply-templates select="not:users/not:user"/>
        	</not:preset>
        	<not:textarea/>
        </not:notification>

        <input type="submit" name="lenya.usecase" value="publish"/>
        &#160;&#160;&#160;
        <input type="button" onClick="location.href='';" value="Cancel"/>

        <xsl:call-template name="scheduler-form">
        	<xsl:with-param name="form-name">form_publish</xsl:with-param>
        </xsl:call-template>

      </form>
    </page:body>
  </page:page>
</xsl:template>


</xsl:stylesheet>  
