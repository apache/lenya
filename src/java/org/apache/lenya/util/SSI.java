package org.apache.lenya.util;

import java.io.*;
/*
import org.wyona.xpipe.*;
import org.wyona.xpipe.util.*;
import org.wyona.xpipe.filter.Filter;
import org.wyona.xpipe.Processor;
*/

/**
 *
 */
public class SSI {
    static String fileinc = "<!--#include file=\"";
    static String virtinc = "<!--#include virtual=\"";


    /**
     *
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java org.apache.lenya.util.SSI <file> -o file.out");
            return;
        }

        try {
            OutputStream out = System.out;
            out = new FileOutputStream("ssi-out.html");
            new SSI().includeFile(args[0], false, out);
            out.close();
        } catch (Exception e) {
            System.err.println(".main(): " + e);
        }
    }


    /**
     * Read the specified file and parse server side include instructions
     *
     * @param fileName The file path to read in
     * @param virtual Whether the fileName parameter is absolut or relativ to the document root of the web server
     * @param out The OutputStream where to write output to
     */
    public void includeFile(String fileName, boolean virtual, OutputStream out) throws IOException {
		if (virtual) {
			String documentRoot = ""; //(String) r_request.get("Request.DOCUMENT_ROOT");
			if (documentRoot != null) {
				fileName = documentRoot + fileName;
			}
		}
		
		System.err.println("Including file: "+fileName);
		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
		parseStream(bis, out);
	}

/** Parse server side include instruction in the input stream and put the input
 * stream together with the includes to the output stream
 * @param in The input stream with instruction inline
 * @param out The output stream
 */	
	public void parseStream(InputStream in, OutputStream out)
							throws IOException {
		int type = 0;
		int count = 0;
		int c = -1;
		while ((c = in.read()) != -1) {
			if (count < 13 && c == fileinc.charAt(count)) {
				count++;
				//System.err.println("Matched shared character("+count+"): "+(char) c);
				continue;
			} else if (count == 13) {
				if (c == fileinc.charAt(count)) {
					type = 19;
					count++;
					//System.err.println("Matched file character");
					continue;
				} else if (c == virtinc.charAt(count)) {
					type = 22;
					count++;
					//System.err.println("Matched virt character");
					continue;
				} else {
					out.write(fileinc.substring(0, count).getBytes());
					type = 0;
					count = 0;
				}
			} else if (count > 13) {
				int oldcount = count;
				if (type == 19 && c == fileinc.charAt(count)) {
					count++;
					//System.err.println("Matched file character("+count+"): "+(char)c);
				} else if (type == 22 && c == virtinc.charAt(count)) {
					count++;
					//System.err.println("Matched virt character("+count+"): "+(char)c);
				} else {
					String outs = (type == 19) ? fileinc : virtinc;
					out.write(outs.substring(0, count).getBytes());
					count = 0;
					type = 0;
				}
				if (count >= type) {
					StringBuffer fName = new StringBuffer();
					while ((c = in.read()) != -1 && c != '"') {
						fName.append((char) c);
					}
					while ((c = in.read()) != -1 && c != '>');
					includeFile(fName.toString(), (type == 22), out);
					count = 0;
					type = 0;
					continue;
				}
				if (oldcount != count) continue;
			} else {
				if (count > 0) {
					out.write(fileinc.substring(0, count).getBytes());
				}
				count = 0;
				type = 0;
			}
			out.write(c);
		}
	}
    
}
