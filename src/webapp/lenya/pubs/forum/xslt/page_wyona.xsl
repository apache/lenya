<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="context_prefix">/wyona-cms/forum</xsl:variable>
<xsl:variable name="image_path"><xsl:value-of select="$context_prefix"/>/images</xsl:variable>

<xsl:template name="page">
<html>
<body bgcolor="#000000">
<table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" width="%100">
 <tr>
  <td colspan="3">
  <a href="{$context_prefix}/index.html"><img src="{$image_path}/wyona-org-forum.jpg" border="0"/></a>
  </td>
 </tr>
 <tr>
  <td valign="top">
<font size="2">
<b>
<a href="http://www.slashcode.org">Slash</a><br />
<a href="http://www.slashdot.org/slashdot.xml">Slashdot</a><br />
<a href="http://www.beblogging.com/blog/docs">CocoBlog</a><br />
<a href="http://www.squishdot.org">Squishdot</a><br />
<a href="introduction.html">Introduction</a><br />
<a href="">Submit&#160;Story</a><br />
<a href="">Getting&#160;Started</a><br />
<a href="customizing.html">Customizing</a><br />
<a href="http://www.infoxchange.net.au/tutorials/discussion-forum/mail-chat.html">Difference</a><br />
<a href="http://www.oscom.org">OSCOM</a><br />
<a href="http://www.xmlhack.com/rss10.php">xmlhack</a><br />
<a href="http://xml.apache.org/cocoon/">Cocoon</a><br />
<a href="http://www.xopus.org">Xopus</a><br />
</b>
</font>
  </td>
  <td valign="top">
    <xsl:call-template name="cmsbody"/>
  </td>
  <td valign="top" width="150">
    <table>
      <tr>
        <td>
        Related Content
        </td>
      </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td colspan="3">
  copyright &#169; 2002 wyona.org
  </td>
 </tr>
</table>
</body>
</html>
</xsl:template>
 
</xsl:stylesheet>  
