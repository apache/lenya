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
package org.apache.lenya.cms.metadata;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ExtendedComponentSelector;
import org.apache.lenya.cms.publication.DocumentException;

public class MetaDataRegistryImpl extends AbstractLogEnabled implements MetaDataRegistry, Serviceable {
    
    public ElementSet getElementSet(String namespaceUri) throws DocumentException {
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(ElementSet.class.getName() + "Selector");
            return (ElementSet) selector.select(namespaceUri);
        } catch (ServiceException e) {
            throw new DocumentException(e);
        }
        finally {
            if (selector != null) {
                this.manager.release(selector);
            }
        }
    }

    public boolean isRegistered(String namespaceUri) throws DocumentException {
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(ElementSet.class.getName() + "Selector");
            return selector.isSelectable(namespaceUri);
        } catch (ServiceException e) {
            throw new DocumentException(e);
        }
        finally {
            if (selector != null) {
                this.manager.release(selector);
            }
        }
    }

    private ServiceManager manager;
    
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
