<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
    <xsl:template match="skinconfig">

  <xsl:if test="not(colors)">
  <colors>
    <color name="header" value="#294563"/>

    <color name="tab-selected" value="#4a6d8c"/>
    <color name="tab-unselected" value="#b5c7e7"/>
    <color name="subtab-selected" value="#4a6d8c"/>
    <color name="subtab-unselected" value="#4a6d8c"/>

    <color name="heading" value="#294563"/>
    <color name="subheading" value="#4a6d8c"/>
        
    <color name="navstrip" value="#cedfef"/>
    <color name="toolbox" value="#294563"/>
    
    <color name="menu" value="#4a6d8c"/>    
    <color name="dialog" value="#4a6d8c"/>
            
    <color name="body" value="#ffffff"/>
    
    <color name="table" value="#7099C5"/>    
    <color name="table-cell" value="#f0f0ff"/>    
    <color name="highlight" value="#ffff00"/>
    <color name="fixme" value="#cc6600"/>
    <color name="note" value="#006699"/>
    <color name="warning" value="#990000"/>
    <color name="code" value="#a5b6c6"/>
        
    <color name="footer" value="#cedfef"/>
  </colors>
  </xsl:if>


  <xsl:if test="not(extra-css)">
    <extra-css>
    </extra-css>
  </xsl:if>
  <xsl:if test="not(credits)">
   <credits>
    <credit>
      <name>Built with Apache Forrest</name>
      <url>http://xml.apache.org/forrest/</url>
      <image>images/built-with-forrest-button.png</image>
      <width>88</width>
      <height>31</height>
    </credit>
    <!-- A credit with @role='pdf' will have its name and url displayed in the
    PDF page's footer. -->
  </credits>     
  </xsl:if>

     <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="node()"/> 
<!--      <xsl:copy-of select="node()[not(name(.)='colors')]"/>     
      <xsl:apply-templates select="colors"/>-->
     </xsl:copy> 

    </xsl:template>

    <xsl:template match="colors">
    <colors>
     <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:copy-of select="node()[name(.)='color']"/> 
      
     <xsl:if test="not(color[@name='header'])">
       <color name="header" value="#294563"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='tab-selected'])">
      <color name="tab-selected" value="#4a6d8c"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='tab-unselected'])">
      <color name="tab-unselected" value="#b5c7e7"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subtab-selected'])">
      <color name="subtab-selected" value="#4a6d8c"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subtab-unselected'])">
      <color name="subtab-unselected" value="#4a6d8c"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='heading'])">
      <color name="heading" value="#294563"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='subheading'])">
      <color name="subheading" value="#4a6d8c"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='navstrip'])">
      <color name="navstrip" value="#cedfef"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='toolbox'])">
       <color name="toolbox" value="#294563"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='border'])">
       <color name="border" value="#a5b6c6"/>
     </xsl:if>       
     <xsl:if test="not(color[@name='menu'])">
       <color name="menu" value="#4a6d8c"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='dialog'])">
      <color name="dialog" value="#4a6d8c"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='body'])">
      <color name="body" value="#ffffff"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='table'])">
      <color name="table" value="#7099C5"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='table-cell'])">
      <color name="table-cell" value="#f0f0ff"/>    
     </xsl:if>  
     <xsl:if test="not(color[@name='highlight'])">
       <color name="highlight" value="#ffcc00"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='fixme'])">
       <color name="fixme" value="#c60"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='note'])">
       <color name="note" value="#069"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='warning'])">
       <color name="warning" value="#900"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='code'])">
       <color name="code" value="#CFDCED"/>
     </xsl:if>  
     <xsl:if test="not(color[@name='footer'])">
       <color name="footer" value="#cedfef"/>
     </xsl:if>  
    
     </xsl:copy> 
</colors>
    </xsl:template>
    
</xsl:stylesheet>
