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

package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceResolver;

/**
 * @version $Id$
 */
public final class SourceUtil {

    /**
     * <p>Copies one Source to another using a source buffer i.e.
     * the source Source is buffered before it is copied to its final destination.</p>
     * <p>The optional buffering is sometimes useful, if the source Source somehow depends on
     * the destination Source. This situation may occur e.g. if source Source is a Cocoon pipeline.</p> 
     * <p><em>NOTE:</em>o.a.e..s.SourceUtil.copy does not close streams on an exception!!</p>
     * @param source
     * @param destination
     * @param useBuffer If true, the source data will be read into a buffer before it is written to the final destination.
     * @throws IOException If an error occures.
     */
    public static void copy(Source source, ModifiableSource destination, boolean useBuffer) 
    throws IOException {
        InputStream sourceInputStream = null;
        OutputStream destOutputStream = null;
        try {
            sourceInputStream = source.getInputStream();
            destOutputStream = destination.getOutputStream();
            if(useBuffer) {
                final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
                CopyUtils.copy(sourceInputStream, sourceBos);
                CopyUtils.copy(sourceBos.toByteArray(), destOutputStream);
            }
            else
                CopyUtils.copy(sourceInputStream, destOutputStream);
        } finally {
            if(destOutputStream != null) {
                destOutputStream.flush();
                destOutputStream.close();    
            }
            if(sourceInputStream != null) {
                sourceInputStream.close();
            }
        }       
    }
    
    /**
     * Copies one Source to another. 
     * The source Source is optionally buffered.
     * @param resolver The SourceResolver to use for lookin up Sources.
     * @param sourceUri The source to be copied.
     * @param destUri The URI to copy to.
     * @param useBuffer If true, the source Source is buffered before copied to the final destination.
     * @throws IOException If an error occures.
     * @throws SourceException If the destination is not modifiable.
     * @see #copy(Source, ModifiableSource, boolean)
     */
    public static void copy(SourceResolver resolver, String sourceUri, String destUri, boolean useBuffer) 
    throws IOException, SourceException {
        Source source = null;
        Source dest = null;
        try {
            source = resolver.resolveURI(sourceUri);
            dest = resolver.resolveURI(destUri);
            
            if(!(dest instanceof ModifiableSource))
                throw new SourceException("Destination '"+ dest.getURI() + "' is not modifiable.");
            
            copy(source, (ModifiableSource) dest, useBuffer);            
        }
        finally {
            if(source != null)
                resolver.release(source);
            if(dest != null)
                resolver.release(dest);
        }
    }
    
    /** 
     * Copies a Source without buffering.
     * @param resolver A SourceResolver instance.
     * @param sourceUri The source URI to copy from.
     * @param destUri The destination URI to copy to.
     * @throws IOException If an error occures.
     * @see #copy(SourceResolver, String, String, boolean)
     */
    public static void copy(SourceResolver resolver, String sourceUri, String destUri) 
    throws IOException {
        copy(resolver, sourceUri, destUri, false);
    }
}
