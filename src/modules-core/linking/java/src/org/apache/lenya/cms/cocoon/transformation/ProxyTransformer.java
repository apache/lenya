package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ProxyTransformer extends AbstractSAXTransformer {
  protected static final String[] elementNames = { "a", "object", "img",
      "link", "form" , "script" };

  protected static final String[] attributeNames = { "href", "src", "data",
      "action" };

  private boolean ignoreLinkElement = false;

  private String indent = "";

  private DocumentFactory factory;

  private Publication publication;

  private String url;

  private ServiceSelector serviceSelector;

  private AccessControllerResolver acResolver;

  private AccreditableManager accreditableManager;

  private PolicyManager policyManager;

  protected static final String PARAMETER_FACTORY = "private.factory";

  private static final String ATTRIBUTE_ROOT = "root";

  public void setup(SourceResolver _resolver, Map _objectModel, String _source,
      Parameters _parameters) throws ProcessingException, SAXException,
      IOException {
    super.setup(_resolver, _objectModel, _source, _parameters);
    Request _request = ObjectModelHelper.getRequest(_objectModel);

    try {
      Session session = RepositoryUtil.getSession(this.manager, _request);
      this.factory = DocumentUtil.createDocumentFactory(this.manager, session);
      this.url = ServletHelper.getWebappURI(_request);
      this.publication = PublicationUtil.getPublicationFromUrl(this.manager,
          factory, url);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    this.serviceSelector = null;
    try {
      this.serviceSelector = (ServiceSelector) this.manager
          .lookup(AccessControllerResolver.ROLE + "Selector");
      this.acResolver = (AccessControllerResolver) this.serviceSelector
          .select(AccessControllerResolver.DEFAULT_RESOLVER);
      AccessController accessController = this.acResolver
          .resolveAccessController(url);
      if (accessController!=null){
        this.accreditableManager = accessController.getAccreditableManager();
        this.policyManager = accessController.getPolicyManager();
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }

  }

  public void startElement(String uri, String name, String qname,
      Attributes attrs) throws SAXException {
    if (getLogger().isDebugEnabled()) {
      getLogger().debug(
          this.indent + "<" + qname + "> (ignoreAElement = "
              + this.ignoreLinkElement + ")");
      this.indent += "  ";
    }
    AttributesImpl newAttrs = null;
    if (lookingAtLinkElement(name)) {

      for (int i = 0; i < attributeNames.length; i++) {
        String linkUrl = attrs.getValue(attributeNames[i]);
        if (linkUrl != null) {
          if (linkUrl.startsWith("/")) {
            try {
              newAttrs = new AttributesImpl(attrs);
              rewriteLink(newAttrs, attributeNames[i], linkUrl);
              if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "link URL: [" + linkUrl + "]");
              }
            } catch (final Exception e) {
              getLogger().error("startElement failed: ", e);
              throw new SAXException(e);
            }
          }
        }
      }
      if (newAttrs == null)
        super.startElement(uri, name, qname, attrs);
      else
        super.startElement(uri, name, qname, newAttrs);
    } else
      super.startElement(uri, name, qname, attrs);

  }

  public void endElement(String uri, String name, String qname)
      throws SAXException {
    if (getLogger().isDebugEnabled()) {
      this.indent = this.indent.substring(2);
      getLogger().debug(this.indent + "</" + qname + ">");
    }
    if (getLogger().isDebugEnabled()) {
      getLogger().debug(this.indent + "</" + qname + "> sent");
    }
    super.endElement(uri, name, qname);
  }

  private void rewriteLink(AttributesImpl newAttrs, String attributeName,
      String linkUrl) throws AccessControlException, DocumentBuildException {
    String rewrittenURL = "";
    Policy policy=null;
    if(policyManager!=null)
    policy = this.policyManager.getPolicy(this.accreditableManager, linkUrl);
    String area = "";
    if(factory.isDocument(linkUrl)){
      area = factory.getFromURL(linkUrl).getArea();
    }
    if (PublicationUtil.isValidArea(area)) {
      Proxy proxy = this.publication.getProxy(area, policy.isSSLProtected());
      if (proxy == null) {
        rewrittenURL = this.request.getContextPath() + linkUrl;
      } else {
        String prefix = "/" + publication.getId() + "/" + area;
        if (linkUrl.startsWith(prefix))
          rewrittenURL = proxy.getUrl() + linkUrl.substring(prefix.length());
        else
          rewrittenURL = proxy.getUrl() + linkUrl;
      }
      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            this.indent + "SSL protection: [" + policy.isSSLProtected() + "]");
        getLogger().debug(this.indent + "Resolved proxy: [" + proxy + "]");
      }

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            this.indent + "Rewriting URL to: [" + rewrittenURL + "]");
      }
    } else {
      // Since we came here the link is not covered by the area proxies.
      // Now we try the global proxy for the pub of our initial request.
      Proxy proxy = this.publication.getProxy(ATTRIBUTE_ROOT, (policy==null)?false:policy.isSSLProtected());
      if (proxy == null) {
        rewrittenURL = this.request.getContextPath() + linkUrl;
      } else {
        rewrittenURL = proxy.getUrl() + linkUrl.substring(1);
      }
      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            this.indent + "Rewriting URL to: [" + rewrittenURL + "]");
      }
    }
    setAttribute(newAttrs, attributeName, rewrittenURL);
  }

  private boolean lookingAtLinkElement(String name) {
    return Arrays.asList(elementNames).contains(name);
  }

  /**
   * Sets the value of the href attribute.
   * 
   * @param attr
   *          The attributes.
   * @param name
   *          The attribute name.
   * @param value
   *          The value.
   * @throws IllegalArgumentException
   *           if the href attribute is not contained in this attributes.
   */
  protected void setAttribute(AttributesImpl attr, String name, String value) {
    int position = attr.getIndex(name);
    if (position == -1) {
      throw new IllegalArgumentException("The href attribute is not available!");
    }
    attr.setValue(position, value);
  }
}
