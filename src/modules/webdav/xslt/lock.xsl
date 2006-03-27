<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="userid"/>
    <xsl:param name="docid"/>

	<xsl:template match="/">
        <D:prop xmlns:D="DAV:">
          <D:lockdiscovery>
               <D:activelock>
                    <D:locktype><D:write/></D:locktype>
                    <D:lockscope><D:exclusive/></D:lockscope>
                    <D:depth>Infinity</D:depth>
                    <xsl:if test="//*[local-name()='owner']">
                        <D:owner><xsl:value-of select="//*[local-name()='owner']"/></D:owner>
                    </xsl:if>
                    <D:timeout>Second-604800</D:timeout>
                    <D:locktoken>
                         <D:href>
                           <xsl:value-of select="$docid"/>
                         </D:href>
                    </D:locktoken>
               </D:activelock>
          </D:lockdiscovery>
        </D:prop>	
	</xsl:template>

</xsl:stylesheet>