/*
 * Index.java
 *
 * Created on 19. März 2003, 10:45
 */

package org.lenya.lucene.index;

import java.io.File;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;

import org.lenya.lucene.IndexConfiguration;
import org.lenya.util.DebugConfiguration;
import org.lenya.xml.DOMParserFactory;
import org.lenya.xml.DOMUtil;
import org.lenya.xml.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author  hrt
 */
public class Index {
    
    public static void main(String[] argv) {
        try {
            String index = "index";
            boolean create = false;
            File root = null;
            
            String usage = "Index <lucene.xconf> <debug(true/false)>";
            
            if(argv.length != 2){
                System.err.println("Usage: " + usage);
                return;
            }
            
            IndexConfiguration ie = new IndexConfiguration(argv[0]);
            index = ie.resolvePath(ie.getIndexDir());
            root = new File(ie.resolvePath(ie.getHTDocsDumpDir()));
            if (ie.getUpdateIndexType().equals("new")){
                create=true;
            }
            else if (ie.getUpdateIndexType().equals("incremental")){
                create=false;
            }
            else {
                System.err.println("ERROR: No such update-index/@type: "+ie.getUpdateIndexType());
                return;
            }
            
            String debug = argv[1];
            if (debug.equalsIgnoreCase("false") || debug.equalsIgnoreCase("no")) {
                DebugConfiguration.setDebug(false);
            }
            else if (debug.equalsIgnoreCase("false") || debug.equalsIgnoreCase("no")) {
                DebugConfiguration.setDebug(false);
            }
            else {
                System.err.println("ERROR: <debug> must be one of 'yes', 'true', 'no', or 'false'");
            }
            
/*
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("-index")) {		  // parse -index option
          index = argv[++i];
        } else if (argv[i].equals("-create")) {	  // parse -create option
          create = true;
        } else if (i != argv.length-1) {
          System.err.println("Usage: " + usage);
          return;
        } else
          root = new File(argv[i]);
      }
 */
            
            Date start = new Date();
            
            Indexer indexer = (Indexer) ie.getIndexerClass().newInstance();
            
            DOMUtil du = new DOMUtil();
            Document config = new DOMParserFactory().getDocument(argv[0]);
            indexer.configure(du.getElement(config.getDocumentElement(), new XPath("indexer")));
            
            if (create)
                indexer.createIndex(root, index);
            else
                indexer.updateIndex(root, index);
            
            Date end = new Date();
            
            System.out.print(end.getTime() - start.getTime());
            System.out.println(" total milliseconds");
            
        } catch (Exception e) {
            /*
            System.out.println(" caught a " + e.getClass() +
            "\n with message: " + e.getMessage());
             */
            e.printStackTrace(System.out);
        }
    }
    
}
