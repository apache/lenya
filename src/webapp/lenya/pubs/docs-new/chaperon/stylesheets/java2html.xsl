<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: java2html.xsl,v 1.2 2004/03/13 12:49:05 gregor Exp $ -->
    
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:java="http://chaperon.sourceforge.net/grammar/java/1.0"
                xmlns="http://www.w3.org/1999/xhtml">

 <xsl:output indent="no"/>

 <!--<xsl:strip-space elements="*"/>-->
 <xsl:param name="selected">MethodDeclaration</xsl:param>

 <xsl:template match="java:CompilationUnit" >
  <html>
   <head>
    <link rel="stylesheet" type="text/css" href="java.css" title="Style"/>
    <title>java2html example</title>
   </head>
   <body>
    <table id="Header" border="0" cellpadding="0" cellspacing="0" width="100%">
     <tr>
      <td colspan="2" width="33%">&#160;</td>
      <td align="center" colspan="2" width="33%">
       <font size="4">java2html example</font>
      </td>
      <td align="right" colspan="2" width="33%"></td>
     </tr>
    </table>
<!--    <p>
     <form>
      <select size="1" name="selected">
       <option value=""><xsl:if test="$selected=''"><xsl:attribute name="selected"/></xsl:if>Nothing selected</option>
       <option value="QualifiedName"><xsl:if test="$selected='QualifiedName'"><xsl:attribute name="selected"/></xsl:if>Qualified name</option>
       <option value="FieldVariableDeclaration"><xsl:if test="$selected='FieldVariableDeclaration'"><xsl:attribute name="selected"/></xsl:if>Field variable declaration</option>
       <option value="MethodDeclaration"><xsl:if test="$selected='MethodDeclaration'"><xsl:attribute name="selected"/></xsl:if>Method declaration</option>
       <option value="SelectionStatement"><xsl:if test="$selected='SelectionStatement'"><xsl:attribute name="selected"/></xsl:if>Selection statement</option>
       <option value="IterationStatement"><xsl:if test="$selected='IterationStatement'"><xsl:attribute name="selected"/></xsl:if>Iteration statement</option>
       <option value="GuardingStatement"><xsl:if test="$selected='GuardingStatement'"><xsl:attribute name="selected"/></xsl:if>Guarding statement</option>
      </select>
     </form>
    </p>-->
    <pre id="Classes">
     <xsl:apply-templates/>
    </pre>
   </body>
  </html>
 </xsl:template>

 <xsl:template match="java:ABSTRACT">
  <font id="ABSTRACT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:BOOLEAN">
  <font id="BOOLEAN"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:BREAK">
  <font id="BREAK"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:BYTE">
  <font id="BYTE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:CASE">
  <font id="CASE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:CATCH">
  <font id="CATCH"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:CHAR">
  <font id="CHAR"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:CLASS">
  <font id="CLASS"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:CONST">
  <font id="CONST"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:CONTINUE">
  <font id="CONTINUE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:DEFAULT">
  <font id="DEFAULT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:DO">
  <font id="DO"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:DOUBLE">
  <font id="DOUBLE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:ELSE">
  <font id="ELSE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:EXTENDS">
  <font id="EXTENDS"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:FALSE">
  <font id="FALSE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:FINAL">
  <font id="FINAL"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:FINALLY">
  <font id="FINALLY"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:FLOAT">
  <font id="FLOAT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:FOR">
  <font id="FOR"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:GOTO">
  <font id="GOTO"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:IF">
  <font id="IF"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:IMPLEMENTS">
  <font id="IMPLEMENTS"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:IMPORT">
  <font id="IMPORT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:INSTANCEOF">
  <font id="INSTANCEOF"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:INT">
  <font id="INT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:INTERFACE">
  <font id="INTERFACE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:LONG">
  <font id="LONG"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:NATIVE">
  <font id="NATIVE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:NEW">
  <font id="NEW"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:PACKAGE">
  <font id="PACKAGE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:PRIVATE">
  <font id="PRIVATE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:PROTECTED">
  <font id="PROTECTED"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:PUBLIC">
  <font id="PUBLIC"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:RETURN">
  <font id="RETURN"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:SHORT">
  <font id="SHORT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:STATIC">
  <font id="STATIC"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:SUPER">
  <font id="SUPER"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:SWITCH">
  <font id="SWITCH"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:SYCHRONIZED">
  <font id="SYCHRONIZED"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:THIS">
  <font id="THIS"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:THROW">
  <font id="THROW"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:THROWS">
  <font id="THROWS"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:TRANSIENT">
  <font id="TRANSIENT"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:TRUE">
  <font id="TRUE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:TRY">
  <font id="TRY"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:VOID">
  <font id="VOID"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:VOLATILE">
  <font id="VOLATILE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:WHILE">
  <font id="WHILE"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:LITERAL">
  <font id="LITERAL"><xsl:apply-templates/></font>
 </xsl:template>

 <xsl:template match="java:IDENTIFIER">
  <font id="IDENTIFIER"><xsl:apply-templates/></font> 
 </xsl:template>

 <xsl:template match="java:DOPEN">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:DCLOSE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:COPEN">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:CCLOSE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:BOPEN">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:BCLOSE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:SEMICOLON">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:COMMA">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:DOT">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_EQ">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_LE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_GE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_NE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_LOR">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_LAND">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_INC">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_DEC">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_SHR">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_SHL">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OP_SHRR">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ASS_OP">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:EQ">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:GT">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LT">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:NOT">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:TILDE">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:QM">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:COLON">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:PLUS">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MINUS">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MULT">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:DIV">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:AND">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:OR">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:XOR">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MOD">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:BOOLLIT">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:eol">
  <xsl:text>
