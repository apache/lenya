<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:import href="../util/page-util.xsl"/>

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="title" select="'Publish Document'"/>

<xsl:variable name="separator" select="','"/>

<xsl:template match="/">
<html>
  <head>
    <title><xsl:value-of select="$title"/></title>
    <xsl:call-template name="include-css">
      <xsl:with-param name="context-prefix" select="concat(publish/context, '/', publish/publication-id)"/>
    </xsl:call-template>
  </head>
<body>
  <xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="publish">
<p>
<h1><xsl:value-of select="$title"/></h1>
<form action="">
<input type="hidden" name="lenya.usecase" value="publish"/>
<input type="hidden" name="lenya.step" value="execute"/>
<input type="hidden" name="uris" value="{uris}"/>
<input type="hidden" name="sources" value="{sources}"/> <!-- DefaultFilePublisher -->
<input type="hidden" name="properties.publish.sources" value="{sources}"/> <!-- AntTask -->
<input type="hidden" name="task-id" value="{task-id}"/>
<!-- FIXME: arbitrary request parameters set within the menubar should be transfered!
<input type="hidden" name="server-port" value="1937"/>
-->

<div class="menu">Do you really want to publish the following source<xsl:text/>
<xsl:if test="contains(sources, $separator)">s</xsl:if>
<xsl:text/>?
</div>
<ul>
  <xsl:call-template name="print-list">
    <xsl:with-param name="list-string" select="sources"/>
  </xsl:call-template>
</ul>
<div class="menu">And do you really want to publish the following uri<xsl:text/>
<xsl:if test="contains(uris, $separator)">s</xsl:if>
<xsl:text/>?
</div>
<ul>
  <xsl:call-template name="print-list">
    <xsl:with-param name="list-string" select="uris"/>
  </xsl:call-template>
</ul>

<input type="submit" value="YES"/>
&#160;&#160;&#160;<input type="button" onClick="location.href='{referer}';" value="Cancel"/>
</form>
</p>
</xsl:template>


</xsl:stylesheet>  
