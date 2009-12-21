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

/* $Id$  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.ItemManagerListener;
import org.apache.lenya.ac.impl.ItemConfiguration;

/**
 * Abstract superclass for classes that manage items loaded from configuration
 * files.
 */
public abstract class FileItemManager extends AbstractLogEnabled implements ItemManager {

    private Map items = new HashMap();
    private File configurationDirectory;
    private DirectoryChangeNotifier notifier;

    private AccreditableManager accreditableManager;

    /**
     * Create a new ItemManager.
     * @param accreditableManager The {@link AccreditableManager}.
     */
    protected FileItemManager(AccreditableManager accreditableManager) {
        this.accreditableManager = accreditableManager;
    }

    /**
     * Configures the item manager.
     * @param _configurationDirectory where the items are fetched from
     * @throws AccessControlException if the item manager cannot be instantiated
     */
    public void configure(File _configurationDirectory) throws AccessControlException {
        assert _configurationDirectory != null;

        if (!_configurationDirectory.exists() || !_configurationDirectory.isDirectory()) {
            throw new AccessControlException("The directory ["
                    + _configurationDirectory.getAbsolutePath() + "] does not exist!");
        }

        this.configurationDirectory = _configurationDirectory;
        this.notifier = new DirectoryChangeNotifier(_configurationDirectory, getFileFilter());
        this.notifier.enableLogging(getLogger());
        loadItems();
    }

