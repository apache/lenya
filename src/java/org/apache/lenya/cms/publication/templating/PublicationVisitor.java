/*
 * Created on 12.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.publication.templating;

import org.apache.lenya.cms.publication.Publication;

/**
 * Publication visitor interface.
 */
public interface PublicationVisitor {
    
    /**
     * Visits a publication.
     * @param publication The publication.
     */
    void visit(Publication publication);

}
