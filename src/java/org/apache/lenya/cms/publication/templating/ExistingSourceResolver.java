/*
 * Created on 12.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.publication.templating;

import org.apache.excalibur.source.Source;

/**
 * Source visitor to obtain the first existing source.
 */
public class ExistingSourceResolver implements SourceVisitor {
    
    private String uri;

    /**
     * Ctor.
     */
    public ExistingSourceResolver() {
        super();
    }
    
    /**
     * Returns the URI of the first existing source.
     * @return
     */
    public String getURI() {
        return uri;
    }

    /**
     * @see org.apache.lenya.cms.publication.templating.SourceVisitor#visit(org.apache.excalibur.source.Source)
     */
    public void visit(Source source) {
        if (this.uri == null && source.exists()) {
            this.uri = source.getURI();
        }
    }

}
