/*
 * Created on 20.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

/**
 */
public interface Operation {

    /**
     * The Avalon role.
     */
    String ROLE = Operation.class.getName();
    
    /**
     * Returns the unit of work object.
     * @return A unit of work.
     */
    UnitOfWork getUnitOfWork();
    
}
