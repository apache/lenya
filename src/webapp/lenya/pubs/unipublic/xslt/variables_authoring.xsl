<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:variable name="view">/authoring</xsl:variable>
<xsl:variable name="section"><xsl:value-of select="/wyona/cmsbody/Page/Content/NewsML/NewsItem/NewsComponent/TopicSet/Topic/TopicType/FormalName"/></xsl:variable>
<xsl:variable name="channel"><xsl:value-of select="/wyona/cmsbody/Page/Content/NewsML/NewsItem/NewsComponent/TopicSet/Topic/TopicType/@FormalName"/></xsl:variable>
</xsl:stylesheet>
