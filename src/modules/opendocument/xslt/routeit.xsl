<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:jx="http://apache.org/cocoon/templates/jx/1.0"
		xmlns:sql="http://apache.org/cocoon/SQL/2.0">

<!-- routeit.xsl:  set up SQL
                @author <a href="http://librarycog.uwindsor.ca">art rhyno</a>
-->

<xsl:template match="/router">
	<xsl:variable name="quote">
		<![CDATA[']]>
	</xsl:variable>

	<xsl:variable name="replace_quote">
		<![CDATA[%20]]>
	</xsl:variable>

	<xsl:variable name="clean_url">
		<xsl:value-of select="translate(url,$quote,$replace_quote)"/>
	</xsl:variable>

	<xsl:variable name="comments">
		<xsl:value-of select="translate(htmlstuff,$quote,$replace_quote)"/>
	</xsl:variable>

	<updates>
		<sql:execute-query>
		<sql:query>
			INSERT INTO resource (uri, blob_val) VALUES
			('<xsl:value-of select="$clean_url"/>',
			'<xsl:value-of select="$comments"/>')
		</sql:query>
		</sql:execute-query>
<!--
		<sql:execute-query>
		<sql:query>
		<xsl:for-each select="triples/triple">
			<xsl:value-of select="value"/><br/>
		</xsl:for-each>
		</sql:query>
		</sql:execute-query>

		<sql:execute-query>
		<sql:query>
			SELECT resource.id from resource where 
				uri='<xsl:value-of select="$clean_url"/>'
		</sql:query>
  		<sql:execute-query>
     			<sql:query>
				UPDATE rdf SET lockstamp=NOW() WHERE 
					id = 
				'<sql:ancestor-value sql:name="resource.id" sql:level="1"/>'
     			</sql:query>
  		</sql:execute-query>
  		</sql:execute-query>
-->
	</updates>
</xsl:template>

</xsl:stylesheet>
