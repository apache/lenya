/*
 * Created on 12.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.publication.templating;

import org.apache.excalibur.source.Source;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SourceVisitor {

    /**
     * Visits a source.
     * @param source The source.
     */
    void visit(Source source);

}
