<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rgrm="http://chaperon.sourceforge.net/grammar/rgrm/1.0"
                xmlns="http://chaperon.sourceforge.net/schema/grammar/1.0">

 <xsl:output indent="yes" method="xml" encoding="US-ASCII"/>

<!-- <xsl:template match="/">
 </xsl:template>-->

 <xsl:template match="rgrm:grammar" >
  <grammar>
   <xsl:attribute name="uri"><xsl:value-of select="substring(rgrm:token_decls/rgrm:uri_decl/rgrm:string,2,
                         string-length(rgrm:token_decls/rgrm:uri_decl/rgrm:string)-2)"/></xsl:attribute>
   <tokens>
    <xsl:apply-templates select="rgrm:token_decls/rgrm:token_decl"/>
   </tokens>

   <ignorabletokens>
    <xsl:apply-templates select="rgrm:token_decls/rgrm:ignorabletoken_decl"/>
   </ignorabletokens>

   <xsl:apply-templates select="rgrm:production_decls"/>

   <xsl:apply-templates select="rgrm:token_decls/rgrm:start_decl"/>
  </grammar>
 </xsl:template>

 <xsl:template match="rgrm:token_decl" >
  <token>
   <xsl:if test="rgrm:token_decl = '%left'">
    <xsl:attribute name="assoc">left</xsl:attribute>
   </xsl:if>
   <xsl:if test="rgrm:token_decl = '%right'">
    <xsl:attribute name="assoc">right</xsl:attribute>
   </xsl:if>
   <xsl:attribute name="tsymbol"><xsl:value-of select="rgrm:id"/></xsl:attribute>
   <xsl:apply-templates select="rgrm:regexexpression"/>
  </token>
 </xsl:template>

 <xsl:template match="rgrm:ignorabletoken_decl" >
  <token>
   <xsl:attribute name="tsymbol"><xsl:value-of select="rgrm:id"/></xsl:attribute>
   <xsl:apply-templates select="rgrm:regexexpression"/>
  </token>
 </xsl:template>

 <xsl:template match="rgrm:regexexpression" >
  <xsl:apply-templates
    select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
            rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|
            rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
 </xsl:template>

 <xsl:template match="rgrm:regexalternation" >
  <alt>
   <xsl:apply-templates
     select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
             rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
  </alt>
 </xsl:template>

 <xsl:template match="rgrm:regexconcatenation" >
  <concat>
   <xsl:apply-templates
     select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
             rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
  </concat>
 </xsl:template>

 <xsl:template match="rgrm:regexquantifier" >
  <xsl:apply-templates
    select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
            rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
 </xsl:template>

 <xsl:template match="rgrm:regexoptional" >
  <concat minOccurs="0" maxOccurs="1">
   <xsl:apply-templates
     select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
             rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
  </concat>
 </xsl:template>

 <xsl:template match="rgrm:regexstar" >
  <concat minOccurs="0" maxOccurs="*">
   <xsl:apply-templates
     select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
             rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
  </concat>
 </xsl:template>

 <xsl:template match="rgrm:regexplus" >
  <concat minOccurs="1" maxOccurs="*">
   <xsl:apply-templates
     select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
             rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
  </concat>
 </xsl:template>

 <xsl:template match="rgrm:regexvar" >
  <xsl:choose>
   <xsl:when test="count(rgrm:regexmultiplicator/rgrm:number)=2">
    <concat>
     <xsl:attribute name="minOccurs"><xsl:value-of select="rgrm:regexmultiplicator/rgrm:number[1]"/></xsl:attribute>
     <xsl:attribute name="maxOccurs"><xsl:value-of select="rgrm:regexmultiplicator/rgrm:number[2]"/></xsl:attribute>
     <xsl:apply-templates
       select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
               rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
    </concat>
   </xsl:when>
   <xsl:otherwise>
    <concat>
     <xsl:attribute name="minOccurs"><xsl:value-of select="rgrm:regexmultiplicator/rgrm:number"/></xsl:attribute>
     <xsl:attribute name="maxOccurs"><xsl:value-of select="rgrm:regexmultiplicator/rgrm:number"/></xsl:attribute>
     <xsl:apply-templates
       select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
               rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexdot|rgrm:regexbol|rgrm:regexeol|rgrm:regexabref"/>
    </concat>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="rgrm:regexterm" >
  <xsl:apply-templates
    select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
            rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexabref"/>
 </xsl:template>

 <xsl:template match="rgrm:regexklammer" >
  <xsl:apply-templates
    select="rgrm:regexalternation|rgrm:regexconcatenation|rgrm:regexklammer|rgrm:regexoptional|rgrm:regexstar|
            rgrm:regexplus|rgrm:regexvar|rgrm:string|rgrm:characterclass|rgrm:negatedcharacterclass|rgrm:regexabref"/>
 </xsl:template>

 <xsl:template match="rgrm:regexdot" >
  <dot/>
 </xsl:template>

 <xsl:template match="rgrm:regexbol" >
  <bol/>
 </xsl:template>

 <xsl:template match="rgrm:regexeol" >
  <eol/>
 </xsl:template>

 <xsl:template match="rgrm:regexabref" >
  <xsl:variable name="ref" select="rgrm:id"/>
  <xsl:apply-templates select="/rgrm:grammar/rgrm:token_decls/rgrm:ab_decl[rgrm:id=$ref]/rgrm:regexexpression"/>
 </xsl:template>

 <!--<xsl:template match="string" >
  <string>
   <xsl:attribute name="content"><xsl:value-of select="translate(normalize-space(substring(., 2, string-length(.)-2)),' ', '')"/></xsl:attribute>
  </string>
 </xsl:template>-->

 <xsl:template match="rgrm:string" >
  <string>
   <xsl:attribute name="content"><xsl:apply-templates select="rgrm:character|rgrm:maskedcharacter" mode="string"/></xsl:attribute>
  </string>
 </xsl:template>

 <xsl:template match="rgrm:characterclass" >
  <cc>
   <xsl:apply-templates select="rgrm:character|rgrm:maskedcharacter|rgrm:intervall"/>
  </cc>
 </xsl:template>

 <xsl:template match="rgrm:negatedcharacterclass" >
  <ncc>
   <xsl:apply-templates select="rgrm:character|rgrm:maskedcharacter|rgrm:intervall"/>
  </ncc>
 </xsl:template>

 <xsl:template match="rgrm:character" >
  <cs>
   <xsl:attribute name="content"><xsl:value-of select="translate(normalize-space(.), ' ', '')"/></xsl:attribute>
  </cs>
 </xsl:template>

 <xsl:template match="rgrm:maskedcharacter" >
  <cs>
   <xsl:choose>
    <xsl:when test="substring(translate(normalize-space(.), ' ', ''), 2,1) = 'n'">
     <xsl:attribute name="content"><xsl:text disable-output-escaping="yes">&#10;</xsl:text></xsl:attribute>
    </xsl:when>
    <xsl:when test="substring(translate(normalize-space(.), ' ', ''), 2,1) = 'r'">
     <xsl:attribute name="content"><xsl:text disable-output-escaping="yes">&#13;</xsl:text></xsl:attribute>
    </xsl:when>
    <xsl:when test="substring(translate(normalize-space(.), ' ', ''), 2,1) = 't'">
     <xsl:attribute name="content"><xsl:text disable-output-escaping="yes">&#9;</xsl:text></xsl:attribute>
    </xsl:when>
    <xsl:when test="contains(.,'\ ')">
     <xsl:attribute name="content"><xsl:text disable-output-escaping="yes">&#32;</xsl:text></xsl:attribute>
    </xsl:when>
    <xsl:otherwise>
     <xsl:attribute name="content"><xsl:value-of select="substring(translate(normalize-space(.), ' ', ''), 2,1)"/></xsl:attribute>
    </xsl:otherwise>
   </xsl:choose>
  </cs>
 </xsl:template>

 <xsl:template match="rgrm:character" mode="string">
  <xsl:value-of select="translate(normalize-space(.), ' ', '')"/>
 </xsl:template>
 
 <xsl:template match="rgrm:maskedcharacter" mode="string">
  <xsl:choose>
    <xsl:when test="substring(translate(normalize-space(.), ' ', ''), 2,1) = 'n'">
     <xsl:text disable-output-escaping="yes">&#10;</xsl:text>
    </xsl:when>
    <xsl:when test="substring(translate(normalize-space(.), ' ', ''), 2,1) = 'r'">
     <xsl:text disable-output-escaping="yes">&#13;</xsl:text>
    </xsl:when>
    <xsl:when test="substring(translate(normalize-space(.), ' ', ''), 2,1) = 't'">
     <xsl:text disable-output-escaping="yes">&#9;</xsl:text>
    </xsl:when>
    <xsl:when test="contains(.,'\ ')">
     <xsl:text disable-output-escaping="yes">&#32;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
     <xsl:value-of select="substring(translate(normalize-space(.), ' ', ''),2,1)"/>
    </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <xsl:template match="rgrm:intervall" >
  <ci>
   <xsl:attribute name="min"><xsl:value-of select="rgrm:character[1]"/></xsl:attribute>
   <xsl:attribute name="max"><xsl:value-of select="rgrm:character[2]"/></xsl:attribute>
  </ci>
 </xsl:template>

 <xsl:template match="rgrm:comment_decl" >
  <comment>
   <xsl:apply-templates select="rgrm:regexexpression"/>
  </comment>
 </xsl:template>

 <xsl:template match="rgrm:whitespace_decl" >
  <whitespace>
   <xsl:apply-templates select="rgrm:regexexpression"/>
  </whitespace>
 </xsl:template>

 <xsl:template match="rgrm:production_decls" >
  <productions>
   <xsl:for-each select="rgrm:production_decl/rgrm:production_defs/rgrm:production_def">
    <production>
     <xsl:attribute name="ntsymbol"><xsl:value-of select="../../rgrm:id"/></xsl:attribute>
     
     <xsl:choose>
      <xsl:when test="rgrm:reducetype_decl[.='%append']">
       <xsl:attribute name="reducetype">append</xsl:attribute>
      </xsl:when>
      <xsl:when test="rgrm:reducetype_decl[.='%resolve']">
       <xsl:attribute name="reducetype">resolve</xsl:attribute>
      </xsl:when>
      <xsl:when test="rgrm:reducetype_decl[.='%neglect']">
       <xsl:attribute name="reducetype">neglect</xsl:attribute>
      </xsl:when>
     </xsl:choose>

     <xsl:if test="rgrm:prec_decl">
      <xsl:attribute name="prec"><xsl:value-of select="rgrm:prec_decl/rgrm:id"/></xsl:attribute>
     </xsl:if>

     <xsl:apply-templates select="rgrm:ids/rgrm:id"/>
    </production>
   </xsl:for-each>
  </productions>
 </xsl:template>

 <xsl:template match="rgrm:id" >
  <xsl:variable name="symbol" select="text()"/>
  <xsl:choose>
   <xsl:when test="/rgrm:grammar/rgrm:token_decls/rgrm:token_decl/rgrm:id[.=$symbol]">
    <tsymbol>
     <xsl:attribute name="name"><xsl:value-of select="."/></xsl:attribute>
    </tsymbol>
   </xsl:when>
   <xsl:otherwise>
    <ntsymbol>
      <xsl:attribute name="name"><xsl:value-of select="."/></xsl:attribute>
    </ntsymbol>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="rgrm:start_decl" >
  <ssymbol>
   <xsl:attribute name="ntsymbol"><xsl:value-of select="rgrm:id"/></xsl:attribute>
  </ssymbol>
 </xsl:template>

</xsl:stylesheet>
