/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id: RelaxNG.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;

import org.xml.sax.InputSource;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

import org.apache.log4j.Logger;

/**
 * Validate XML Document with RELAX NG Schema
 */
public class RelaxNG {
    static Logger log = Logger.getLogger(RelaxNG.class);

    /**
     *
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: relaxng.rng sample.xml");
            return;
        }

        try {
            String message = RelaxNG.validate(new File(args[0]), new File(args[1]));
            if (message == null) {
                System.out.println("Document is valid");
            } else {
                System.out.println("Document not valid: " + message);
            }
        } catch (Exception e) {
            System.err.println("" + e);
        }
    }

    /**
     *
     */
    public static String validate(File schema, File xml) throws Exception {
        InputSource in = ValidationDriver.uriOrFileInputSource(schema.getAbsolutePath());
        PropertyMapBuilder properties = new PropertyMapBuilder();
	    ByteArrayOutputStream error = new ByteArrayOutputStream();
        ErrorHandlerImpl eh = new ErrorHandlerImpl(new BufferedWriter(new OutputStreamWriter(error)));
        ValidateProperty.ERROR_HANDLER.put(properties, eh);
        SchemaReader schemaReader = new AutoSchemaReader();
        ValidationDriver driver = new ValidationDriver(properties.toPropertyMap(), schemaReader);
        if (driver.loadSchema(in)) {
            if (driver.validate(ValidationDriver.uriOrFileInputSource(xml.getAbsolutePath()))) {
                log.debug("" + error);
                return null;
            } else {
                log.error("" + error);
                return "" + error;
            }
        } else {
            throw new Exception("Could not load schema!\n" + error);
        }
    }
}
