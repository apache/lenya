<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:oscom="http://www.oscom.org/2002/oscom">

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="oscom">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org" />
<title>OSCOM - Open Source Content Management - Conference 2002, Berkeley</title>
<link type="text/css" rel="stylesheet" href="http://oscom.smokinggun.com/site.css" />
<!--
<link type="text/css" rel="stylesheet" href="site.css" />
-->
<script type="text/javascript">
    
    function titleOver()
        {
        document.getElementById("titleImage").src = "http://oscom.smokinggun.com/images/title_on.gif";
        }
        
    function titleOut()
        {
        document.getElementById("titleImage").src = "http://oscom.smokinggun.com/images/title.gif";
        }
    
    
</script>
</head>
<body>

<div id="top">
<img src="http://oscom.smokinggun.com/images/logo.gif" id="logo" alt="#2" />
<!--
<a href="../../index.html"><img src="http://oscom.smokinggun.com/images/logo.gif" id="logo" alt="#2" /></a>
-->
<a href="../../index.html" onmouseover="titleOver()" onmouseout="titleOut()" id="title"><img src="http://oscom.smokinggun.com/images/title.gif" id="titleImage" alt="OSCOM | Berkeley Conference" /></a>
</div>

<div id="nav">
<a href="http://www.oscom.org"><img src="http://oscom.smokinggun.com/images/oscom.gif" alt="OSCOM Home" /></a>
<a href="index.html"><img src="http://oscom.smokinggun.com/images/intro_on.gif" alt="Intro" /></a>
<a href="program.html"><img src="http://oscom.smokinggun.com/images/program.gif" alt="Program" /></a>
<a href="registration_fees.html"><img
src="http://oscom.smokinggun.com/images/registration.gif" alt="Registration" /></a> <a
href="localinfo.html"><img src="http://oscom.smokinggun.com/images/local.gif" alt="Local Info" />
</a> <a href="press-release.html"><img src="http://oscom.smokinggun.com/images/press.gif" alt="Press" /></a> <a
href="sponsoring.html"><img src="http://oscom.smokinggun.com/images/sponsor.gif" alt="Sponsoring" /></a> <a
href="contact.html"><img src="http://oscom.smokinggun.com/images/contact.gif" alt="Contact" /></a></div>

<div id="contents">
<!--
      <xsl:apply-templates select="oscom_navigation"/>
-->
      <xsl:call-template name="body"/>
</div>

<div id="footer">copyright &#169; 2002 <a href="http://www.oscom.org">oscom.org</a> | design by <a href="http://www.smokinggun.com">sg</a><br />
Please contact <a href="mailto:abuse@oscom.org">abuse@oscom.org</a>
to address spam or abuse complaints</div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
