/*
 * AbstractDocumentCreator.java
 *
 * Created on 21. März 2003, 10:13
 */

package org.lenya.lucene.index;

import java.io.File;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 *
 * @author  hrt
 */
public class AbstractDocumentCreator
    implements DocumentCreator {
    
    /** Creates a new instance of AbstractDocumentCreator */
    public AbstractDocumentCreator() {
    }
    
    public Document getDocument(File file, File htdocsDumpDir)
        throws Exception {
        //System.out.println(getClass().getName() + ".getDocument(): " + file);

        // make a new, empty document
        Document doc = new Document();

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        String requestURI = file.getPath().replace(File.separatorChar, '/').substring(htdocsDumpDir.getPath().length());

        //System.out.println(requestURI);
        if (requestURI.substring(requestURI.length() - 8).equals(".pdf.txt")) {
            requestURI = requestURI.substring(0, requestURI.length() - 4); // Remove .txt extension from PDF text file

        }

        doc.add(Field.UnIndexed("url", requestURI));

        // Add the mime-type as a field named "mime-type"
        if (requestURI.substring(requestURI.length() - 5).equals(".html")) {
            doc.add(Field.UnIndexed("mime-type", "text/html"));
        } else if (requestURI.substring(requestURI.length() - 4).equals(".txt")) {
            doc.add(Field.UnIndexed("mime-type", "text/plain"));
        } else if (requestURI.substring(requestURI.length() - 4).equals(".pdf")) {
            doc.add(Field.UnIndexed("mime-type", "application/pdf"));
        } else {
            doc.add(Field.UnIndexed("mime-type", "null"));
        }

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        doc.add(Field.Keyword("modified", DateField.timeToString(file.lastModified())));

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        doc.add(new Field("uid", uid(file, htdocsDumpDir), false, true, false));

        return doc;
    }

    /**
     * Append path and date into a string in such a way that lexicographic sorting gives the same
     * results as a walk of the file hierarchy.  Thus null (\u0000) is used both to separate
     * directory components and to separate the path from the date.
     *
     * @param f DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String uid(File f, File htdocsDumpDir) {
        String requestURI = f.getPath().substring(htdocsDumpDir.getPath().length());
        String uid = requestURI.replace(File.separatorChar, '\u0000') + "\u0000" +
            DateField.timeToString(f.lastModified());

        return uid;
    }

}
