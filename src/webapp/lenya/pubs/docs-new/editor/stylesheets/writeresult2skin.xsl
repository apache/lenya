<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:sch="http://www.ascc.net/xml/schematron" 
 xmlns:zvon="http://zvon.org/schematron"
 xmlns:request="http://xml.apache.org/cocoon/requestgenerator/2.0"
 xmlns:dir="http://apache.org/cocoon/directory/2.0"
 xmlns:source="http://apache.org/cocoon/source/1.0"
 >
 
 <xsl:template match="sourceResult">
    <xsl:apply-templates/>
 </xsl:template> 

<!-- outputs the response from SourceWritingTransformer -->
  <xsl:template match="sourceResult">
		<i>Writing to Source...</i>
 			   <table>
					<tr>
						<td>Result</td>
						<td><xsl:value-of select="execution"/></td>
					</tr>
					<tr>
						<td>Behaviour</td>
						<td><xsl:value-of select="behaviour"/></td>
					</tr>
					<xsl:if test="message">
						<tr>
							<td>Message</td>
							<td><xsl:value-of select="message"/></td>
						</tr>
					</xsl:if>
					<tr>
						<td>Source</td>
						<td><xsl:value-of select="source"/></td>
					</tr>
					<tr>
						<td>Action</td>
						<td>
							<xsl:choose>
								<xsl:when test="action = 'none'">None taken</xsl:when>
								<xsl:when test="action = 'new'">New document created</xsl:when>
								<xsl:when test="action = 'overwritten'">Existing document overwritten</xsl:when>
								<xsl:otherwise>Unknown</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
					<xsl:if test="serializer">
						<tr>
							<td>Serializer</td>
							<td><xsl:value-of select="serializer"/></td>
						</tr>
					</xsl:if>
				</table>
  </xsl:template>


</xsl:stylesheet>
