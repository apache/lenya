/*
 * Created on 06.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.repository;

import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;
import org.w3c.dom.Document;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Node extends Transactionable {

    Document getDocument();
    
    void setDocument(Document document);
    
    String TRANSACTIONABLE_TYPE = "node";
    
    boolean exists() throws TransactionException;
    
}
