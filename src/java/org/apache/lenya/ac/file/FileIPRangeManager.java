/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* $Id: FileIPRangeManager.java,v 1.4 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.Item;

/**
 * Manager for IP address ranges.
 */
public class FileIPRangeManager extends FileItemManager implements IPRangeManager {

    /**
     * Ctor.
     * @param configurationDirectory The configuration directory.
     * @throws AccessControlException when something went wrong.
     */
    protected FileIPRangeManager(File configurationDirectory) throws AccessControlException {
        super(configurationDirectory);
    }
    
    protected static final String SUFFIX = ".ipml";

    /**
     * @see org.apache.lenya.cms.ac.ItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }

    private static Map instances = new HashMap();
    
    /**
     * Describe <code>instance</code> method here.
     *
     * @param configurationDirectory a directory
     * @return an <code>IPRangeManager</code> value
     * @exception AccessControlException if an error occurs
     */
    public static FileIPRangeManager instance(File configurationDirectory) throws AccessControlException {

        assert configurationDirectory != null;
        if (!configurationDirectory.isDirectory()) {
            throw new AccessControlException(
                "Configuration directory [" + configurationDirectory + "] does not exist!");
        }

        if (!instances.containsKey(configurationDirectory)) {
            instances.put(configurationDirectory, new FileIPRangeManager(configurationDirectory));
        }

        return (FileIPRangeManager) instances.get(configurationDirectory);
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
     * @return the requested IP range or null if there is
     * no IP range with the given id
     */
    public IPRange getIPRange(String rangeId) {
        return (IPRange) getItem(rangeId);
    }

}
