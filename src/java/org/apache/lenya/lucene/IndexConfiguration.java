/*
 * $Id: IndexConfiguration.java,v 1.4 2003/04/24 13:53:00 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2003 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.lucene;

import org.apache.lenya.xml.DOMParserFactory;
import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.xml.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.avalon.excalibur.io.FileUtil;

import org.apache.log4j.Category;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 */
public class IndexConfiguration {
    static Category log = Category.getInstance(IndexConfiguration.class);
    private String configurationFilePath;
    private String update_index_type;
    private String index_dir;
    private String htdocs_dump_dir;
    private Class indexerClass;

    /**
     * Creates a new IndexConfiguration object.
     *
     * @param configurationFilePath DOCUMENT ME!
     */
    public IndexConfiguration(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;

        File configurationFile = new File(configurationFilePath);

        try {
            Document document = new DOMParserFactory().getDocument(configurationFilePath);
            configure(document.getDocumentElement());
        } catch (Exception e) {
            log.error("Cannot load publishing configuration! ", e);
            System.err.println("Cannot load publishing configuration! " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: org.apache.lenya.lucene.IndexConfiguration lucene.xconf");

            return;
        }

        IndexConfiguration ic = new IndexConfiguration(args[0]);
        String parameter;

        parameter = ic.getUpdateIndexType();
        System.out.println("Index type: " + parameter);

        parameter = ic.getIndexDir();
        System.out.println("Index dir: " + parameter);
        System.out.println("Index dir (resolved): " + ic.resolvePath(parameter));

        parameter = ic.getHTDocsDumpDir();
        System.out.println("htdocs_dump: " + parameter);
        System.out.println("htdocs_dump (resolved): " + ic.resolvePath(parameter));

        System.out.println("Indexer class: " + ic.getIndexerClass());
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void configure(Element root) throws Exception {
        DOMUtil du = new DOMUtil();
        update_index_type = du.getAttributeValue(root, new XPath("update-index/@type"));
        index_dir = du.getAttributeValue(root, new XPath("index-dir/@src"));
        htdocs_dump_dir = du.getAttributeValue(root, new XPath("htdocs-dump-dir/@src"));

        String indexerClassName = du.getAttributeValue(root, new XPath("indexer/@class"));
        indexerClass = Class.forName(indexerClassName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUpdateIndexType() {
        log.debug(".getUpdateIndexType(): " + update_index_type);

        return update_index_type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getIndexDir() {
        log.debug(".getIndexDir(): " + index_dir);

        return index_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTDocsDumpDir() {
        log.debug(".getHTDocsDumpDir(): " + htdocs_dump_dir);

        return htdocs_dump_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getIndexerClass() {
        log.debug(".getIndexerClass(): " + indexerClass);

        return indexerClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String resolvePath(String path) {
        if (path.indexOf(File.separator) == 0) {
            return path;
        }

        return FileUtil.catPath(configurationFilePath, path);
    }
}
