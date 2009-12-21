package org.apache.cocoon.components.search.components;

import org.apache.cocoon.ProcessingException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

/**
 * this Searcher Component allows: <br/> - searching in several indexes <br/> - sorting hits by a
 * specified field and order
 * 
 * @author Nicolas Maisonneuve
 */
public interface Searcher {
    /**
     * The ROLE name of this avalon component.
     * <p>
     * Its value if the FQN of this interface, ie.
     * <code>org.apache.cocoon.components.search.Searcher</code>.
     * </p>
     * 
     * @since
     */
    String ROLE = Searcher.class.getName();

    /**
     * Add a lucene directory -- you can add several directories
     * <p>
     * The directory specifies the directory used for looking up the index. It defines the physical
     * place of the index
     * </p>
     * 
     * @param directory The new directory value
     */
    public void addDirectory(Directory directory);

    /**
     * Set the field by which the search results are to be sorted
     * @param field the index field
     * @param reverse reverse order or not
     */
    public void setSortField(String field, boolean reverse);

    /**
     * Search using a Lucene Query object, returning zero, or more hits.
     * 
     * @param query A lucene query
     * @return Hits zero or more hits matching the query string
     * @exception ProcessingException throwing due to processing errors while looking up the index
     *                directory, parsing the query string, generating the hits.
     */
    public Hits search(Query query) throws ProcessingException;
}
