/*
 * Created on 20.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

import org.apache.lenya.cms.publication.DocumentIdentityMap;

/**
 * This is a "Unit of Work" object (see "Unit of Work" pattern by Martin Fowler).
 * 
 * @version $Id:$
 */
public interface UnitOfWork {
    
    /**
     * The Avalon role.
     */
    String ROLE = UnitOfWork.class.getName();

    /**
     * Returns the document identity map.
     * @return An identity map.
     */
    DocumentIdentityMap getIdentityMap();
    
}
