/*
 * Created on 06.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.repository;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.transaction.TransactionableFactory;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SourceNodeFactory extends AbstractLogEnabled implements TransactionableFactory {

    private ServiceManager manager;
    
    /**
     * Ctor.
     * @param manager
     * @param logger
     */
    public SourceNodeFactory(ServiceManager manager, Logger logger) {
        this.manager = manager;
        enableLogging(logger);
    }
    
    /**
     * @see org.apache.lenya.transaction.TransactionableFactory#build(org.apache.lenya.transaction.IdentityMap, java.lang.String)
     */
    public Transactionable build(IdentityMap map, String key) throws Exception {
        return new SourceNode(map, key, this.manager, getLogger());
    }

}
