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

<!-- $Id: form.xsl,v 1.13 2004/03/13 12:42:05 gregor Exp $ -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    <xsl:param name="docid"/>
    <xsl:param name="form"/>
    <xsl:param name="message"/>
    
    <xsl:template match="/">
        <form>
            <docid><xsl:value-of select="$docid"/></docid>
            <ftype><xsl:value-of select="$form"/></ftype>
            <xsl:if test="$message">
                <message><xsl:value-of select="$message"/></message>
            </xsl:if>
            <xsl:apply-templates/>
        </form>
    </xsl:template>
    
    <!-- NOTE: Mixed content is currently being handled by copy-mixed-content.xsl, which is being called by form-layout.xsl -->

</xsl:stylesheet>
