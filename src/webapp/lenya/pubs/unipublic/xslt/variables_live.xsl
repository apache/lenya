<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up"
>
<xsl:variable name="view"></xsl:variable>
<xsl:variable name="channel"><xsl:value-of select="/Page/Content/NewsML/NewsItem/NewsComponent/TopicSet/Topic/TopicType/@FormalName"/></xsl:variable>
<xsl:variable name="section"><xsl:value-of select="/Page/Content/NewsML/NewsItem/NewsComponent/TopicSet/Topic/TopicType/FormalName"/></xsl:variable>                                                                                                                                 
</xsl:stylesheet>
