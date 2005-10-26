<!--

   The Contents of this file are made available subject to the terms of
   either of the following licenses

          - GNU Lesser General Public License Version 2.1
          - Sun Industry Standards Source License Version 1.1

   Sun Microsystems Inc., October, 2000

   GNU Lesser General Public License Version 2.1
   =============================================
   Copyright 2000 by Sun Microsystems, Inc.
   901 San Antonio Road, Palo Alto, CA 94303, USA

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License version 2.1, as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston,
   MA  02111-1307  USA


   Sun Industry Standards Source License Version 1.1
   =================================================
   The contents of this file are subject to the Sun Industry Standards
   Source License Version 1.1 (the "License"); You may not use this file
   except in compliance with the License. You may obtain a copy of the
   License at http://www.openoffice.org/license.html.

   Software provided under this License is provided on an "AS IS" basis,
   WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING,
   WITHOUT LIMITATION, WARRANTIES THAT THE SOFTWARE IS FREE OF DEFECTS,
   MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE, OR NON-INFRINGING.
   See the License for the specific provisions governing your rights and
   obligations concerning the Software.

   The Initial Developer of the Original Code is: Sun Microsystems, Inc.

   Copyright © 2002 by Sun Microsystems, Inc.

   All Rights Reserved.

   Contributor(s): _______________________________________

-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 		xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" 
	xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
	xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" 
	xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format" 
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:dc="http://purl.org/dc/elements/1.1/" 
	xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" 
	xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" 
	xmlns:svg="http://www.w3.org/2000/svg" 
	xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" 
	xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" 
	xmlns:math="http://www.w3.org/1998/Math/MathML" 
	xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" 
	xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" 		xmlns:ooo="http://openoffice.org/2004/office" 
	xmlns:ooow="http://openoffice.org/2004/writer" 
	xmlns:oooc="http://openoffice.org/2004/calc" 
	xmlns:dom="http://www.w3.org/2001/xml-events" 
	xmlns:xforms="http://www.w3.org/2002/xforms" 
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        office:class="text"
        office:version="1.0"
        xmlns:config="http://openoffice.org/2001/config"
        xmlns:help="http://openoffice.org/2000/help"
        xmlns:xt="http://www.jclark.com/xt"
	extension-element-prefixes="xt"
        xmlns:system="http://www.jclark.com/xt/java/java.lang.System"
        xmlns:urlencoder="http://www.jclark.com/xt/java/java.net.URLEncoder"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:java="http://xml.apache.org/xslt/java"
        	exclude-result-prefixes="java">



    <!-- *********************************** -->
    <!-- *** build WML / WAP ASCII TABLE *** -->
    <!-- *********************************** -->


    <!--  templates from table.xsl has been dublicated for wml
          instead a condition-check for every cell               -->


    <!-- ********************************************* -->
    <!-- *** write (explicit) repeating table rows *** -->
    <!-- ********************************************* -->

    <xsl:template name="wml-repeat-write-row">
        <xsl:param name="collectedGlobalData"/>
        <xsl:param name="allColumnStyleEntries"/>
        <xsl:param name="number-rows-repeated" select="1"/>
        <xsl:param name="maxRowLength"/>

        <!-- soffice bug workaround for not showing empty cell repeated multiple times [not(child::*)]-->
        <xsl:if test="not(table:table-cell[last()=1])">
            <xsl:choose>
                <!-- write an entry of a row and repeat calling this method until all elements are written out -->
                <xsl:when test="$number-rows-repeated > 1 and (table:table-cell/text() or table:table-cell/*)">
                    <xsl:call-template name="wml-write-row">
                        <xsl:with-param name="collectedGlobalData"       select="$collectedGlobalData"/>
                        <xsl:with-param name="allColumnStyleEntries"    select="$allColumnStyleEntries"/>
                        <xsl:with-param name="maxRowLength"             select="$maxRowLength"/>
                    </xsl:call-template>

                    <xsl:call-template name="wml-repeat-write-row">
                        <xsl:with-param name="collectedGlobalData"       select="$collectedGlobalData"/>
                        <xsl:with-param name="allColumnStyleEntries"    select="$allColumnStyleEntries"/>
                        <xsl:with-param name="maxRowLength"             select="$maxRowLength"/>
                        <xsl:with-param name="number-rows-repeated"     select="$number-rows-repeated - 1"/>
                    </xsl:call-template>
                </xsl:when>
                <!-- write an entry of a row -->
                <xsl:otherwise>
                    <xsl:call-template name="wml-write-row">
                        <xsl:with-param name="collectedGlobalData"       select="$collectedGlobalData"/>
                        <xsl:with-param name="allColumnStyleEntries"    select="$allColumnStyleEntries"/>
                        <xsl:with-param name="maxRowLength"             select="$maxRowLength"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
       </xsl:if>
    </xsl:template>



    <xsl:template name="wml-write-row">
        <xsl:param name="collectedGlobalData"/>
        <xsl:param name="allColumnStyleEntries"/>
        <xsl:param name="maxRowLength"/>

        <xsl:choose>
            <xsl:when test="ancestor::*[contains($wap-paragraph-elements-without-table-row, name())]">
                <!-- the first cell of the row, as a row has only cells <!ELEMENT table:table-row %table-cells;> -->
                <xsl:apply-templates select="table:table-cell">
                    <xsl:with-param name="collectedGlobalData"       select="$collectedGlobalData"/>
                    <xsl:with-param name="allColumnStyleEntries"    select="$allColumnStyleEntries"/>
                    <xsl:with-param name="maxRowLength"             select="$maxRowLength"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="p">
                    <!-- the first cell of the row, as a row has only cells <!ELEMENT table:table-row %table-cells;> -->
                    <xsl:apply-templates select="table:table-cell">
                        <xsl:with-param name="collectedGlobalData"       select="$collectedGlobalData"/>
                        <xsl:with-param name="allColumnStyleEntries"    select="$allColumnStyleEntries"/>
                        <xsl:with-param name="maxRowLength"             select="$maxRowLength"/>
                    </xsl:apply-templates>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
</xsl:stylesheet>

