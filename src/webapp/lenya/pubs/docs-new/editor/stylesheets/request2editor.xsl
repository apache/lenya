<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:request="http://xml.apache.org/cocoon/requestgenerator/2.0">
<!--
    <xsl:include href="generation-utils.xsl"/>                      
    <xsl:variable name="target" select="request:request/request:requestParameters/request:parameter[@name = 'target']/request:value"/>
-->
<!-- 

    This StyleSheet converts the output of the RequestGenerator into the new 'page'
    then wraps it with 'editor' tags.
    
    It looks for double CRs in the submitted text and makes paragraphs from them using the 'para' tag.
    It then looks inside the paragraphs for single CRs and inserts a 'br' tag if found.
    
    All other tags are saved as Entities.

    NB. the indentation of these templates effects the output document
        
-->
   <xsl:template match="/">
      <div>
         <h1>
            <xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'title']/request:value" />
         </h1>

         <tf:textfragment xmlns:tf="http://chaperon.sourceforge.net/schema/textfragment/1.0">
            <xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'content']/request:value" />
         </tf:textfragment>

         <form action="put">
            <input type="hidden" name="title">
               <xsl:attribute name="value">
                  <xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'title']/request:value" />
               </xsl:attribute>
            </input>

            <input type="hidden" name="content">
               <xsl:attribute name="value">
                  <xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'content']/request:value" />
               </xsl:attribute>
            </input>

            <input type="hidden" name="target">
               <xsl:attribute name="value">
                  <xsl:value-of select="request:request/request:requestParameters/request:parameter[@name = 'target']/request:value" />
               </xsl:attribute>
            </input>

            <input type="submit" name="submit" />
         </form>
      </div>
   </xsl:template>
</xsl:stylesheet>

