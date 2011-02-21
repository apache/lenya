package org.apache.lenya.cms.metadata;

import java.util.HashMap;
import java.util.Map;

//florent import org.apache.lenya.cms.repository.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataRegistry;

public class MetaDataRegistryWrapper implements MetaDataRegistry {

    //florent private org.apache.lenya.cms.repository.metadata.MetaDataRegistry metaDataRegistry;
	private MetaDataRegistry metaDataRegistry;

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

    public void setRepositoryMetaDataRegistry(MetaDataRegistry metaDataRegistry) {
            //florent org.apache.lenya.cms.repository.metadata.MetaDataRegistry metaDataRegistry) {
    		
        this.metaDataRegistry = metaDataRegistry;
    }

    //florent public org.apache.lenya.cms.repository.metadata.MetaDataRegistry getRepositoryMetaDataRegistry() {
    public MetaDataRegistry getRepositoryMetaDataRegistry() {
        return this.metaDataRegistry;
    }

		public void register(String namespaceUri, ElementSet elementSet)
				throws MetaDataException {
			// TODO Auto-generated method stub
			
		}

}