    /**
     * Reloads the items if an item was changed / added / removed.
     * @throws AccessControlException when something went wrong.
     */
    protected void loadItems() throws AccessControlException {

        boolean changed;
        try {
            changed = this.notifier.hasChanged();
        } catch (IOException e) {
            throw new AccessControlException(e);
        }

        if (changed) {

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Item configuration has changed - reloading.");
            }

            File[] addedFiles = this.notifier.getAddedFiles();

            for (int i = 0; i < addedFiles.length; i++) {
                Item item = loadItem(addedFiles[i]);
                add(item);
            }

            File[] removedFiles = this.notifier.getRemovedFiles();
            for (int i = 0; i < removedFiles.length; i++) {
                String fileName = removedFiles[i].getName();
                String id = fileName.substring(0, fileName.length() - getSuffix().length());

                Item item = (Item) this.items.get(id);

                if (item != null) {

                    if (item instanceof Groupable) {
                        ((Groupable) item).removeFromAllGroups();
                    }
                    if (item instanceof Group) {
                        ((Group) item).removeAllMembers();
                    }

                    remove(item);
                }
            }

            File[] changedFiles = this.notifier.getChangedFiles();
            for (int i = 0; i < changedFiles.length; i++) {
                Item item = loadItem(changedFiles[i]);
                update(item);
            }

        }

    }

    /**
     * Loads an item from a file.
     * @param file The file.
     * @return An item.
     * @throws AccessControlException when something went wrong.
     */
    protected Item loadItem(File file) throws AccessControlException {
        Configuration config = getItemConfiguration(file);

        String fileName = file.getName();
        String id = fileName.substring(0, fileName.length() - getSuffix().length());
        Item item = (Item) this.items.get(id);

        String klass = ItemConfiguration.getItemClass(config);
        if (item == null) {
            try {
                Class[] paramTypes = { ItemManager.class, Logger.class };
                Constructor ctor = Class.forName(klass).getConstructor(paramTypes);
                Object[] params = { this, getLogger() };
                item = (Item) ctor.newInstance(params);
            } catch (Exception e) {
                String errorMsg = "Exception when trying to instanciate: " + klass
                        + " with exception: " + e.fillInStackTrace();

                // an exception occured when trying to instanciate
                // a user.
                getLogger().error(errorMsg);
                throw new AccessControlException(errorMsg, e);
            }
        }

        try {
            item.configure(config);
        } catch (ConfigurationException e) {
            String errorMsg = "Exception when trying to configure: " + klass;
            throw new AccessControlException(errorMsg, e);
        }
        return item;
    }

    /**
     * Loads teh configuration of an item from a file.
     * @param file The file.
     * @return A configuration.
     * @throws AccessControlException when something went wrong.
     */
    protected Configuration getItemConfiguration(File file) throws AccessControlException {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = null;

        try {
            assert file.exists();
            config = builder.buildFromFile(file);
        } catch (Exception e) {
            String errorMsg = "Exception when reading the configuration from file: "
                    + file.getName();

            // an exception occured when trying to read the configuration
            // from the identity file.
            getLogger().error(errorMsg);
            throw new AccessControlException(errorMsg, e);
        }
        return config;
    }

    protected void removeItem(File file) {
        // do nothing
    }

    /**
     * Returns an item for a given ID.
     * @param id The id.
     * @return An item.
     */
    public Item getItem(String id) {
        try {
            loadItems();
        } catch (AccessControlException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return (Item) this.items.get(id);
    }

    /**
     * get all items
     * @return an array of items
     */
    public Item[] getItems() {
        try {
            loadItems();
        } catch (AccessControlException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return (Item[]) this.items.values().toArray(new Item[this.items.values().size()]);
    }

    /**
     * Add an Item to this manager
     * @param item to be added
     * @throws AccessControlException when the notification threw this
     *         exception.
     */
    public void add(Item item) throws AccessControlException {
        assert item != null;
        this.items.put(item.getId(), item);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item [" + item + "] added.");
        }
        notifyAdded(item);
    }

    /**
     * Remove an item from this manager
     * @param item to be removed
     * @throws AccessControlException when the notification threw this
     *         exception.
     */
    public void remove(Item item) throws AccessControlException {
        this.items.remove(item.getId());
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item [" + item + "] removed.");
        }
        notifyRemoved(item);
    }

    /**
     * Update an item.
     * @param newItem The new version of the item.
     * @throws AccessControlException when the notification threw this
     *         exception.
     */
    public void update(Item newItem) throws AccessControlException {
        this.items.remove(newItem.getId());
        this.items.put(newItem.getId(), newItem);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item [" + newItem + "] updated.");
        }
    }

    /**
     * Returns if the ItemManager contains an object.
     * @param item The object.
     * @return A boolean value.
     */
    public boolean contains(Item item) {
        try {
            loadItems();
        } catch (AccessControlException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return this.items.containsValue(item);
    }

    /**
     * Get the directory where the items are located.
     * @return a <code>File</code>
     */
    public File getConfigurationDirectory() {
        return this.configurationDirectory;
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
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Adding listener: [" + listener + "]");
        }
        if (!this.itemManagerListeners.contains(listener)) {
            this.itemManagerListeners.add(listener);
        }
    }

    /**
     * Removes an item manager listener from this item manager.
     * @param listener An item manager listener.
     */
    public void removeItemManagerListener(ItemManagerListener listener) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Removing listener: [" + listener + "]");
        }
        this.itemManagerListeners.remove(listener);
    }

    /**
     * Notifies the listeners that an item was added.
     * @param item The item that was added.
     * @throws AccessControlException if an error occurs.
     */
    protected void notifyAdded(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was added: [" + item + "]");
        }
        List clone = new ArrayList(this.itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext();) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            listener.itemAdded(item);
        }
    }

    /**
     * Notifies the listeners that an item was removed.
     * @param item The item that was removed.
     * @throws AccessControlException if an error occurs.
     */
    protected void notifyRemoved(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was removed: [" + item + "]");
        }
        List clone = new ArrayList(this.itemManagerListeners);
        for (Iterator i = clone.iterator(); i.hasNext();) {
            ItemManagerListener listener = (ItemManagerListener) i.next();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Notifying listener: [" + listener + "]");
            }
            listener.itemRemoved(item);
        }
    }

    /**
     * Helper class to observe a directory for changes.
     */
    public static class DirectoryChangeNotifier extends AbstractLogEnabled {

        /**
         * Ctor.
         * @param _directory The directory to observe.
         * @param _filter A filter to specify the file type to observe.
         */
        public DirectoryChangeNotifier(File _directory, FileFilter _filter) {
            this.directory = _directory;
            this.filter = _filter;
        }

        private File directory;
        private FileFilter filter;
        private Map canonicalPath2LastModified = new HashMap();

        private Set addedFiles = new HashSet();
        private Set removedFiles = new HashSet();
        private Set changedFiles = new HashSet();

        /**
         * Checks if the directory has changed (a new file was added, a file was
         * removed, a file has changed).
         * @return A boolean value.
         * @throws IOException when something went wrong.
         */
        public boolean hasChanged() throws IOException {

            this.addedFiles.clear();
            this.removedFiles.clear();
            this.changedFiles.clear();

            File[] files = this.directory.listFiles(this.filter);

            Set newPathSet = new HashSet();

            for (int i = 0; i < files.length; i++) {
                String canonicalPath = files[i].getCanonicalPath();
                newPathSet.add(canonicalPath);

                if (!this.canonicalPath2LastModified.containsKey(canonicalPath)) {
                    this.addedFiles.add(new File(canonicalPath));

                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("New file: [" + canonicalPath + "]");
                    }

                } else {
                    Long lastModifiedObject = (Long) this.canonicalPath2LastModified
                            .get(canonicalPath);
                    long lastModified = lastModifiedObject.longValue();
                    if (lastModified < files[i].lastModified()) {
                        this.changedFiles.add(files[i]);
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("File has changed: [" + canonicalPath + "]");
                        }
                    }
                }
                Long lastModified = new Long(files[i].lastModified());
                this.canonicalPath2LastModified.put(canonicalPath, lastModified);
            }

            Set oldPathSet = this.canonicalPath2LastModified.keySet();
            String[] oldPaths = (String[]) oldPathSet.toArray(new String[oldPathSet.size()]);
            for (int i = 0; i < oldPaths.length; i++) {
                if (!newPathSet.contains(oldPaths[i])) {
                    this.removedFiles.add(new File(oldPaths[i]));
                    this.canonicalPath2LastModified.remove(oldPaths[i]);
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("File removed: [" + oldPaths[i] + "]");
                    }
                }
            }

            return !this.addedFiles.isEmpty() || !this.removedFiles.isEmpty()
                    || !this.changedFiles.isEmpty();
        }

        /**
         * Returns the added files.
         * @return An array of files.
         */
        public File[] getAddedFiles() {
            return (File[]) this.addedFiles.toArray(new File[this.addedFiles.size()]);
        }

        /**
         * Returns the removed files.
         * @return An array of files.
         */
        public File[] getRemovedFiles() {
            return (File[]) this.removedFiles.toArray(new File[this.removedFiles.size()]);
        }

        /**
         * Returns the changed files.
         * @return An array of files.
         */
        public File[] getChangedFiles() {
            return (File[]) this.changedFiles.toArray(new File[this.changedFiles.size()]);
        }

    }

    public AccreditableManager getAccreditableManager() {
        return this.accreditableManager;
    }

}
