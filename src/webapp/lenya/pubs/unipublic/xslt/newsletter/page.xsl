<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="documentid"/>
<xsl:param name="authoring"/>

<xsl:template match="newsletter">
  <html>
    <head>
      <title>unipublic - Newsletter: <xsl:value-of select="title" /></title>
      <xsl:call-template name="styles"/>
      <xsl:call-template name="jscript"/>
    </head>

    <body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">
      <div align="center">
        <xsl:call-template name="Searchbox"/>

          <table cellspacing="0" cellpadding="0" border="0" width="585" bordercolor="green">

	   <!-- Lead -->
           <tr>
             <td colspan="3" valign="top" bgcolor="white" class="tsr-text">
              <div style="margin: 30px; font-size:medium">
                <h1>Newsletter</h1>
              
                <table border="0" cellpadding="0" cellspacing="0">
                  <xsl:apply-templates select="email/*"/>
                </table>
              </div>

                <div style="margin: 30px; font-size:medium">
                   <code>
                     <xsl:value-of select="title"/>
                     <br />
                     <xsl:call-template name="underline">
                       <xsl:with-param name="title" select="title"/>
                     </xsl:call-template>
                     <br />
                     <br />
                     <xsl:value-of select="abstract" />
                     <br />
                     <br />
                   </code>

                   <xsl:apply-templates select="articles" />
                   
                   <code>
                     <xsl:apply-templates select="footer" />
                   </code>
                </div>
             </td>
          </tr>

           <!-- Footer -->
	   <tr>
             <td width="187"></td>
             <td width="5" bgcolor="white"></td>
	     <td width="393" bgcolor="white">
               <xsl:call-template name="footer">
	         <xsl:with-param name="footer_date" select="articles/article[1]/body.head/dateline/story.date/@norm" />
               </xsl:call-template>
             </td>
           </tr>
         </table>
       </div>
     </body>
  </html>
</xsl:template>

<xsl:template match="email/to">
  <tr>
    <td><strong>To:</strong></td>
    <td>
      <code>
        <xsl:apply-templates/>
      </code>
    </td>
  </tr>
</xsl:template>

<xsl:template match="email/cc">
  <tr>
    <td><strong>Cc:</strong></td>
    <td>
      <code>
        <xsl:apply-templates/>
      </code>
    </td>
  </tr>
</xsl:template>

<xsl:template match="email/subject">
  <tr>
    <td><strong>Subject:&#160;&#160;</strong></td>
    <td>
        <xsl:apply-templates/>
    </td>
  </tr>
</xsl:template>

<xsl:template match="br">
  <xsl:copy/>
</xsl:template>

</xsl:stylesheet>

