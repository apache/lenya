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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;
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
import java.util.Iterator;
import java.util.Set;
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
    
    private File indexDir=null;
    private File excerptDir=null;
    
    private String[] fields={"contents","title"};
    private String field = "contents";
    private String queryString = "";
    private String publication_id = "";
    private String sortBy = "";
    private String sortReverse = "";
    
    private String startString = "";
    private String endString = "";
    
    private String[] words=new String[0];
    
    private Hits hits = null;
    private int hits_length=-1;
    
    private int hitsPerPage = 10;
    private int maxPages = 10;
    private int excerptOffset = 100;
    private int start = 10;
    private int end = 10; 
    private String sitemapPath = null;
    private int numberOfPubs = 1;
    
    /**
     * Ctor.
     */
    public Search() {
        super();
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
     * @param query_string The query string
     * @param publication_id The id of the publication to search
     * @param sortField The field to sort by
     * @param sortReverse Whether to sort in reverse
     * @return the hits
     */
    private Hits search(String query_string, String publication_id, String sortField, boolean sortReverse) {
        Hits hits =null;
        
        try {
            Searcher searcher=new IndexSearcher(this.indexDir.getAbsolutePath());
            Analyzer l_analyzer=new StandardAnalyzer();
            QueryParser l_queryParser = new QueryParser(this.field,l_analyzer); // Single field
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
        } catch (final IOException e) {
            addErrorMessage("IO error occured: " +e.toString());
        } catch (final ParseException e) {
            addErrorMessage("Parse error occured: " +e.toString());
        }
        
        return null;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        this.numberOfPubs = Integer.parseInt(getParameterAsString("number-of-pubs"));
        Publication[] pubs = new Publication[this.numberOfPubs];
        for(int i = 0;i < pubs.length;i++) {
            pubs[i] = new Publication();
            pubs[i].id = getParameterAsString("pub"+i+"-id");
            pubs[i].name = getParameterAsString("pub"+i+"-name");
            pubs[i].indexDir = getParameterAsString("pub"+i+"-index-dir");
            pubs[i].searchFields = getParameterAsString("pub"+i+"-search-fields");
            pubs[i].excerptDir = getParameterAsString("pub"+i+"-excerpt-dir");
            pubs[i].prefix = getParameterAsString("pub"+i+"-prefix");
        }
        
        this.hitsPerPage = Integer.parseInt(getParameterAsString("max-hits-per-page"));
        this.maxPages = Integer.parseInt(getParameterAsString("max-pages"));
        this.excerptOffset = Integer.parseInt(getParameterAsString("excerpt-offset"));
        
        // Read parameters from query string
        this.queryString = getParameterAsString("queryString");
        this.publication_id = getParameterAsString("publication-id");
        this.sortBy = getParameterAsString("sortBy");
        this.sortReverse = getParameterAsString("sortReverse");
        
        this.startString = getParameterAsString("start");
        this.endString = getParameterAsString("end");
        this.start=new Integer(this.startString).intValue();
        if(this.endString == null){
            this.end=this.hitsPerPage;
        }
        else{
            this.end=new Integer(this.endString).intValue();
        }
        
        // Find the number of the selected publication
        int whichPublication=0;
        for (int i = 0;i < pubs.length;i++) {
            if (pubs[i].id.equals(this.publication_id)) {
                whichPublication = i;
            }
        }
        
        
        // Get all search fields
        Vector myFields = new Vector();
        Set keys = getParameters().keySet();
        
        for (Iterator i = keys.iterator(); i.hasNext();) {
            Object key = i.next();
            
            if (key instanceof String) {
                String keyString = (String) key;
                
                if (keyString.indexOf(".fields") > 0) {// looking for field parameters
                    StringTokenizer st = new StringTokenizer(keyString, ".");
                    int length = st.countTokens();
                    String fieldPublicationId = st.nextToken();
                    if (fieldPublicationId.equals(this.publication_id) || fieldPublicationId.equals("dummy-index-id")) {
                        st.nextToken(); // Ignore "fields" token
                        if (length == 2) { // radio or select
                            myFields.addElement(getParameterAsString(keyString));
                        } else if (length == 3) { // checkbox
                            myFields.addElement(st.nextToken());
                        } else {
                            // something is wrong
                        }
                    }
                }
            }
        }
        if (myFields.size() > 0) {
            this.field = (String)myFields.elementAt(0);
            this.fields = new String[myFields.size()];
            for (int i = 0; i < myFields.size(); i++) {
                this.fields[i] = (String)myFields.elementAt(i);
            }
        }
        
        // Set index and excerpt dir
        String param_index_dir=pubs[whichPublication].indexDir;
        if(param_index_dir.charAt(0) == '/'){
            this.indexDir=new File(param_index_dir);
        }
        else{
            this.indexDir=new File(this.sitemapPath+File.separator+param_index_dir);
        }
        String param_excerpt_dir=pubs[whichPublication].excerptDir;
        if(param_excerpt_dir.charAt(0) == '/'){
            this.excerptDir=new File(param_excerpt_dir);
        }
        else{
            this.excerptDir=new File(this.sitemapPath+File.separator+param_excerpt_dir);
        }
        
        // hitsPerPage
        // maxPages
        // excerptOffset
        
        // Get sitemap path
        /*        Source input_source=this.getResolver().resolveURI("");
         String sitemapPath=input_source.getURI();
         sitemapPath=sitemapPath.substring(5); // Remove "file:" protocol
         */        
        
        
        
    }
    
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        if(this.queryString != null && this.queryString.length() != 0 && this.publication_id != null 
                && this.publication_id.length() > 0){
            
            if (this.sortBy.equals("score")) {
                this.hits = search(this.queryString, this.publication_id, null, false);
            } else {
                if (this.sortReverse.equals("true")) {
                    this.hits = search(this.queryString, this.publication_id, this.sortBy, true);
                } else {
                    this.hits = search(this.queryString, this.publication_id, this.sortBy, false);
                }
            }
            
            if(this.hits != null){
                this.hits_length=this.hits.length();
            } else{
                this.hits_length=-1;
            }
            //publication_id
            // pubs[whichPublication].name
            // pubs[whichPublication].prefix
            //sortBy
            //queryString
            Vector twords=new Vector();
            
            StringTokenizer st=new StringTokenizer(this.queryString," ");
            while(st.hasMoreTokens()){
                String word=(String)st.nextElement();
                if(!(word.equals("OR") || word.equals("AND"))){
                    //word
                    twords.addElement(word);
                }
            }
            this.words=new String[twords.size()];
            for(int i=0;i < twords.size();i++){
                this.words[i]=(String)twords.elementAt(i);
            }
            //start
            //end
            
            try {
                Analyzer ll_analyzer=new StandardAnalyzer();
                QueryParser queryParser = new QueryParser(this.field,ll_analyzer);
                //MultiFieldQueryParser queryParser = new MultiFieldQueryParser("contents",ll_analyzer);
                queryParser.setOperator(QueryParser.DEFAULT_OPERATOR_AND);
                Query ll_query = queryParser.parse(queryString);
                //Query ll_query = queryParser.parse(queryString,fields,ll_analyzer);
                // ll_query.toString("contents");
            } catch (final ParseException e) {
                addErrorMessage("Parse error occured: " +e.toString());
            }
            
        }
        else{
            this.hits_length=-1;
            this.hits=null;
        }
        
        if(hits != null)
            generateExtracts();
    }

    /**
     * @throws IOException
     */
    private void generateExtracts() throws IOException {
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
                    
                    File excerptFile=new File(this.excerptDir+File.separator+lurl);
                    if((ltitle != null) && (ltitle.length() > 0)){
                    }
                    else{
                    }
                    if((mime_type != null) && (mime_type.length() > 0)){
                    }
                    else{
                    }
                    
                    try {
                        ReTokenizeFile rtf=new ReTokenizeFile();
                        rtf.setOffset(excerptOffset);
                        String excerpt=rtf.getExcerpt(excerptFile,words);
                        if(excerpt != null){
                            excerpt=rtf.emphasizeAsXML(rtf.tidy(excerpt),words);                                
                        }
                    } catch (final FileNotFoundException e) {
                        addErrorMessage("File not found error: " +e.toString());
                    } catch (final IOException e) {
                        addErrorMessage("IO error occured: " +e.toString());
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
    
    /**
     *
     */
    String getPercent(float score){
        return ""+java.lang.Math.round(score*100.0);
    }
    
}