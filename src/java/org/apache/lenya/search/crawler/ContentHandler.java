package org.wyona.search.crawler;

import java.io.InputStream;
import java.util.List;

/**
 *
 */
public interface ContentHandler {
    
    
    /**
     * Return author
     */
    public String getAuthor();
    /**
     * Return categories (from META tags)
     */
    public String getCategories();
    /**
     *	Return contents
     */
    public String getContents();
    /**
     *	Return description (from META tags)
     */
    public String getDescription();
    /**
     *	Return META HREF
     */
    public String getHREF();
    /**
     * Return keywords (from META tags)
     */
    public String getKeywords();
    /**
     * Return links
     */
    public List getLinks();
    /**
     *	Return published date (from META tag)
     */
    public long getPublished();
    /**
     *	Return description (from META tags)
     */
    public boolean getRobotFollow();
    /**
     *	Return description (from META tags)
     */
    public boolean getRobotIndex();
    /**
     *		Return page title
     */
    public String getTitle();
    /**
     * Parse Content.
     */
    public void parse(InputStream in);
}
