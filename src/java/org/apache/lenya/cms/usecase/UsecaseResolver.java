/*
 * Created on 22.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.service.ServiceException;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface UsecaseResolver {

    /**
     * The Avalon role.
     */
    String ROLE = UsecaseResolver.class.getName();
    
    /**
     * Resolves a usecase object.
     * @param name The name of the usecase.
     * @return A usecase object.
     * @throws ServiceException if the object could not be created.
     */
    Usecase resolve(String name) throws ServiceException;
    
    /**
     * Releases a usecase object.
     * @param usecase The usecase object.
     * @throws ServiceException if an error occurs.
     */
    void release(Usecase usecase) throws ServiceException;
    
}
