<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:xhtml="http://www.w3.org/1999/xhtml"
>
<!-- Stylesheet which Bitflux uses to convert elements after loading the xml
     Is necessary in cases where you have multiple elements with the same name
     because Bitflux can only handle elements with unique names
-->


<!-- Images can only be displayed having the img tag -->
<!-- For article images -->
<xsl:template match="body/media/media-reference">
        <!-- this is usz/bitflux cms related... maybe we should include it from another dir (wysiwyg_config)-->
    <xsl:copy>
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>

        <xsl:call-template name="generateID"/>
        <xhtml:img src="/lenya/computerworld/authoring/img/news/{@source}" bxe_temporaryelement="yes" bxe_internalid="yes" id="img_{generate-id()}">
<!--    The following is from Unipublic
        <xhtml:img src="{$url-back}/{@source}" bxe_temporaryelement="yes" bxe_internalid="yes" id="img_{generate-id()}">
-->

            </xhtml:img>        
        <xsl:apply-templates />
    </xsl:copy>
</xsl:template>


<!-- Images can only be displayed having the img tag -->
<!-- for small preview images (frontpage) -->
<xsl:template match="item/media/media-reference">
        <!-- this is usz/bitflux cms related... maybe we should include it from another dir (wysiwyg_config)-->
    <xsl:copy>
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>

        <xsl:call-template name="generateID"/>
        <xhtml:img src="/lenya/computerworld/authoring/img/{@source}" bxe_temporaryelement="yes" bxe_internalid="yes" id="img_{gen
erate-id()}">
<!--    The following is from Unipublic
        <xhtml:img src="{$url-back}/{@source}" bxe_temporaryelement="yes" bxe_internalid="yes" id="img_{generate-id()}">
-->

            </xhtml:img>
        <xsl:apply-templates />
    </xsl:copy>

</xsl:template>  

</xsl:stylesheet>
