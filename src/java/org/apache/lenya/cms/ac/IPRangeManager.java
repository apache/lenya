/*
 * Created on Jul 22, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.lenya.cms.ac;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manager for IP address ranges.
 * 
 * @author andreas
 */
public class IPRangeManager extends ItemManager {

    /**
     * Ctor.
     * @param configurationDirectory The configuration directory.
     * @throws AccessControlException when something went wrong.
     */
    protected IPRangeManager(File configurationDirectory) throws AccessControlException {
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
    public static IPRangeManager instance(File configurationDirectory) throws AccessControlException {

        assert configurationDirectory != null;
        if (!configurationDirectory.isDirectory()) {
            throw new AccessControlException(
                "Configuration directory [" + configurationDirectory + "] does not exist!");
        }

        if (!instances.containsKey(configurationDirectory)) {
            instances.put(configurationDirectory, new IPRangeManager(configurationDirectory));
        }

        return (IPRangeManager) instances.get(configurationDirectory);
    }

    /**
     * Get all IP ranges.
     *
     * @return an Iterator to iterate over all IP ranges
     */
    public Iterator getIPRanges() {
        return super.getItems();
    }

    /**
     * Add the given IP range
     *
    * @param range IP range that is to be added
    */
    public void add(IPRange range) throws AccessControlException {
        super.add(range);
    }

    /**
     * Remove the given IP range
     *
    * @param range IP range that is to be removed
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
