/*
 * Created on 03.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.cocoon.source;

import org.apache.excalibur.source.Source;
import org.apache.lenya.transaction.TransactionException;

/**
 * Transactionable source.
 *
 * @version $Id:$
 */
public interface TransactionableSource extends Source {

    /**
     * Checks the source out.
     * @throws TransactionException if an error occurs.
     */
    void checkout() throws TransactionException;
    
    /**
     * Checks the source in.
     * @throws TransactionException if an error occurs.
     */
    void checkin() throws TransactionException;
    
    void lock() throws TransactionException;
    
    void unlock() throws TransactionException;
    
}
