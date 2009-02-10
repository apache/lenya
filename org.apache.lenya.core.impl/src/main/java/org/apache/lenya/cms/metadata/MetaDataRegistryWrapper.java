package org.apache.lenya.cms.metadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.cms.repository.metadata.MetaDataException;

public class MetaDataRegistryWrapper implements MetaDataRegistry {

    private org.apache.lenya.cms.repository.metadata.MetaDataRegistry metaDataRegistry;

    private Map<String, ElementSetWrapper> elementSets = new HashMap<String, ElementSetWrapper>();

    public ElementSet getElementSet(String namespaceUri)
            throws org.apache.lenya.cms.metadata.MetaDataException {
        ElementSetWrapper wrapper = this.elementSets.get(namespaceUri);
        if (wrapper == null) {
            try {
                wrapper = new ElementSetWrapper(this.metaDataRegistry.getElementSet(namespaceUri));
            } catch (MetaDataException e) {
                throw new org.apache.lenya.cms.metadata.MetaDataException(e);
            }
            this.elementSets.put(namespaceUri, wrapper);
        }
        return wrapper;
    }

    public String[] getNamespaceUris() throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            return this.metaDataRegistry.getNamespaceUris();
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public boolean isRegistered(String namespaceUri)
            throws org.apache.lenya.cms.metadata.MetaDataException {
        try {
            return this.metaDataRegistry.isRegistered(namespaceUri);
        } catch (MetaDataException e) {
            throw new org.apache.lenya.cms.metadata.MetaDataException(e);
        }
    }

    public void setRepositoryMetaDataRegistry(
            org.apache.lenya.cms.repository.metadata.MetaDataRegistry metaDataRegistry) {
        this.metaDataRegistry = metaDataRegistry;
    }

    public org.apache.lenya.cms.repository.metadata.MetaDataRegistry getRepositoryMetaDataRegistry() {
        return this.metaDataRegistry;
    }

}
