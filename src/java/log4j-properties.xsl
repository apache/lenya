<?xml version="1.0" encoding="UTF-8" ?>

<!--
    $Id: log4j-properties.xsl,v 1.11 2004/03/01 05:35:17 michi Exp $
    Description: Create log4j.properties file
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:output method="text"/>

<xsl:param name="log4j-rollingFileAppender"/>
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


<xsl:choose>
<xsl:when test="$log4j-rollingFileAppender = 'true'">
#log4j.appender.A1=org.apache.log4j.ConsoleAppender

log4j.appender.A1=org.apache.log4j.RollingFileAppender
log4j.appender.A1.File=<xsl:value-of select="$directory"/>/WEB-INF/logs/log4j.log
</xsl:when>
<xsl:otherwise>
log4j.appender.A1=org.apache.log4j.ConsoleAppender

#log4j.appender.A1=org.apache.log4j.RollingFileAppender
#log4j.appender.A1.File=<xsl:value-of select="$directory"/>/WEB-INF/logs/log4j.log
</xsl:otherwise>
</xsl:choose>


log4j.appender.A1.layout=org.apache.log4j.PatternLayout
#log4j.appender.A1.layout.ConversionPattern=%-4r %d [%t] %-5p %c %x - %m%n
# Showing the method might slow down logging. In order to improve performance use the pattern above
log4j.appender.A1.layout.ConversionPattern=%-4r %d [%t] %-5p %c.%M():%L %x - %m%n

#log4j.category.org.apache.lenya.cms.cocoon.transformation.IncludeTransformer=DEBUG
log4j.category.org.apache.lenya.lucene.index.Indexer=INFO
log4j.category.org.apache.lenya.search.crawler.IterativeHTMLCrawler=INFO
</xsl:template>

</xsl:stylesheet> 
