<?xml version="1.0" encoding="iso-8859-1"?>


<!--
	This stylesheet converts a notification message to a named stylesheet.
-->


<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslt="http://apache.org/cocoon/lenya/xslt/1.0"
    xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    >
    
<xsl:namespace-alias stylesheet-prefix="xslt" result-prefix="xsl"/>
    
<xsl:template match="/">
	<xslt:stylesheet exclude-result-prefixes="not">
		<xsl:apply-templates/>
	</xslt:stylesheet>
</xsl:template>
		
		
<xsl:template match="/not:notification[not(@enabled = 'true')]">
	
	<xslt:template match="not:notification"/>
	<xslt:template match="not:notification-subject"/>
	<xslt:template match="not:notification-comment"/>
		
	<xslt:template match="@*|node()">
		<xslt:copy><xslt:apply-templates select="@*|node()"/></xslt:copy>
	</xslt:template>
 
</xsl:template>
	
	
<xsl:template match="/not:notification[@enabled = 'true']">
	
	<xslt:template match="not:notification">
		<input type="hidden" name="properties.mail.subject"
			value="{not:message/not:subject}"/>
		<div class="lenya-box">
			<div class="lenya-box-title">Notification</div>
			<div class="lenya-box-body">
			<table class="lenya-table-noborder">
				<tr>
					<td class="lenya-entry-caption">Recipient:</td>
					<td>
						<xslt:apply-templates select="not:users"/>
					</td>
				</tr>
				<tr>
					<td class="lenya-entry-caption">Comment:</td>
					<td>
						<textarea name="properties.mail.message" class="lenya-form-element">
							<xsl:value-of select="not:message/not:body"/>
							&#160;
						</textarea>
					</td>
				</tr>
			</table>	
			</div>
		</div>
	</xslt:template>
	
	<xslt:template match="not:users">
		<xslt:choose>
			<xslt:when test="count(not:user) &gt; 1">
				<select name="properties.mail.tolist" class="lenya-form-element">
					<xslt:apply-templates select="not:user" mode="multiple"/>
				</select>
			</xslt:when>
			<xslt:otherwise>
				<xslt:apply-templates select="not:user" mode="single"/>
			</xslt:otherwise>
		</xslt:choose>
	</xslt:template>
	
	<xslt:template match="not:user" mode="multiple">
		<option>
			<xslt:attribute name="value"><xslt:value-of select="@email"/></xslt:attribute>
			<xslt:value-of select="@id"/>
			<xslt:if test="@name != ''">&#160;(<xslt:value-of select="@name"/>)</xslt:if>
		</option>
	</xslt:template>
	
	<xslt:template match="not:user" mode="single">
		<input type="hidden" name="properties.mail.tolist">
			<xslt:attribute name="value"><xslt:value-of select="@email"/></xslt:attribute>
		</input> 
		<span style="white-space: nobreak">
			<xslt:value-of select="@id"/>
			<xslt:if test="@name != ''">&#160;(<xslt:value-of select="@name"/>)</xslt:if>
		</span>
	</xslt:template>

	<xslt:template match="@*|node()">
		<xslt:copy><xslt:apply-templates select="@*|node()"/></xslt:copy>
	</xslt:template>
 
</xsl:template>
    

</xsl:stylesheet>