</xsl:text>
 </xsl:template>

 <xsl:template match="java:whitespace">
  <!--<xsl:text><xsl:value-of select="."/></xsl:text>-->
  <xsl:value-of select="."/>
 </xsl:template>

 <xsl:template match="java:comment">
  <font id="MultiLineComment"><xsl:value-of select="."/></font>
 </xsl:template>

 <xsl:template match="java:TypeSpecifier">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:TypeName">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ClassNameList">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:PrimitiveType">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:SemiColons">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ProgramFile">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:PackageStatement">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:TypeDeclarations">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:TypeDeclarationOptSemi">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ImportStatements">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ImportStatement">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:QualifiedName">
  <xsl:choose>
   <xsl:when test="$selected=local-name(.)">
    <span class="selected"><xsl:apply-templates/></span>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>
  
 <xsl:template match="java:TypeDeclaration">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ClassHeader">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:Modifiers">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:Modifier">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ClassWord">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:Interfaces">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:FieldDeclarations">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:FieldDeclarationOptSemi">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:FieldDeclaration">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:FieldVariableDeclaration">
  <xsl:choose>
   <xsl:when test="$selected=local-name(.)">
    <span class="selected"><xsl:apply-templates/></span>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>
  
 <xsl:template match="java:VariableDeclarators">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:VariableDeclarator">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:VariableInitializer">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ArrayInitializers">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:MethodDeclaration">
  <xsl:choose>
   <xsl:when test="$selected=local-name(.)">
    <span class="selected"><xsl:apply-templates/></span>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>
  
 <xsl:template match="java:MethodDeclarator">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:ParameterList">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:Parameter">
  <xsl:apply-templates/>
 </xsl:template>
  
 <xsl:template match="java:DeclaratorName">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Throws">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MethodBody">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ConstructorDeclaration">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ConstructorDeclarator">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:StaticInitializer">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:NonStaticInitializer">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Extends">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Block">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LocalVariableDeclarationsAndStatements">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LocalVariableDeclarationOrStatement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LocalVariableDeclarationStatement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Statement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:EmptyStatement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LabelStatement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ExpressionStatement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:SelectionStatement">
  <xsl:choose>
   <xsl:when test="$selected=local-name(.)">
    <span class="selected"><xsl:apply-templates/></span>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="java:IterationStatement">
  <xsl:choose>
   <xsl:when test="$selected=local-name(.)">
    <span class="selected"><xsl:apply-templates/></span>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="java:ForInit">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ForExpr">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ForIncr">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ExpressionStatements">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:JumpStatement">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:GuardingStatement">
  <xsl:choose>
   <xsl:when test="$selected=local-name(.)">
    <span class="selected"><xsl:apply-templates/></span>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match="java:Catches">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Catch">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:CatchHeader">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Finally">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:PrimaryExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:NotJustName">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ComplexPrimary">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ComplexPrimaryNoParenthesis">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ArrayAccess">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:FieldAccess">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MethodCall">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MethodAccess">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:SpecialName">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ArgumentList">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:NewAllocationExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:PlainNewAllocationExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ClassAllocationExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ArrayAllocationExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:DimExprs">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:DimExpr">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Dims">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:PostfixExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:RealPostfixExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:UnaryExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LogicalUnaryExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:LogicalUnaryOperator">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ArithmeticUnaryOperator">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:CastExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:PrimitiveTypeExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ClassTypeExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:MultiplicativeExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:AdditiveExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ShiftExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:RelationalExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:EqualityExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:AndExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ExclusiveOrExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:InclusiveOrExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ConditionalAndExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ConditionalOrExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ConditionalExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:AssignmentExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:AssignmentOperator">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:Expression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="java:ConstantExpression">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="@*|*|text()|processing-instruction()" priority="-1">
  <xsl:copy>
   <xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
  </xsl:copy>
 </xsl:template>

</xsl:stylesheet>
