<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:text="http://chaperon.sourceforge.net/schema/text/1.0"
  xmlns:lex="http://chaperon.sourceforge.net/schema/lexemes/2.0"
  xmlns:mon="http://apache.org/lenya/monitoring/1.0">
  
  <xsl:template match="/text:text">
    <mon:log>
      <xsl:attribute name="max">
        <xsl:for-each select="lex:lexeme">
          <xsl:sort select="lex:group[5]" data-type="number" order="ascending"/>
          <xsl:if test="position() = last()">
            <xsl:value-of select="lex:group[5]"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:attribute>
      <xsl:apply-templates select="lex:lexeme"/>
    </mon:log>
  </xsl:template>
  
  
  <xsl:template match="lex:lexeme[@symbol='logEntry']">
    <mon:entry hour="{lex:group[2]}" min="{lex:group[3]}" sec="{lex:group[4]}" sessions="{lex:group[5]}"/>
  </xsl:template>
  
  
</xsl:stylesheet>