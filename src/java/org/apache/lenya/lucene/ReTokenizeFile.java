package org.wyona.lucene;

import java.io.File;
import java.io.*;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.wyona.lucene.html.HTMLParser;

/**
 *
 */
public class ReTokenizeFile{
/**
 *
 */
  public static void main(String[] args){
    if(args.length < 2){
      System.err.println("Usage: ReTokenizeFile filename word1 word2 ...");
      return;
      }
 
    try{
      String s=null;
      s=new ReTokenizeFile().reTokenize(new File(args[0]));
      String[] words=new String[args.length-1]; //{"Cocoon","Wyona"};
      for(int i=1;i<args.length;i++){
        words[i-1]=args[i];
        }
      s=new ReTokenizeFile().getExcerpt(new File(args[0]),words);
      System.err.println(".main(): Excerpt: "+s);
      }
    catch(Exception e){
      System.err.println(".main(): "+e);
      }
    }
/**
 *
 */
  public String reTokenize(File file) throws Exception{
    System.out.println("ReTokenizeFile.reTokenize(File): Re-tokenize "+file);
    TokenStream ts=new StandardAnalyzer().tokenStream(new HTMLParser(file).getReader());

    Token token=null;
    while((token=ts.next()) != null){
      System.out.println("ReTokenizeFile.reTokenize(File): "+token.termText()+" "+token.startOffset()+" "+token.endOffset()+" "+token.type());
      }

    return file.getAbsolutePath();
    }
/**
 *
 */
  public String getExcerpt(File file,String[] words) throws Exception{
    System.out.println("ReTokenizeFile.getExcerpt(File,String[]): Get excerpt from "+file);

    java.io.Reader reader=new HTMLParser(file).getReader();
    char[] chars=new char[1024];
    int chars_read;
    java.io.Writer writer=new java.io.StringWriter();
    while((chars_read=reader.read(chars)) > 0){
      writer.write(chars,0,chars_read);
      }

    InputStream in=new FileInputStream(file.getAbsolutePath());
    byte[] buffer=new byte[1024];
    int bytes_read;
    ByteArrayOutputStream bufferOut=new ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      bufferOut.write(buffer,0,bytes_read);
      }
    in.close();

    String html=writer.toString();
    //String html=bufferOut.toString();

    //if(true) return html;

    int index=html.toLowerCase().indexOf(words[0].toLowerCase());
    int offset=20;
    int start=index-offset;
    int end=index+words[0].length()+offset;

    return html.substring(start,end);
    }
  }
