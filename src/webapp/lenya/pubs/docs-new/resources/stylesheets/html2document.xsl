<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method = "xml"  
                version="1.0"
                omit-xml-declaration="no" 
                indent="no"
                encoding="ISO-8859-1"
                doctype-system="document-v11.dtd"
                doctype-public="-//APACHE//DTD Documentation V1.1//EN" />

    <xsl:key name="h2s" match="h2" use="generate-id(preceding-sibling::h1[1])"/>
    <xsl:key name="h3s" match="h3" use="generate-id(preceding-sibling::h2[1])"/>
    <xsl:key name="h4s" match="h4" use="generate-id(preceding-sibling::h3[1])"/>
    <xsl:key name="h5s" match="h5" use="generate-id(preceding-sibling::h4[1])"/>
    <xsl:key name="h6s" match="h6" use="generate-id(preceding-sibling::h5[1])"/>

    <xsl:template match="/">
     <xsl:choose>
   	   <xsl:when test="name(child::node())='html'">
         <xsl:apply-templates/>
	   </xsl:when>
	     
	   <xsl:otherwise>
	     <document>
	      <header><title>Error in conversion</title></header>
	      <body>
	       <warning>This file is not in a html format, please convert manually.</warning>
	      </body>
	     </document>
	   </xsl:otherwise>
     </xsl:choose>
    </xsl:template>
           
    <xsl:template match="html">
        <document>
            <xsl:apply-templates/>
        </document>
    </xsl:template>

    <xsl:template match="head">
        <header>
            <xsl:apply-templates/>
        </header>
    </xsl:template>
        
    <xsl:template match="meta">
      <xsl:text disable-output-escaping = "yes"><![CDATA[ <!-- ]]></xsl:text>
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
      <xsl:text disable-output-escaping = "yes"><![CDATA[ --> ]]></xsl:text> 
    </xsl:template>  
    
    <!--infer structure from sibling headings-->
    <xsl:template match="body">
       <body>
         <xsl:for-each select="h1">
           <section>
             <title><xsl:apply-templates/></title>
             <xsl:apply-templates select="following-sibling::*[1]" mode="next"/>
             <xsl:for-each select="key('h2s',generate-id(.))">
               <section>
                 <title><xsl:apply-templates/></title>
                 <xsl:apply-templates select="following-sibling::*[1]" mode="next"/>
                 <xsl:for-each select="key('h3s',generate-id(.))">
                   <section>
                     <title><xsl:apply-templates/></title>
                     <xsl:apply-templates select="following-sibling::*[1]"
                                          mode="next"/>
                     <xsl:for-each select="key('h4s',generate-id(.))">
                       <section>
                         <title><xsl:apply-templates/></title>
                         <xsl:apply-templates select="following-sibling::*[1]"
                                              mode="next"/>
                         <xsl:for-each select="key('h5s',generate-id(.))">
                           <section>
                             <title><xsl:apply-templates/></title>
                             <xsl:apply-templates select="following-sibling::*[1]"
                                                  mode="next"/>
                             <xsl:for-each select="key('h6s',generate-id(.))">
                               <section>
                                 <title><xsl:apply-templates/></title>
                              <xsl:apply-templates select="following-sibling::*[1]" mode="next"/>
                               </section>
                             </xsl:for-each>
                           </section>
                         </xsl:for-each>
                       </section>
                     </xsl:for-each>
                   </section>
                 </xsl:for-each>
               </section>
             </xsl:for-each>
           </section>
         </xsl:for-each>
       </body>
    </xsl:template>

    <!--process each sibling in order until the next heading level-->

    <xsl:template match="*" mode="next">
       <xsl:if test="not( translate( local-name(.),'123456','' ) = 'h' )">
         <xsl:apply-templates select="."/>
         <xsl:apply-templates select="following-sibling::*[1]" mode="next"/>
       </xsl:if>
    </xsl:template>
      
    <xsl:template match="P|p">
        <p>
          <xsl:apply-templates/>
        </p>
    </xsl:template>
    
    <xsl:template match="img">
       <xsl:choose>
    	<xsl:when test="name(..)='section'">
          <figure alt="{@alt}" src= "{@src}"/>
    	</xsl:when>
    	<xsl:otherwise>
          <img alt="{@alt}" src= "{@src}"/>
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
    
    <xsl:template match="source|blockquote">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	  <code>
    	    <xsl:value-of select="." />
    	  </code> 
    	</xsl:when>
      
    	<xsl:otherwise>
    	  <source>
    	    <xsl:value-of select="." />
    	  </source> 
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>

  
    <!-- convert a to link -->
    <xsl:template match="a">
      <xsl:if test="@name">
        <!-- Attach an id to the current node -->
        <xsl:attribute name="id"><xsl:value-of select="translate(@name, ' $', '__')"/></xsl:attribute>
        <xsl:apply-templates/>
      </xsl:if>
      <xsl:if test="@href">
        <link href="{@href}">
          <xsl:apply-templates/>
        </link>
      </xsl:if>
    </xsl:template>
    
    <xsl:template match="@valign | @align"/>
        
    <xsl:template match="center">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	    <xsl:apply-templates/>
    	</xsl:when>
      
    	<xsl:otherwise>
    	  <p>
    	    <xsl:apply-templates/>
    	  </p> 
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>

    <xsl:template match="ol">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	   <xsl:text disable-output-escaping="yes"><![CDATA[</p>]]></xsl:text>
    	    <ol>
    	     <xsl:apply-templates/>
    	    </ol>
    	   <xsl:text disable-output-escaping="yes"><![CDATA[<p>]]></xsl:text>
    	</xsl:when>
      	<xsl:otherwise>
    	    <ol>
    	     <xsl:apply-templates/>
    	    </ol>
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
    
    <xsl:template match="ul">
      <xsl:choose>
    	<xsl:when test="name(..)='p'">
    	   <xsl:text disable-output-escaping="yes"><![CDATA[</p>]]></xsl:text>
    	    <ul>
    	     <xsl:apply-templates/>
    	    </ul>
    	   <xsl:text disable-output-escaping="yes"><![CDATA[<p>]]></xsl:text>
    	</xsl:when>
      	<xsl:otherwise>
    	    <ul>
    	     <xsl:apply-templates/>
    	    </ul>
    	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>
        
    <xsl:template match="b">
      <strong>
        <xsl:value-of select = "."/>
      </strong>
    </xsl:template>
    
    <xsl:template match="i">
      <em>
        <xsl:value-of select = "."/>
      </em>
    </xsl:template>

    <xsl:template match="u">
      <u>
        <xsl:value-of select = "."/>
      </u>
    </xsl:template>
    
    <xsl:template match="table">
      <table>
            <xsl:apply-templates select="node()"/>
      </table>
    </xsl:template>
    
            
    <xsl:template match="br">
      <xsl:choose>
	    <xsl:when test="normalize-space(text())">
	    	    
		  <xsl:choose>
		    <xsl:when test="name(..)='p'">
		        <xsl:apply-templates/>
		      <br/> 
		    </xsl:when>
	  	    <xsl:otherwise>
		      <p>
	            <xsl:apply-templates/>
		      </p>
		    </xsl:otherwise>
	       </xsl:choose>
	      
	    </xsl:when>
  	    <xsl:otherwise>
	      <br/>
	    </xsl:otherwise>
       </xsl:choose>
    </xsl:template>
    
    <!-- Strip -->
    <xsl:template match="font|div|big">
      <xsl:apply-templates/>
    </xsl:template>


    <xsl:template match="span">
		  <xsl:choose>
		    <xsl:when test="contains(@style,'bold')">
		       <strong>
		        <xsl:apply-templates/>
		       </strong>
		    </xsl:when>
		    <xsl:when test="contains(@style,'italic')">
		       <em>
		        <xsl:apply-templates/>
		       </em>
		    </xsl:when>
		    <xsl:when test="contains(@style,'underline')">
		       <u>
		        <xsl:apply-templates/>
		       </u>
		    </xsl:when>
	  	    <xsl:otherwise>
	  	        <!-- Strip -->
	            <xsl:apply-templates/>
		    </xsl:otherwise>
	       </xsl:choose>
    
      <xsl:apply-templates/>
    </xsl:template>
        

                
    <xsl:template match="node()|@*" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
