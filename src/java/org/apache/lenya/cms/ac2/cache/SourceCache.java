/*
 * Created on Aug 13, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.lenya.cms.ac2.cache;

import org.apache.avalon.framework.component.Component;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface SourceCache extends Component {

    String ROLE = SourceCache.class.getName();

    /**
     * Returns a cached object or builds a new object when the cached
     * object does not exist or is not up to date.
     * @param sourceUri The URI to build the source from.
     * @param builder The builder to create a new object if needed.
     * @return An object.
     * @throws CachingException when something went wrong.
     */
    Object get(String sourceUri, InputStreamBuilder builder) throws CachingException;
}
