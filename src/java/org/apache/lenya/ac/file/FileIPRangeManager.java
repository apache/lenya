/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: FileIPRangeManager.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.Item;

/**
 * Manager for IP address ranges.
 */
public class FileIPRangeManager extends FileItemManager implements IPRangeManager {

    private FileIPRangeManager(ServiceManager manager, AccreditableManager accreditableManager,
            Logger logger) throws AccessControlException {
        super(manager, accreditableManager, logger);
    }

    protected static final String SUFFIX = ".ipml";

    /**
     * @see org.apache.lenya.ac.file.FileItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }

    private static Map instances = new HashMap();

    /**
     * Describe <code>instance</code> method here.
     * @param manager The service manager.
     * @param accrMgr The accreditable manager this IP range manager belongs to.
     * @param logger The logger.
     * @return an <code>IPRangeManager</code> value
     * @exception AccessControlException if an error occurs
     */
    public static synchronized FileIPRangeManager instance(ServiceManager manager,
            FileAccreditableManager accrMgr, Logger logger) throws AccessControlException {

        File configDir = accrMgr.getConfigurationDirectory();

        if (!instances.containsKey(configDir)) {
            instances.put(configDir, new FileIPRangeManager(manager, accrMgr, logger));
        }

        return (FileIPRangeManager) instances.get(configDir);
    }

    /**
     * Get all IP ranges.
     * 
     * @return an array of IP ranges.
     */
    public IPRange[] getIPRanges() {
        Item[] items = super.getItems();
        IPRange[] ranges = new IPRange[items.length];
        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = (IPRange) items[i];
        }
        return ranges;
    }

    /**
     * Add the given IP range
     * 
     * @param range IP range that is to be added
     * @throws AccessControlException when the notification failed.
     */
    public void add(IPRange range) throws AccessControlException {
        super.add(range);
    }

    /**
     * Remove the given IP range
     * 
     * @param range IP range that is to be removed
     * @throws AccessControlException when the notification failed.
     */
    public void remove(IPRange range) throws AccessControlException {
        super.remove(range);
    }

    /**
     * Get the IPRange with the given id.
     * 
     * @param rangeId user id of requested IP range
     * @return the requested IP range or null if there is no IP range with the
     *         given id
     */
    public IPRange getIPRange(String rangeId) {
        return (IPRange) getItem(rangeId);
    }

}
