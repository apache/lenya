package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.Constants;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Request;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.xml.sax.EntityResolver;

public class NoPasswordYetAction extends AbstractAction{
  public Map act(Redirector redirector,SourceResolver resolver,Map objectModel,String source,Parameters params){
    // This is the name of the flag file that tells us if
    // this is a fresh installation. If the file is present,
    // we assume a fresh install and return an empty map,
    // otherwise we assume a configured installation and return 
    // null.
    //
    // We get this parameter from the sitemap through the params object.
    // The filename is relative to the WEB-INF directory.
    //
    String flagFileRelativePath=params.getParameter("flagfilerelativepath","new_installation_flag");
    Context context=(Context)objectModel.get(Constants.CONTEXT_OBJECT);
    String flagFileAbsolutePath = context.getRealPath("/") + "/" + flagFileRelativePath;
    getLogger().error("DetectNewInstallationAction: flagFileAbsolutePath = " + flagFileAbsolutePath);
    if (!new File(flagFileAbsolutePath).exists()) {
      getLogger().error("DetectNewInstallationAction: does not exist, skipping first-time setup!");
      return null;
      }
    getLogger().error("DetectNewInstallationAction: does exist!");
    Map sitemapParams = new HashMap();
    sitemapParams.put("flagfileabsolutepath", flagFileAbsolutePath);
    return sitemapParams;
    }
  }
