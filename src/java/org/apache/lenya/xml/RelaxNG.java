package org.apache.lenya.xml;

import org.xml.sax.InputSource;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;

/**
 * Validate XML Document with RELAX NG Schema
 */
public class RelaxNG {

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
                System.out.println("" + error);
                return null;
            } else {
                return "" + error;
            }
        } else {
            throw new Exception("Could not load schema!\n" + error);
        }
    }
}
