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

package org.apache.lenya.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.StringTokenizer;

import org.apache.commons.codec.net.BCodec;
import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 * @deprecated use cocoon mail block
 */
public class SMTP {
    private static Logger log = Logger.getLogger(SMTP.class);
    String host = null;
    int port;
    String domain = null;
    Socket socket = null;
    PrintWriter out = null;
    DataInputStream in = null;
    String errlog = null;
    String from = null;
    String to = null;
    String reply_to = null;
    String cc = null;
    String[] ccs = null;
    String bcc = null;
    String[] bccs = null;
    String subject = null;
    String data = null;
    String charset ="ISO-8859-1";
    BCodec encoder = null;
    
    /**
     *
     */
    public SMTP() {

        Configuration conf = new Configuration();
        host = conf.smtpHost;
        port = new Integer(conf.smtpPort).intValue();
        domain = conf.smtpDomain;
        log.debug(host + ":" + port + " (" + domain + ")");
    }


    /**
     * DOCUMENT ME!
     *
     * @param from DOCUMENT ME!
     * @param to DOCUMENT ME!
     * @param cc DOCUMENT ME!
     * @param bcc DOCUMENT ME!
     * @param subject DOCUMENT ME!
     * @param body DOCUMENT ME!
     */
    public void send(String from, String to, String cc, String bcc, String subject, String body) {
        From(from);
        Reply_To(from);
        To(to);
        Cc(cc);
        Bcc(bcc);
        Subject(subject);
        DATA(body);
        send();
    }

    /**
     * DOCUMENT ME!
     */
    public void send() {
        errlog = "";

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charset)), true);
            in = new DataInputStream(socket.getInputStream());
            encoder = new BCodec(charset);
            
            errlog = errlog + getResponse(220);

            errlog = errlog + "HELO " + domain + "\n";
            out.println("HELO " + domain);
            errlog = errlog + getResponse(250);

            errlog = errlog + "MAIL FROM:<" + from + ">\n";
            out.println("MAIL FROM:<" + from + ">");
            errlog = errlog + getResponse(250);

            errlog = errlog + "RCPT TO:<" + to + ">\n";
            out.println("RCPT TO:<" + to + ">");
            errlog = errlog + getResponse(250);

            for (int i = 0; i < ccs.length; i++) {
                errlog = errlog + "RCPT TO:<" + ccs[i] + ">\n";
                out.println("RCPT TO:<" + ccs[i] + ">");
                errlog = errlog + getResponse(250);
            }

            for (int i = 0; i < bccs.length; i++) {
                errlog = errlog + "RCPT TO:<" + bccs[i] + ">\n";
                out.println("RCPT TO:<" + bccs[i] + ">");
                errlog = errlog + getResponse(250);
            }

            errlog = errlog + "DATA\n";
            out.println("DATA");
            errlog = errlog + getResponse(354);

            String base64reply_to = encoder.encode(reply_to);
            errlog = errlog + "From: " + base64reply_to + "\n";
            out.println("From: " + base64reply_to);
            
            errlog = errlog + "To: " + to + "\n";
            out.println("To: " + to);

            errlog = errlog + "Reply-To: " + base64reply_to + "\n";
            out.println("Reply-To: " + base64reply_to);

            if (cc != null) {
                errlog = errlog + "Cc: " + cc + "\n";
                out.println("Cc: " + cc);
            }

            if (bcc != null) {
                errlog = errlog + "Bcc: " + bcc + "\n";
                out.println("Bcc: " + bcc);
            }
 
            String base64Subject = encoder.encode(subject);
            errlog = errlog +"Subject: " + base64Subject + "\n";
            out.println("Subject: " + base64Subject);
            
            errlog = errlog + "MIME-Version: 1.0\n";
            out.println("MIME-Version: 1.0");

            errlog = errlog + "Content-Type: text/plain; charset=" + charset +"; format=flowed\n";
            out.println("Content-Type: text/plain; charset=" + charset +"; format=flowed");

            errlog = errlog + "Content-Transfer-Encoding: quoted-printable\n";
            out.println("Content-Transfer-Encoding: quoted-printable");
            
            errlog = errlog + data + "\n.\n";
            out.println(data + "\n.");
            errlog = errlog + getResponse(250);

            errlog = errlog + "QUIT\n";
            out.println("QUIT");
            errlog = errlog + getResponse(221);
            log.debug(errlog);
        } catch (ConnectException e) {
            log.error(".send(): " + e + " (sendmail is probably not running)");

            return;
        } catch (Exception e) {
            log.error(".send(): " + e);
        }

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            log.error(this.getClass().getName() + ".send(): " + e);
        }
    }
        
    private String getResponse(int value) throws IOException {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }

        return readLine(in);
    }

    private String readLine(DataInputStream in) throws IOException {
        StringBuffer line = new StringBuffer("");

        while (in.available() > 0) {
            char character = (char) in.read();
            line.append(character);
        }

        String response = new String(line);

        return response;
    }

    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     */
    public void DATA(String data) {
        this.data = data;
    }

    /**
     * DOCUMENT ME!
     *
     * @param from DOCUMENT ME!
     */
    public void From(String from) {
        this.from = from;
        
        if (from == null) {
        	return;
        }
        if (from.indexOf("<") > 0) {
        	this.from = from.substring(from.indexOf("<")+1, from.length()-1);
        }

    }


    /**
     * DOCUMENT ME!
     *
     * @param to DOCUMENT ME!
     */
    public void To(String to) {
        this.to = to;
    }

    /**
     * DOCUMENT ME!
     *
     * @param reply_to DOCUMENT ME!
     */
    public void Reply_To(String reply_to) {
        this.reply_to = reply_to;
    }

    /**
     * DOCUMENT ME!
     *
     * @param cc DOCUMENT ME!
     */
    public void Cc(String cc) {
        if (cc == null) {
            ccs = new String[0];

            return;
        }

        this.cc = cc;

        StringTokenizer st = new StringTokenizer(cc, ",");
        ccs = new String[st.countTokens()];

        for (int i = 0; i < ccs.length; i++) {
            ccs[i] = st.nextToken();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param bcc DOCUMENT ME!
     */
    public void Bcc(String bcc) {
        if (bcc == null) {
            bccs = new String[0];

            return;
        }

        this.bcc = bcc;

        StringTokenizer st = new StringTokenizer(bcc, ",");
        bccs = new String[st.countTokens()];

        for (int i = 0; i < bccs.length; i++) {
            bccs[i] = st.nextToken();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param subject DOCUMENT ME!
     */
    public void Subject(String subject) {
        this.subject = subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int numberOfLines(String filename) {
        String string = "";
        int nlines = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            while (in.available() != 0) {
                string = reader.readLine();
                nlines++;
            }

            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return nlines;
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] loadLines(String filename) {
        String[] string = new String[numberOfLines(filename)];
        int nlines = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            while (in.available() != 0) {
                string[nlines] = reader.readLine();
                nlines++;
            }

            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return string;
    }
}
