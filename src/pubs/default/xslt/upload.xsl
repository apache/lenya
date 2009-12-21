<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:import href="upload-generic.xsl"/>
  
  <!--<xsl:template name="title">Import Article (SGML)</xsl:template>-->
  
<xsl:template name="fields">
  <form method="POST" enctype="multipart/form-data" id="form-upload_document">

    <input name="lenya.usecase" type="hidden" value="{$lenya.usecase}"/>
    <input name="lenya.continuation" type="hidden" value="{uc:continuation}"/>

    <!-- DEBUG ... -->
    <!--<input name="cocoon-view" type="hidden" value="xml"/>-->

    <table class="lenya-table-noborder">
    <tr>
      <td class="lenya-entry-caption">Upload Document</td>
      <td>
        <input class="lenya-form-element" name="upload-regulation" type="file"/>
      </td>
    </tr>

    <tr>
      <td/>
      <td>
        <input type="submit" name="submit" value="Upload"/>
        <xsl:text> </xsl:text>
        <input type="submit" value="Cancel" name="input-cancel"/>
      </td>
    </tr>
    </table>
  </form>
</xsl:template>
  
</xsl:stylesheet>
