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

/* $Id: FileItemManager.java,v 1.3 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManagerListener;
import org.apache.lenya.ac.impl.ItemConfiguration;
import org.apache.log4j.Category;


/**
 * Abstract superclass for classes that manage items loaded from configuration files.
 */
public abstract class FileItemManager {
    private static final Category log = Category.getInstance(FileItemManager.class);

    public static final String PATH = "config" + File.separator + "ac" + File.separator + "passwd";
    private Map items = new HashMap();
    private File configurationDirectory;

    /**
     * Create a new ItemManager
     *
     * @param configurationDirectory where the items are fetched from
     * @throws AccessControlException if the item manager cannot be instantiated
     */
    protected FileItemManager(File configurationDirectory)
        throws AccessControlException {
        assert configurationDirectory != null;

        if (!configurationDirectory.exists() || !configurationDirectory.isDirectory()) {
            throw new AccessControlException("The directory [" +
                configurationDirectory.getAbsolutePath() + "] does not exist!");
        }

        this.configurationDirectory = configurationDirectory;

        File[] itemFiles = configurationDirectory.listFiles(getFileFilter());

        Configuration config = null;

        for (int i = 0; i < itemFiles.length; i++) {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

            try {
                assert itemFiles[i].exists();
                config = builder.buildFromFile(itemFiles[i]);
            } catch (Exception e) {
                String errorMsg = "Exception when reading the configuration from file: " +
                    itemFiles[i].getName();

                // an exception occured when trying to read the configuration
                // from the identity file.
                log.error(errorMsg);
                throw new AccessControlException(errorMsg, e);
            }

            String klass = null;

            try {
                klass = config.getAttribute(ItemConfiguration.CLASS_ATTRIBUTE);
            } catch (ConfigurationException e) {
                String errorMsg = "Exception when extracting class name from identity file: " +
                    klass + config.getAttributeNames();
                log.error(errorMsg);
                throw new AccessControlException(errorMsg, e);
            }

            Item item = null;

            try {
                item = (Item) Class.forName(klass).newInstance();
            } catch (Exception e) {
                String errorMsg = "Exception when trying to instanciate: " + klass +
                    " with exception: " + e.fillInStackTrace();

                // an exception occured when trying to instanciate
                // a user.
                log.error(errorMsg);
                throw new AccessControlException(errorMsg, e);
            }

            item.setConfigurationDirectory(configurationDirectory);

            try {
                item.configure(config);
            } catch (ConfigurationException e) {
                String errorMsg = "Exception when trying to configure: " + klass;
                throw new AccessControlException(errorMsg, e);
            }

            add(item);
        }
    }
    
    /**
     * Returns an item for a given ID.
     * @param id The id.
     * @return An item.
     */
    public Item getItem(String id) {
        return (Item) items.get(id);
    }

    /**
     * get all items
     *
     * @return an array of items
     */
    public Item[] getItems() {
        return (Item[]) items.values().toArray(new Item[items.values().size()]);
    }

    /**
     * Add an Item to this manager
     *
     * @param item to be added
     * @throws AccessControlException when the notification threw this exception.
     */
    public void add(Item item) throws AccessControlException {
        assert item != null;
        items.put(item.getId(), item);
        notifyAdded(item);
    }

    /**
     *
     * Remove an item from this manager
     *
     * @param item to be removed
     * @throws AccessControlException when the notification threw this exception.
     */
    public void remove(Item item) throws AccessControlException {
        items.remove(item.getId());
        notifyRemoved(item);
    }
    
    /**
     * Returns if the ItemManager contains an object.
     * @param item The object.
     * @return A boolean value.
     */
    public boolean contains(Item item) {
        return items.containsValue(item);
    }

    /**
     * Get the directory where the items are located.
     *
     * @return a <code>File</code>
     */
    public File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * Get a file filter which filters for files containing items.
     * @return a <code>FileFilter</code>
     */
    protected FileFilter getFileFilter() {
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(getSuffix()));
            }
        };

        return filter;
    }
    
    /**
     * Returns the file extension to be used.
     * @return A string.
     */
    protected abstract String getSuffix();

    private List itemManagerListeners = new ArrayList();
    
    /**
     * Attaches an item manager listener to this item manager.
     * @param listener An item manager listener.
     */
    public void addItemManagerListener(ItemManagerListener listener) {
        log.debug("Adding listener: [" + listener + "]");
        if (!itemManagerListeners.contains(listener)) {
            itemManagerListeners.add(listener);
        }
    }
    
    /**
     * Removes an item manager listener from this item manager.
     * @param listener An item manager listener.
     */
    public void removeItemManagerListener(ItemManagerListener listener) {
        log.debug("Removing listener: [" + listener + "]");
       itemManagerListeners.remove(listener);
    }
    
    /**
     * Notifies the listeners that an item was added.
     * @param item The item that was added.
     */
    protected void notifyAdded(Item item) throws AccessControlException {
        log.debug("Item was added: [" + item + "]");
        List clone = new ArrayList(itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext(); ) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            listener.itemAdded(item);
        }
    }

    /**
     * Notifies the listeners that an item was removed.
     * @param item The item that was removed.
     */
    protected void notifyRemoved(Item item) throws AccessControlException {
        log.debug("Item was removed: [" + item + "]");
        List clone = new ArrayList(itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext(); ) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            log.debug("Notifying listener: [" + listener + "]");
            listener.itemRemoved(item);
        }
    }

}
