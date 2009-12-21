<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:svg="http://www.w3.org/2000/svg"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">
  
  <xsl:template match="svg:svg">
    <fo:root>
      
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4-landscape" page-width="297mm" page-height="210mm">
          <fo:region-body margin="2cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      
      <fo:page-sequence master-reference="A4-landscape">
        <fo:flow flow-name="xsl-region-body">
          <fo:block>
            <fo:instream-foreign-object>
              <xsl:copy-of select="."/>
            </fo:instream-foreign-object>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
      
    </fo:root>
    
  </xsl:template>

</xsl:stylesheet>