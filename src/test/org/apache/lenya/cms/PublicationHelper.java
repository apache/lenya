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

/* $Id: PublicationHelper.java,v 1.7 2004/03/04 15:41:09 egli Exp $  */

package org.apache.lenya.cms;

import java.util.Arrays;
import java.util.List;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

public class PublicationHelper {
    
    /**
     * Constructor for PublicationHelper.
     *
     */
    protected PublicationHelper() {}
    
    /**
     * Initializes the object with the first parameters from the command line arguments
     * <code>args</code>. The remainder of the array is returned.
     * 
     * @param args The command line arguments of the test.
     * @return The remainder of the arguments after the publication parameters are extracted.
     * 
     */
    public static String[] extractPublicationArguments(String[] args) {
        String servletContextPath = args[0];
        String publicationId = args[1];
        try {
            publication =
                PublicationFactory.getPublication(
                    publicationId,
                    servletContextPath);
        } catch (PublicationException e) {
            e.printStackTrace();
        }

        List subList = Arrays.asList(args).subList(2, args.length);
        String[] remainder =
            (String[])subList.toArray(new String[subList.size()]);

        return remainder;
    }

    private static Publication publication;

    /**
     * Returns the publication.
     * @return A publication object.
     */
    public static Publication getPublication() {
        return publication;
    }
}
