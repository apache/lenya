<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    <xsl:param name="docid"/>
    <xsl:param name="form"/>
    <xsl:param name="message"/>
    
    <xsl:template match="/">
        <form>
            <docid><xsl:value-of select="$docid"/></docid>
            <ftype><xsl:value-of select="$form"/></ftype>
            <xsl:if test="$message">
                <message><xsl:value-of select="$message"/></message>
            </xsl:if>
            <xsl:apply-templates/>
        </form>
    </xsl:template>
    
    <!-- NOTE: Mixed content is currently being handled by copy-mixed-content.xsl, which is being called by form-layout.xsl -->

</xsl:stylesheet>
