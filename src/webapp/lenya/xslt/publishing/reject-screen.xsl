<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
    xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
    >
 
<xsl:import href="../util/page-util.xsl"/>

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="lenya.event"/>

<xsl:template match="/usecase:reject">

  <page:page>
    <page:title>Reject</page:title>
    <page:body>
    	
		<form method="GET" action="">
		
		<input type="hidden" name="lenya.usecase" value="transition"/>
		<input type="hidden" name="lenya.event" value="{$lenya.event}"/>
		<input type="hidden" name="task-id" value="ant"/>
		<input type="hidden" name="target" value="mail"/>
		
		<not:notification>
			<not:preset>
				<xsl:copy-of select="not:users"/>
			</not:preset>
		</not:notification>

    <input type="submit" name="submit" value="Reject"/>
      
    </form>  
        
    </page:body>
  </page:page>
</xsl:template>


</xsl:stylesheet>  
