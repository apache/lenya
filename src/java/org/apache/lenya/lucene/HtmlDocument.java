package org.wyona.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

// Imports commented out since there is a name clash and fully
// qualified class names will be used in the code.  Imports are
// left for ease of maintenance.
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.Document;
//import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;

/**
 *  The <code>HtmlDocument</code> class creates a Lucene {@link
 *  org.apache.lucene.document.Document} from an HTML document. <P>
 *
 *  It does this by using JTidy package. It can take input input
 *  from {@link java.io.File} or {@link java.io.InputStream}.
 *
 *@author     Erik Hatcher
 *@created    October 27, 2001
 */
public class HtmlDocument {
    private Element rawDoc;


    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------

    /**
     *  Constructs an <code>HtmlDocument</code> from a {@link
     *  java.io.File}.
     *
     *@param  file             the <code>File</code> containing the
     *      HTML to parse
     *@exception  IOException  if an I/O exception occurs
     *@since
     */
    public HtmlDocument(File file) throws IOException {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        org.w3c.dom.Document root = 
                    tidy.parseDOM(new FileInputStream(file), null);
        rawDoc = root.getDocumentElement();
    }


    /**
     *  Constructs an <code>HtmlDocument</code> from an {@link
     *  java.io.InputStream}.
     *
     *@param  is               the <code>InputStream</code>
     *      containing the HTML
     *@exception  IOException  if I/O exception occurs
     *@since
     */
    public HtmlDocument(InputStream is) throws IOException {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        org.w3c.dom.Document root = tidy.parseDOM(is, null);
        rawDoc = root.getDocumentElement();
    }


    /**
     *  Creates a Lucene <code>Document</code> from an {@link
     *  java.io.InputStream}.
     *
     *@param  is
     *@return org.apache.lucene.document.Document
     *@exception  IOException
     */
    public static org.apache.lucene.document.Document
                   getDocument(InputStream is) throws IOException {
        HtmlDocument htmlDoc = new HtmlDocument(is);
        org.apache.lucene.document.Document luceneDoc =
                new org.apache.lucene.document.Document();

        luceneDoc.add(Field.Text("title", htmlDoc.getTitle()));
        luceneDoc.add(Field.Text("contents", htmlDoc.getBody()));

        return luceneDoc;
    }


    //-------------------------------------------------------------
    // Public methods
    //-------------------------------------------------------------

    /**
     *  Creates a Lucene <code>Document</code> from a {@link
     *  java.io.File}.
     *
     *@param  file
     *@return org.apache.lucene.document.Document
     *@exception  IOException
     */
    public static org.apache.lucene.document.Document
                           Document(File file) throws IOException {
        HtmlDocument htmlDoc = new HtmlDocument(file);
        org.apache.lucene.document.Document luceneDoc =
                new org.apache.lucene.document.Document();

        luceneDoc.add(Field.Text("title", htmlDoc.getTitle()));
        luceneDoc.add(Field.Text("contents", htmlDoc.getBody()));

        String contents = null;
        BufferedReader br =
                          new BufferedReader(new FileReader(file));
        StringWriter sw = new StringWriter();
        String line = br.readLine();
        while (line != null) {
            sw.write(line);
            line = br.readLine();
        }
        br.close();
        contents = sw.toString();
        sw.close();

        luceneDoc.add(Field.UnIndexed("rawcontents", contents));

        return luceneDoc;
    }


    //-------------------------------------------------------------
    // Private methods
    //-------------------------------------------------------------

    /**
     *  Runs <code>HtmlDocument</code> on the files specified on
     *  the command line.
     *
     *@param  args           Command line arguments
     *@exception  Exception  Description of Exception
     */
    private static void main(String args[]) throws Exception {
//         HtmlDocument doc = new HtmlDocument(new File(args[0]));
//         System.out.println("Title = " + doc.getTitle());
//         System.out.println("Body  = " + doc.getBody());

        HtmlDocument doc =
          new HtmlDocument(new FileInputStream(new File(args[0])));
        System.out.println("Title = " + doc.getTitle());
        System.out.println("Body  = " + doc.getBody());
    }


    /**
     *  Gets the title attribute of the <code>HtmlDocument</code>
     *  object.
     *
     *@return    the title value
     */
    public String getTitle() {
        if (rawDoc == null) {
            return null;
        }

        String title = "";

        NodeList nl = rawDoc.getElementsByTagName("title");
        if (nl.getLength() > 0) {
            Element titleElement = ((Element) nl.item(0));
            Text text = (Text) titleElement.getFirstChild();
            if (text != null) {
                title = text.getData();
            }
        }
        return title;
    }


    /**
     *  Gets the bodyText attribute of the
     *  <code>HtmlDocument</code> object.
     *
     *@return    the bodyText value
     */
    public String getBody() {
        if (rawDoc == null) {
            return null;
        }

        String body = "";
        NodeList nl = rawDoc.getElementsByTagName("body");
        if (nl.getLength() > 0) {
            body = getBodyText(nl.item(0));
            System.out.println("HtmlDocument.getBody(): "+body);
        }
        return body;
    }


    /**
     *  Gets the bodyText attribute of the
     *  <code>HtmlDocument</code> object.
     *
     *@param  node  a DOM Node
     *@return       The bodyText value
     */
    private String getBodyText(Node node) {
        NodeList nl = node.getChildNodes();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if(child.getNodeName().equals("span")){
                      org.w3c.dom.Attr attribute=((Element)child).getAttributeNode("class");
                      if(attribute != null){
                        if(attribute.getValue().equals("lucene-no-index")){
                          System.out.println("HtmlDocument.getBodyText(): ignore span!");
                          break;
                          }
                        }
                      System.out.println("HtmlDocument.getBodyText(): accept span!");
                      }
                    buffer.append(getBodyText(child));
                    buffer.append(" ");
                    break;
                case Node.TEXT_NODE:
                    buffer.append(((Text) child).getData());
                    break;
            }
        }
        return buffer.toString();
    }
}

