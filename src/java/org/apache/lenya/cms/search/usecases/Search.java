/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.cms.search.usecases;

import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.component.ComponentException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.excalibur.source.Source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.lenya.lucene.ReTokenizeFile;
import org.apache.lenya.lucene.Publication;


/**
 * Usecase to search a publication.
 * 
 * @version $Id$
 */
public class Search extends DocumentUsecase {
    
    // File workDir = (File)super.getContext().get(Constants.CONTEXT_WORK_DIR);
    
    File indexDir=null;
    File excerptDir=null;
    
    String[] fields={"contents","title"};
    String field = "contents";
    
    /**
     * Ctor.
     */
    public Search() {
        super();
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
    }
    
    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
        // do nothing
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }
    
    /**
     * Search index
     */
    public Hits search(String query_string, String publication_id, String sortField, boolean sortReverse) 
        throws IOException{
        Hits hits =null;
        
        try{
            Searcher searcher=new IndexSearcher(indexDir.getAbsolutePath());
            Analyzer l_analyzer=new StandardAnalyzer();
            
            QueryParser l_queryParser = new QueryParser(field,l_analyzer); // Single field
            //MultiFieldQueryParser l_queryParser = new MultiFieldQueryParser(fields[0], l_analyzer); // Multiple fields
            
            l_queryParser.setOperator(QueryParser.DEFAULT_OPERATOR_AND);
            
            getLogger().debug(query_string);
            Query l_query = l_queryParser.parse(query_string); // Single field
            //Query l_query = l_queryParser.parse(query_string,fields,l_analyzer); // Multiple fields
            
            if (sortField != null) {
                Sort sort = new Sort(sortField, sortReverse);
                hits = searcher.search(l_query, sort);
            } else {
                hits = searcher.search(l_query);
            }
            if(hits != null){
                return hits;
            }
        }
        catch(IOException e){
            getLogger().error(".search(): EXCEPTION: "+e);
            throw e;
        }
        catch(Exception e){
            getLogger().error(".search(): EXCEPTION: "+e);
        }
        
        return null;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Hits hits = null;
        int hits_length=-1;
        
        String[] words=new String[0];
        
        int hitsPerPage = 10;
        int maxPages = 10;
        int excerptOffset = 100;
        int start = 10;
        int end = 10; 
        String sitemapPath = null;
        int numberOfPubs = 1;
        
        // Get sitemap path
/*        Source input_source=this.getResolver().resolveURI("");
        String sitemapPath=input_source.getURI();
        sitemapPath=sitemapPath.substring(5); // Remove "file:" protocol
*/        
        
        // Read parameters from sitemap
        numberOfPubs = Integer.parseInt(getParameterAsString("number-of-pubs"));
        Publication[] pubs = new Publication[numberOfPubs];
        for(int i = 0;i < pubs.length;i++) {
            pubs[i] = new Publication();
            pubs[i].id = getParameterAsString("pub"+i+"-id");
            pubs[i].name = getParameterAsString("pub"+i+"-name");
            pubs[i].indexDir = getParameterAsString("pub"+i+"-index-dir");
            pubs[i].searchFields = getParameterAsString("pub"+i+"-search-fields");
            pubs[i].excerptDir = getParameterAsString("pub"+i+"-excerpt-dir");
            pubs[i].prefix = getParameterAsString("pub"+i+"-prefix");
        }
        
        hitsPerPage = Integer.parseInt(getParameterAsString("max-hits-per-page"));
        maxPages = Integer.parseInt(getParameterAsString("max-pages"));
        excerptOffset = Integer.parseInt(getParameterAsString("excerpt-offset"));

        // Read parameters from query string
        String queryString = getParameterAsString("queryString");
        String publication_id = getParameterAsString("publication-id");
        String sortBy = getParameterAsString("sortBy");
        String sortReverse = getParameterAsString("sortReverse");
        
        String startString = getParameterAsString("start");
        String endString = getParameterAsString("end");
        start=new Integer(startString).intValue();
        if(endString == null){
            end=hitsPerPage;
        }
        else{
            end=new Integer(endString).intValue();
        }
        
        // Find the number of the selected publication
        int whichPublication=0;
        for (int i = 0;i < pubs.length;i++) {
            if (pubs[i].id.equals(publication_id)) {
                whichPublication = i;
            }
        }
        
        
        // Get all search fields
/*        Vector myFields = new Vector();
        Enumeration parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String parameterName=(String)parameterNames.nextElement();
            String value=request.getParameter(parameterName);
            
            if (parameterName.indexOf(".fields") > 0) { // looking for field parameters
                StringTokenizer st = new StringTokenizer(parameterName, ".");
                int length = st.countTokens();
                String fieldPublicationId = st.nextToken();
                if (fieldPublicationId.equals(publication_id) || fieldPublicationId.equals("dummy-index-id")) {
                    st.nextToken(); // Ignore "fields" token
                    if (length == 2) { // radio or select
                        myFields.addElement(value);
                    } else if (length == 3) { // checkbox
                        myFields.addElement(st.nextToken());
                    } else {
                        // something is wrong
                    }
                }
            }
        }
        
        if (myFields.size() > 0) {
            field = (String)myFields.elementAt(0);
            fields = new String[myFields.size()];
            for (int i = 0; i < myFields.size(); i++) {
                fields[i] = (String)myFields.elementAt(i);
            }
        }
*/        
        // Set index and excerpt dir
        String param_index_dir=pubs[whichPublication].indexDir;
        if(param_index_dir.charAt(0) == '/'){
            indexDir=new File(param_index_dir);
        }
        else{
            indexDir=new File(sitemapPath+File.separator+param_index_dir);
        }
        String param_excerpt_dir=pubs[whichPublication].excerptDir;
        if(param_excerpt_dir.charAt(0) == '/'){
            this.excerptDir=new File(param_excerpt_dir);
        }
        else{
            excerptDir=new File(sitemapPath+File.separator+param_excerpt_dir);
        }
        
        for(int i = 0;i < pubs.length;i++) {
            // pubs[i].id
            // pubs[i].name
            // pubs[i].indexDir
            
            String[] searchFields = pubs[i].getFields();
            if (searchFields != null) {
                for (int k = 0; k < searchFields.length; k++) {
                    // searchFields[k];
                }
            } else {
                // pubs[i].searchFields;
            }
            
            // pubs[i].excerptDir;
            // pubs[i].prefix;
        }
        
        // hitsPerPage
        // maxPages
        // excerptOffset
        
        
        if(queryString != null && queryString.length() != 0 && publication_id != null 
                && publication_id.length() > 0){
            
            try {
                if (sortBy.equals("score")) {
                    hits = search(queryString, publication_id, null, false);
                } else {
                    if (sortReverse.equals("true")) {
                        hits = search(queryString, publication_id, sortBy, true);
                    } else {
                        hits = search(queryString, publication_id, sortBy, false);
                    }
                }
            } catch(Exception e) {
            }
            
            if(hits != null){
                hits_length=hits.length();
            }
            else{
                hits_length=-1;
                hits=null;
            }
            //publication_id
            // pubs[whichPublication].name
            // pubs[whichPublication].prefix
            //sortBy
            //queryString
            if(queryString != null){
                Vector twords=new Vector();
                
                StringTokenizer st=new StringTokenizer(queryString," ");
                while(st.hasMoreTokens()){
                    String word=(String)st.nextElement();
                    if(!(word.equals("OR") || word.equals("AND"))){
                        //word
                        twords.addElement(word);
                    }
                }
                words=new String[twords.size()];
                for(int i=0;i < twords.size();i++){
                    words[i]=(String)twords.elementAt(i);
                }
            }
            //start
            //end
            
            for (int i = 0; i < fields.length; i++) {
                // fields[i];
            }
            
            try{
                Analyzer ll_analyzer=new StandardAnalyzer();
                QueryParser queryParser = new QueryParser(field,ll_analyzer);
                //MultiFieldQueryParser queryParser = new MultiFieldQueryParser("contents",ll_analyzer);
                queryParser.setOperator(QueryParser.DEFAULT_OPERATOR_AND);
                Query ll_query = queryParser.parse(queryString);
                //Query ll_query = queryParser.parse(queryString,fields,ll_analyzer);
                // ll_query.toString("contents");
            }
            catch(Exception e){
            }
            
        }
        else{
            hits_length=-1;
            hits=null;
        }
        
        if(hits != null){
            //  hits_length
            if(hits_length > 0){
                for (int i=start-1;i <= end-1;i++) {
                    if((i < 0) || (i >= hits.length())){
                        continue;
                    }
                    Document ldoc=hits.doc(i);
                    Enumeration lfields = ldoc.fields();
                    String lpath=ldoc.get("path");
                    
                    String lurl=ldoc.get("url");
                    String ltitle=ldoc.get("title");
                    String mime_type=ldoc.get("mime-type");
                    
                    if(lpath != null){
                        //getPercent(hits.score(i);
                    }
                    else if(lurl != null){
                        while (lfields.hasMoreElements()) {
                            Field lfield = (Field)lfields.nextElement();
                            String slfield = lfield.toString();
                            // lfield.name()
                            // slfield.substring(0, slfield.indexOf("&lt;"))
                            // lfield.stringValue()
                        }
                        //getPercent(hits.score(i))
                        //hits.score(i)
                        String parent = "";
                        String filename = "";
                        String querystring = "";
                        if(lurl.lastIndexOf("/") > -1) {
                            parent = lurl.substring(0,lurl.lastIndexOf("/"));
                            filename = lurl.substring(lurl.lastIndexOf("/")+1);
                        }
                        if(lurl.indexOf("?") > -1) {
                            querystring = lurl.substring(lurl.indexOf("?"));
                        }
                        // parent
                        // filename
                        // querystring
                        // lurl
                        
                        File excerptFile=new File(excerptDir+File.separator+lurl);
                        if((ltitle != null) && (ltitle.length() > 0)){
                        }
                        else{
                        }
                        if((mime_type != null) && (mime_type.length() > 0)){
                        }
                        else{
                        }
                        
                        //String[] words={"funds","bonds"};
                        try{
                            ReTokenizeFile rtf=new ReTokenizeFile();
                            rtf.setOffset(excerptOffset);
                            String excerpt=rtf.getExcerpt(excerptFile,words);
                            if(excerpt != null){
                                excerpt=rtf.emphasizeAsXML(rtf.tidy(excerpt),words);
                                
                            }
                            else{
                                throw new Exception("excerpt == null. Maybe file does not contain the words!");
                            }
                        }
                        catch(FileNotFoundException e){
                        }
                        catch(Exception e){
                        }
                        
                    }
                }
            }
            else{
            }
            int number_of_pages=(hits.length()/hitsPerPage);
            if(number_of_pages*hitsPerPage != hits.length()){
                number_of_pages=number_of_pages+1;
            }
            if(number_of_pages > maxPages){
                number_of_pages=maxPages;
            }
            if(hits.length() == 0){
                number_of_pages=0;
            }
            else{
                for(int i=0;i < number_of_pages;i++){
                    int pstart=i*hitsPerPage+1;
                    int pend=(i+1)*hitsPerPage;
                    if(hits_length < pend){
                        pend=hits_length;
                    }
                    String type="other";
                    if(pstart == start){
                        type="current";
                    }
                    else if(pstart == start-hitsPerPage){
                        type="previous";
                    }
                    else if(pstart == start+hitsPerPage){
                        type="next";
                    }
                }
            }
        }
        
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        
    }

    /**
    *
    */
   String getPercent(float score){
       return ""+java.lang.Math.round(score*100.0);
   }

}