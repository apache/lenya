
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up"
                xmlns:rc="http://www.lenya.org/2002/rc" >

<xsl:template match="rc:backup">
  <cmsbody>
    <Page>
      <Content>
        <xsl:apply-templates/> 
      </Content>
    </Page>
  </cmsbody> 
</xsl:template>

<xsl:template match="* | @*">
 <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>
                                                                                                                                            
</xsl:stylesheet>

