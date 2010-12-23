package org.apache.lenya.cms.site.tree2;

import org.apache.lenya.cms.site.tree.SiteTree;

public interface SiteTreeMonitor {

    String ROLE = SiteTreeMonitor.class.getName();

    void addListener(SiteTree siteTree, SiteTreeMonitorListener listener);
}
