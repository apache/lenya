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

package org.apache.lenya.lucene.html;

import java.io.IOException;


class ParserThread extends Thread {
    HTMLParser parser;

    ParserThread(HTMLParser p) {
        this.parser = p;
    }

    /**
     * Run method
     */
    public void run() { // convert pipeOut to pipeIn

        try {
            try { // parse document to pipeOut
                this.parser.HTMLDocument();
            } catch (ParseException e) {
                System.out.println("Parse Aborted: " + e.getMessage());
            } catch (TokenMgrError e) {
                System.out.println("Parse Aborted: " + e.getMessage());
            } finally {
                this.parser.pipeOut.close();

                synchronized (this.parser) {
                    this.parser.summary.setLength(HTMLParser.SUMMARY_LENGTH);
                    this.parser.titleComplete = true;
                    this.parser.notifyAll();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
