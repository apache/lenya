<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:xhtml="http://www.w3.org/1999/xhtml"
>
<!-- Stylesheet which Bitflux uses to convert elements after loading the xml
     Is necessary in cases where you have multiple elements with the same name
     because Bitflux can only handle elements with unique names
-->

<!-- Because there are two elements "block" in body and in related-content -->
  <xsl:template match="related-content/block">
	  <xsl:element name="rcblock">
	  	<xsl:for-each select="@*">
	        <xsl:copy/>
	      </xsl:for-each>
	 <xsl:call-template name="generateID"/>


		<xsl:apply-templates/>
	</xsl:element>
	
  </xsl:template>

<!-- Images can only be displayed having the img tag -->
<xsl:template match="media-reference">
        <!-- this is usz/bitflux cms related... maybe we should include it from another dir (wysiwyg_config)-->
    <xsl:copy>
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>

        <xsl:call-template name="generateID"/>
        <xhtml:img src="{$url-back}/{@source}" bxe_temporaryelement="yes" bxe_internalid="yes" id="img_{generate-id()}">


            </xhtml:img>        
        <xsl:apply-templates />
    </xsl:copy>

</xsl:template>  

<!-- Because there are two elements called "title" in Dossier -->
  <xsl:template match="head/title">
          <xsl:element name="dos_title">
                <xsl:for-each select="@*">
                <xsl:copy/>
              </xsl:for-each>
         <xsl:call-template name="generateID"/>


                <xsl:apply-templates/>
        </xsl:element>

  </xsl:template>


</xsl:stylesheet>
