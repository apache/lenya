<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: transformxsl.xsl,v 1.4 2002/11/17 16:48:14 felixcms Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 

xmlns:xhtml="http://www.w3.org/1999/xhtml"

>
<xsl:output method="xml" encoding="iso-8859-1" />


<xsl:template match="/">

	<xsl:apply-templates/>

</xsl:template>
<xsl:template match="img|A|a|font|br|ul|li|ol|table|tr|td|hr|form|input|textarea|select|option|span|embed|i|b|h1|h2|h3|h4|h5|h6|pre|code|p|sub|sup|br|center">
    <xsl:element name="xhtml:{name()}">
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
            <xsl:apply-templates/>

    </xsl:element>
</xsl:template>

<xsl:template match="*">
    <xsl:element name="{name()}">

        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
            <xsl:apply-templates/>

	  </xsl:element>
</xsl:template>

<xsl:template match="xsl:element|xsl:output|xsl:template|xsl:for-each|xsl:copy">
    <xsl:copy>

        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
            <xsl:apply-templates/>

	  </xsl:copy>
</xsl:template>

<xsl:template match="xsl:apply-templates|xsl:value-of">
<!-- mozilla under windows does not correctly apply templates in generated xsl-documents
for example

<xsl:apply-templates select="node"/>

selects the . node :(
so we translate this to

<xsl:for-each select="node">
<xsl:apply-templates select="."/>
</xsl:for-each>
-->
<xsl:choose>
	<xsl:when test="contains(@select,'node(') or not(@select) or @select = 'name()'"><xsl:call-template name="printDivOrSpan"/></xsl:when>
	<xsl:otherwise>
		<xsl:element name="xsl:for-each">
			<xsl:attribute name="select"><xsl:value-of select="@select"/></xsl:attribute>
			<xsl:call-template name="printDivOrSpan">
            	<xsl:with-param name="noSelectAttr" select="'yes'"/>
            </xsl:call-template>
		</xsl:element>    
    </xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template match="*[@contentEditable = 'true']">
<!-- this lines produce:
		<name() name="bitfluxspan" onmousedown="BX_focusSpan(this);" class="border">			-->

	<xsl:copy>
    	
           <xsl:for-each select="@*">
            	<xsl:copy/>
           </xsl:for-each>

    	<xsl:attribute name="name">bitfluxspan</xsl:attribute>

<!--      	<xsl:attribute name="nodename">{name()}</xsl:attribute>        -->

<!--	   	<xsl:attribute name="style">border-width: thin; border-style: dotted; border-color: #CCCCCC;</xsl:attribute>       -->
<!-- this line produce:
<xsl:attribute name="id" ><xsl:call-template name="generateID"><xsl:with-param name="selectNode" select="..ID.."></xsl:call-template></xsl:attribute>
-->

		<xsl:element name="xsl:attribute">
			<xsl:attribute name="name">id</xsl:attribute>
		    <xsl:element name="xsl:call-template">
    			<xsl:attribute name="name">generateID</xsl:attribute>
				<xsl:element name="xsl:with-param">
        	    	<xsl:attribute name="name">selectNode</xsl:attribute>
            	    <xsl:attribute name="select">
                	    <xsl:choose>
						    <xsl:when test="descendant::*[@select]/@select">
							    <xsl:value-of select="descendant::*[@select]/@select"/>
						    </xsl:when>
					        <xsl:otherwise>.</xsl:otherwise>
					    </xsl:choose>        
            	    </xsl:attribute>
		        </xsl:element>
		    </xsl:element>
		</xsl:element>

	<xsl:text>
	</xsl:text>
	
	<xsl:apply-templates/>

</xsl:copy>

</xsl:template>


<xsl:template name="printDivOrSpan">
<xsl:param name="noSelectAttr"/>
<xsl:choose>
	<xsl:when test="contains(@select,'node(') or @select='name()'">
       <xsl:copy>
           <xsl:for-each select="@*">
            	<xsl:copy/>
           </xsl:for-each>
           <xsl:apply-templates/>
       </xsl:copy>
	</xsl:when>
    <xsl:otherwise>
        
	    <xsl:copy>
	        <xsl:for-each select="@*">
            <xsl:choose>
        	   	 <xsl:when test="not($noSelectAttr = 'yes' and name() = 'select')">
    		        <xsl:copy/>
	             </xsl:when>
            	 <xsl:otherwise>
    		         <xsl:attribute name="select">.</xsl:attribute>
	             </xsl:otherwise>
             </xsl:choose>
	        </xsl:for-each>
	    </xsl:copy>
    	    <xsl:apply-templates/>

    </xsl:otherwise>
 </xsl:choose>   
</xsl:template>

<xsl:template match="xsl:stylesheet">
<xsl:copy>

        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
        <xsl:attribute name="xmlns:xhtml">http://www.w3.org/1999/xhtml</xsl:attribute>		
     <!--   <xsl:attribute name="xmlns">http://wwlallow.w3.org/1999/html</xsl:attribute>-->
        <xsl:apply-templates/>

<!--
the following lines mean :

<xsl:template name="generateID">
<xsl:param name="selectNode" />
<xsl:choose>
    	<xsl:when test="$selectNode/@id">
        <xsl:value-of select="$selectNode/@id"/>
        </xsl:when>
	<xsl:when test="string-length($selectNode) = 0">
		<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
	</xsl:when>
	<xsl:otherwise><xsl:value-of select="generate-id($selectNode)"/></xsl:otherwise>

</xsl:choose>
</xsl:template>        
-->

     <xsl:element name="xsl:template">
	     <xsl:attribute name="name">generateID</xsl:attribute>
	     <xsl:element name="xsl:param">
    	     <xsl:attribute name="name">selectNode</xsl:attribute>
         </xsl:element>
	     <xsl:element name="xsl:choose">
    	     <xsl:element name="xsl:when">
        	     <xsl:attribute name="test">$selectNode/@id</xsl:attribute>
                 <xsl:element name="xsl:value-of">
	        	     <xsl:attribute name="select">$selectNode/@id</xsl:attribute>
                 </xsl:element>
           	 </xsl:element>                 
    	     <xsl:element name="xsl:when">
        	     <xsl:attribute name="test">string-length($selectNode) = 0</xsl:attribute>
                 <xsl:element name="xsl:value-of">
	        	     <xsl:attribute name="select">generate-id(.)</xsl:attribute>
                 </xsl:element>
           	 </xsl:element>                 
	       	 <xsl:element name="xsl:otherwise">
                 <xsl:element name="xsl:value-of">
	        	     <xsl:attribute name="select">generate-id($selectNode)</xsl:attribute>
                 </xsl:element>
	         </xsl:element>                 
	      </xsl:element>      
	   
     </xsl:element>

</xsl:copy>

</xsl:template>


</xsl:stylesheet>

