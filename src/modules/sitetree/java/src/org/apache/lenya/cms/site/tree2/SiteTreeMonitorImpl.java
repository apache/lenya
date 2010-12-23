package org.apache.lenya.cms.site.tree2;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang.Validate;
import org.apache.lenya.cms.site.tree.SiteTree;

/**
 * SiteTreeMonitor implementation.
 * Monitor site tree file for modifications and notify
 * site tree for reloading.
 */
public class SiteTreeMonitorImpl extends AbstractLogEnabled
implements SiteTreeMonitor, Serviceable, Startable, Initializable, ThreadSafe
{
    private ServiceManager serviceManager;
    private FileAlterationMonitor fileAlterationMonitor;
    private HashMap<String, SiteTree> siteTreeMap =
        new HashMap<String, SiteTree>();
    private HashMap<String, SiteTreeMonitorListener> listenerMap =
        new HashMap<String, SiteTreeMonitorListener>();

    @Override
    public void service(ServiceManager serviceManager) throws ServiceException {
        this.serviceManager = serviceManager;
    }

    @Override
    public void start() throws Exception {
        Validate.notNull(fileAlterationMonitor, "Not initialized.");
        if (getLogger().isDebugEnabled())
            getLogger().debug("Site tree monitor started.");
        fileAlterationMonitor.start();
    }

    @Override
    public void stop() throws Exception {
        Validate.notNull(fileAlterationMonitor, "Not initialized.");
        if (getLogger().isDebugEnabled())
            getLogger().debug("Site tree monitor stopped.");
        fileAlterationMonitor.stop();
    }

    @Override
    public void initialize() throws Exception {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Site tree monitor initialized.");
        fileAlterationMonitor = new FileAlterationMonitor();
    }

    @Override
    public void addListener(SiteTree siteTree,
            SiteTreeMonitorListener listener)
    {
        // Publication/area content directory.
        File contentDir = siteTree.getPublication().getContentDirectory(
                siteTree.getArea());
        String siteTreeKey = contentDir.getAbsolutePath() + "/" +
                SiteTreeImpl.SITETREE_FILE_NAME;
        if (!siteTreeMap.containsKey(siteTreeKey)) {
            // Watch sitetree.xml file only.
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().equals(
                            SiteTreeImpl.SITETREE_FILE_NAME);
                }
            };
            FileAlterationObserver observer =
                new FileAlterationObserver(contentDir, fileFilter);
            observer.addListener(new FileAlterationListenerAdaptor() {
                @Override
                public void onFileChange(File file) {
                    if (getLogger().isDebugEnabled())
                        getLogger().debug("Site tree modified [" +
                                file.getAbsolutePath() + "]");
                    String siteTreeKey = file.getAbsolutePath();
                    SiteTree siteTree = siteTreeMap.get(siteTreeKey);
                    SiteTreeMonitorListener listener = listenerMap.get(siteTreeKey);
                    // Notify listener.
                    listener.siteTreeModified(siteTree);
                }
            });
            fileAlterationMonitor.addObserver(observer);
            siteTreeMap.put(siteTreeKey, siteTree);
            listenerMap.put(siteTreeKey, listener);
            if (getLogger().isDebugEnabled())
                getLogger().debug("Site tree monitor listener added for " +
                        siteTreeKey);
        } else {
            if (getLogger().isWarnEnabled())
                getLogger().warn("Site tree monitor listener already " +
                        "added for " + siteTreeKey);
        }
    }

}
