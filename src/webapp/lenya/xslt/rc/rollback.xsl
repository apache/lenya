<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
        xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
        xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:rc="http://apache.org/cocoon/lenya/rc/1.0">

  <xsl:variable name="usecase"><xsl:value-of select="/rc:revisions/usecase/." /></xsl:variable>
  <xsl:variable name="requestUri"><xsl:value-of select="/rc:revisions/request_uri/." /></xsl:variable>
  <xsl:variable name="documentId"><xsl:value-of select="/rc:revisions/documentId/." /></xsl:variable>

  <xsl:template match="/">
    <page:page>
      <page:title>Revisions</page:title>
      <page:body>
        <xsl:apply-templates select="rc:revisions/XPSRevisionControl" />
      </page:body>
    </page:page>
  </xsl:template>

  <xsl:template match="XPSRevisionControl">
        <div class="lenya-box">
          <a href="{$requestUri}">Back to page</a>
        </div>
    <div class="lenya-box">
      <div class="lenya-box-title">Rollback to an earlier version</div>
      <div class="lenya-box-body">
        <table class="lenya-table-noborder">
          <tr bgcolor="#aaaaaa">
            <th></th>
            <th></th>
            <th>Checked in at</th>
            <th>Checked in by</th>
          </tr>
          <xsl:for-each select="CheckIn">
            <xsl:choose>
              <xsl:when test="position()=1">
                <tr>
                  <td>Current version</td>
                  <td>&#160;</td>
                  <xsl:apply-templates select="Time" />
                  <xsl:apply-templates select="Identity" />
                </tr>
              </xsl:when>
              <xsl:when test="position()&gt;1">
                <xsl:apply-templates select="Backup" />
              </xsl:when>
            </xsl:choose>
          </xsl:for-each>
        </table>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="Backup">
    <tr>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href">?lenya.usecase=rollback&amp;lenya.step=rollback&amp;rollbackTime=<xsl:value-of select="../Time" /></xsl:attribute>
        Rollback to this version</xsl:element>
      </td>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href">?lenya.usecase=rollback&amp;lenya.step=view&amp;rollbackTime=<xsl:value-of select="../Time" /></xsl:attribute>
          <xsl:attribute name="target">_blank</xsl:attribute>View
        </xsl:element>
      </td>
      <xsl:apply-templates select="../Time" />
      <xsl:apply-templates select="../Identity" />
    </tr>
  </xsl:template>

  <xsl:template match="Time">
    <td align="right">
      <xsl:value-of select="@humanreadable" />
    </td>
  </xsl:template>

  <xsl:template match="Identity">
    <td>
      <xsl:apply-templates />
    </td>
  </xsl:template>
</xsl:stylesheet>

