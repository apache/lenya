package org.wyona.util;

/**
 *
 */
public class URLUtil{
/**
 *
 */
  public static void main(String[] args){
    System.out.println(URLUtil.complete("http://www.wyona.org/download/index.html","../images/wyona.jpeg"));
    System.out.println(URLUtil.complete("http://www.wyona.org/download/index.html","/images/wyona.jpeg"));
    }
/**
 *
 */
  public static String complete(String parent,String child){
    String url=child;
    String urlLowCase=child.toLowerCase();
    String currentURLPath=parent.substring(0, parent.lastIndexOf("/"));
    String rootURL=parent.substring(0, parent.indexOf("/", 8));

        if (urlLowCase.startsWith("/")) {
            url = rootURL + url;
        } else if (urlLowCase.startsWith("./")) {
            url = currentURLPath + url.substring(1, url.length());
        } else if (urlLowCase.startsWith("../")) {
            int back = 1;
            while (urlLowCase.indexOf("../", back*3) != -1) back++;
            int pos = currentURLPath.length();
            int count = back;
            while (count-- > 0) {
                pos = currentURLPath.lastIndexOf("/", pos) - 1;
            }
            url = currentURLPath.substring(0, pos+2) + url.substring(3*back, url.length());
        } else if (urlLowCase.startsWith("javascript:")) {
            // handle javascript:...
            //url = parseJavaScript(url, urlLowCase);
            System.err.println(".parseHREF(): parseJavaScript is not implemented yet");
        } else if (urlLowCase.startsWith("#")) {
            // internal anchor... ignore.
            url = null;
        } else if (urlLowCase.startsWith("mailto:")) {
            // handle mailto:...
            url = null;
        } else {
            url = currentURLPath + "/" + url;
        }

        // strip anchor if exists otherwise crawler may index content multiple times
        // links to the same url but with unique anchors would be considered unique
        // by the crawler when they should not be
        //int i;
        if (url != null) {
            int i;
            if ((i = url.indexOf("#")) != -1) {
                url = url.substring(0,i);
            }
        }
        return url;
    }
  }
