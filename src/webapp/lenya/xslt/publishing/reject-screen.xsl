<?xml version="1.0" encoding="UTF-8"?>

<!--
$Id: reject-screen.xsl,v 1.7 2004/02/27 11:00:22 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.
 
</License>
-->


<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
    xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
    >
 
<xsl:import href="../util/page-util.xsl"/>

<xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

<xsl:param name="lenya.event"/>

<xsl:template match="/usecase:reject">

  <page:page>
    <page:title><i18n:text>Reject</i18n:text>: <xsl:value-of select="document-id"/></page:title>
    <page:body>
          <div class="lenya-box">
            <div class="lenya-box-title">
              <i18n:translate>
                <i18n:text i18n:key="reject-doc?"/>
                <i18n:param><xsl:value-of select="document-id"/></i18n:param>
              </i18n:translate>
            </div>     
        <div class="lenya-box-body">
        <form method="GET" action="">
            <table class="lenya-table-noborder">
                <tr>
                    <td class="lenya-entry-caption" valign="top">
                        <input type="hidden" name="lenya.usecase" value="transition"/>
                        <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
                        <input type="hidden" name="task-id" value="ant"/>
                        <input type="hidden" name="target" value="mail"/>
                        <not:notification>
                            <not:preset><xsl:copy-of select="not:users"/></not:preset>
                        </not:notification>
                    </td>
                </tr>
                <tr>
                    <td>&#160;</td>
                </tr>
                <tr>
                    <td/>
                    <td>
                       <input i18n:attr="value" type="submit" name="submit" value="Reject"/> &#160; 
                       <input i18n:attr="value" type="button" onClick="location.href='{usecase:referer}';" value="Cancel"/>
                    </td>
                </tr>
                <tr>
                    <td>&#160;</td>
                </tr>
            </table>
        </form>
        </div>         
       </div> 
    </page:body>
  </page:page>
</xsl:template>

</xsl:stylesheet>