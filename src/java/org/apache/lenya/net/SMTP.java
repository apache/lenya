/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
/*
<License>
</License>
 */
package org.apache.lenya.net;

import org.apache.log4j.Category;

import java.io.*;

import java.net.*;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @author Martin Luethi (added log4j)
 * @version 1.8.27
 *
 * @deprecated 0.10.9
 */
public class SMTP {
    static Category log = Category.getInstance(SMTP.class);
    String host = null;
    int port;
    String domain = null;
    Socket socket = null;
    PrintStream out = null;
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
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        SMTP mail = new SMTP();

        if (args.length != 1) {
            System.err.println("Usage: java " + mail.getClass().getName() +
                " michael.wechner@lenya.com");

            return;
        }

        try {
            String to = args[0];
            System.out.print("Subject: ");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String subject = br.readLine();
            String line = null;
            String body = "";

            while (true) {
                line = br.readLine();

                if (line.equals(".")) {
                    break;
                }

                body = body + "\n" + line;
            }

            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: \n" + body);

            String from = "contact@lenya.org";
            String cc = null;
            String bcc = null;
            mail.send(from, to, cc, bcc, subject, body);
            log.debug(mail.errlog);
        } catch (Exception e) {
            log.error("SMTP: " + e);
        }
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
            out = new PrintStream(socket.getOutputStream(), true);
            in = new DataInputStream(socket.getInputStream());

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

            errlog = errlog + "From: " + from + "\n";
            out.println("From: " + from);
            errlog = errlog + "To: " + to + "\n";
            out.println("To: " + to);
            errlog = errlog + "Reply-To: " + reply_to + "\n";
            out.println("Reply-To: " + reply_to);

            if (cc != null) {
                errlog = errlog + "Cc: " + cc + "\n";
                out.println("Cc: " + cc);
            }

            if (bcc != null) {
                errlog = errlog + "Bcc: " + bcc + "\n";
                out.println("Bcc: " + bcc);
            }

            errlog = errlog + "Subject: " + subject + "\n";
            out.println("Subject: " + subject);

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
