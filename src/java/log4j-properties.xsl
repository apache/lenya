<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : log4j-properties.xsl.xsl
    Created on : 6. März 2003, 15:51
    Author     : andreas
    Description:
        
    Create log4j.properties file
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:output method="text"/>

<xsl:param name="webapp-directory"/>

<!-- remove single quotes -->
<xsl:variable name="directory" select='translate($webapp-directory, "&apos;", "")'/>

<xsl:template match="/">
#log4j.rootCategory=DEBUG, A1
#log4j.rootCategory=INFO, A1
log4j.rootCategory=WARN, A1
#log4j.rootCategory=ERROR, A1
#log4j.rootCategory=FATAL, A1
#log4j.rootCategory=LOG, A1

#log4j.appender.A1=org.apache.log4j.ConsoleAppender

log4j.appender.A1=org.apache.log4j.RollingFileAppender
log4j.appender.A1.File=<xsl:value-of select="$directory"/>/WEB-INF/logs/log4j.log

log4j.appender.A1.layout=org.apache.log4j.PatternLayout
#log4j.appender.A1.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n
log4j.appender.A1.layout.ConversionPattern=%-4r %d [%t] %-5p %c %x - %m%n

#log4j.category.org.apache.lenya.cms.cocoon.transformation.IncludeTransformer=DEBUG
#log4j.category.org.apache.lenya.cms.pubs.forum.NewArticleCreator=DEBUG
#log4j.category.org.apache.lenya.cms.scheduler.HelloWorldJob=DEBUG
</xsl:template>

</xsl:stylesheet> 
