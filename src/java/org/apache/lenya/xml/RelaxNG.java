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

/* $Id$  */

package org.apache.lenya.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.xml.EntityResolver;
import org.apache.log4j.Logger;

/**
 * Validate XML Document with RELAX NG Schema
 */
public class RelaxNG {
    private static final Logger log = Logger.getLogger(RelaxNG.class);

    /**
     * Command line interface
     * @param args Command line args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: relaxng.rng sample.xml");
            return;
        }

        try {
            String message = RelaxNG.validate(new File(args[0]), new File(args[1]), null);
            if (message == null) {
                System.out.println("Document is valid");
            } else {
                System.out.println("Document not valid: " + message);
            }
        } catch (IOException e) {
            log.error("" +e.toString());
        }
    }

    /**
     * Validates an XML file against a schema.
     * @param schema The schema file.
     * @param xml The XML file.
     * @return A string. FIXME: what does this mean?
     * @throws IOException if an error occurs.
     */
    public static String validate(File schema, File xml, ServiceManager manager) throws IOException {

        InputSource schemaInputSource = ValidationDriver.uriOrFileInputSource(schema
                .getAbsolutePath());
        InputSource xmlInputSource = ValidationDriver.uriOrFileInputSource(xml.getAbsolutePath());

        return validate(schemaInputSource, xmlInputSource, manager);
    }

    /**
     * Validates an XML input source against a schema.
     * @param schemaInputSource The schema input source.
     * @param xmlInputSource The XML input source.
     * @return A string.
     * @throws IOException if an error occurs.
     */
    public static String validate(InputSource schemaInputSource, InputSource xmlInputSource, ServiceManager manager)
            throws IOException {
        
        ByteArrayOutputStream error;
        EntityResolver entityResolver = null;
        try {
            
            entityResolver = (EntityResolver) manager.lookup(Validator.ROLE);
            PropertyMapBuilder properties = new PropertyMapBuilder();
            error = new ByteArrayOutputStream();
            ErrorHandlerImpl eh = new ErrorHandlerImpl(
                    new BufferedWriter(new OutputStreamWriter(error)));
            
            ValidateProperty.ERROR_HANDLER.put(properties, eh);
            ValidateProperty.ENTITY_RESOLVER.put(properties, entityResolver);
            
            SchemaReader schemaReader = new AutoSchemaReader();
            ValidationDriver driver = new ValidationDriver(properties.toPropertyMap(), schemaReader);
            if (driver.loadSchema(schemaInputSource)) {
                if (driver.validate(xmlInputSource)) {
                    log.debug("" + error);
                    return null;
                } 
            	log.error("" + error);
            	return "" + error;
            }
            throw new IOException("Could not load schema!\n" + error);
        } catch (final Exception e) {
            throw new IOException("Could not load schema!\n" + e);
        }
        finally {
            if (entityResolver != null) {
                manager.release(entityResolver);
            }
        }
    }

}