/*
 * <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.ac.file;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.ac.*;
import org.apache.lenya.ac.AccessControlException;

/**
 * Manager for IP address ranges.
 * 
 * @author andreas
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
