<?php

$xsltproc = xslt_create();
$html = xslt_process($xsltproc,'specialcharacters.xml',"specialcharacters.xsl",NULL);

print $html;
?>

