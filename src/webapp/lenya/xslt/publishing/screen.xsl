<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://www.lenya.org/2003/cms-page"
    xmlns:usecase="http://lenya.org/usecase/1.0"
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
<xsl:variable name="task-id"><xsl:value-of select="/usecase:publish/usecase:task-id"/></xsl:variable>

<xsl:template match="/usecase:publish">


  <page:page>
    <page:title>Publish</page:title>
    <page:body>
      <p>
        <form action="">
        
        <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
        <input type="hidden" name="lenya.usecase" value="publish"/>
        <input type="hidden" name="lenya.step" value="publish"/>
        <input type="hidden" name="task-id" value="{$task-id}"/>
        <xsl:call-template name="task-parameters">
          <xsl:with-param name="prefix" select="''"/>
        </xsl:call-template>
        
        <div class="menu">Do you really want to publish the following source<xsl:text/>
          <xsl:if test="contains(sources, $separator)">s</xsl:if>
          <xsl:text/>?
        </div>
        
        <ul>
          <xsl:call-template name="print-list">
            <xsl:with-param name="list-string" select="$sources"/>
          </xsl:call-template>
        </ul>
        
        <div class="menu">And do you really want to publish the following uri<xsl:text/>
          <xsl:if test="contains(uris, $separator)">s</xsl:if>
          <xsl:text/>?
        </div>
        
        <ul>
          <xsl:call-template name="print-list">
            <xsl:with-param name="list-string" select="$uris"/>
          </xsl:call-template>
        </ul>

        <input type="submit" value="YES"/>
        <!--<input type="submit" name="submit" value="cancel"/>-->
            &#160;&#160;&#160;<input type="button" onClick="location.href='{referer}';" value="Cancel"/>
      </form>
    </p>
    
    <p>

     <xsl:call-template name="scheduler-form">
       <xsl:with-param name="task-id" select="$task-id"/>
     </xsl:call-template>

    </p>
    </page:body>
  </page:page>
</xsl:template>


<xsl:template name="task-parameters">
  <xsl:param name="prefix" select="'task.'"/>
  <input type="hidden" name="{$prefix}properties.publish.sources" value="{$sources}"/>
  <input type="hidden" name="{$prefix}properties.publish.documentid" value="{$document-id}"/>
  <input type="hidden" name="{$prefix}properties.export.uris" value="{$uris}"/>
</xsl:template>


</xsl:stylesheet>  
