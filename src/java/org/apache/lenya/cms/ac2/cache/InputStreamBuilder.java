/*
 * Created on Aug 13, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.lenya.cms.ac2.cache;

import java.io.InputStream;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface InputStreamBuilder {

    /**
     * Builds an object from an input stream.
     * @param stream An input stream.
     * @return An object.
     * @throws BuildException when building the object failed.
     */
    Object build(InputStream stream) throws BuildException;

}
