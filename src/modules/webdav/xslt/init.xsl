<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="pubid" />
    <xsl:param name="context" />

	<xsl:template match="/">
        <D:multistatus xmlns:D="DAV:" xmlns:collection="http://apache.org/cocoon/collection/1.0">
           <D:response>
              <D:href><xsl:value-of select="$context" />/<xsl:value-of select="$pubid" />/</D:href>

              <D:propstat>
                 <D:prop>
                    <D:displayname><xsl:value-of select="$pubid" /></D:displayname>

                    <D:getlastmodified></D:getlastmodified>

                    <D:creationdate />

                    <D:resourcetype>
                       <D:collection />
                    </D:resourcetype>

                    <D:getcontenttype>httpd/unix-directory</D:getcontenttype>

                    <D:getcontentlength>0</D:getcontentlength>
                 </D:prop>

                 <D:status>HTTP/1.1 200 OK</D:status>
              </D:propstat>
           </D:response>

           <D:response>
              <D:href><xsl:value-of select="$context" />/<xsl:value-of select="$pubid" />/webdav</D:href>

              <D:propstat>
                 <D:prop>
                    <D:displayname>webdav</D:displayname>

                    <D:getlastmodified></D:getlastmodified>

                    <D:creationdate />

                    <D:resourcetype>
                       <D:collection />
                    </D:resourcetype>

                    <D:getcontenttype>httpd/unix-directory</D:getcontenttype>

                    <D:getcontentlength>0</D:getcontentlength>
                 </D:prop>

                 <D:status>HTTP/1.1 200 OK</D:status>
              </D:propstat>
           </D:response>


        </D:multistatus>	
	</xsl:template>

</xsl:stylesheet>
