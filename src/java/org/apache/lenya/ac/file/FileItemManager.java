/*
$Id: FileItemManager.java,v 1.2 2004/02/02 02:50:40 stefano Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
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
 * @author egli
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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
