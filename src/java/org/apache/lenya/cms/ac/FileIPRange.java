/*
$Id: FileIPRange.java,v 1.1 2003/07/22 17:01:34 andreas Exp $
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
package org.apache.lenya.cms.ac;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;

/**
 * IP range that is stored in a file.
 * 
 * @author andreas
 */
public class FileIPRange extends IPRange {

    /**
     * Ctor.
     */    
    public FileIPRange() {
    }
    
    /**
     * Ctor.
     * @param configurationDirectory The configuration directory.
     * @param id The IP range ID.
     */
    public FileIPRange(File configurationDirectory, String id) {
        super(id);
        setConfigurationDirectory(configurationDirectory);
    }

    /**
     * @see org.apache.lenya.cms.ac.IPRange#save()
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();

        try {
            serializer.serializeToFile(getFile(), config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * Returns the configuration file.
     * @return A file object.
     */
    protected File getFile() {
        File xmlPath = getConfigurationDirectory();
        File xmlFile = new File(xmlPath, getId() + IPRangeManager.SUFFIX);
        return xmlFile;
    }

    /**
     * @see org.apache.lenya.cms.ac.Item#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);
        
    }

    public static final String IP_RANGE = "ip-range";
    
    /**
     * Create a configuration from the current user details. Can
     * be used for saving.
     *
     * @return a <code>Configuration</code>
     */
    protected Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(IP_RANGE);
        new ItemConfiguration().save(this, config);
        return config;
    }
}
