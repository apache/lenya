package cxa.transformation;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class SendMailTransformer
extends AbstractTransformer
implements Parameterizable, Poolable {

    public static final String NAMESPACE = "http://cxa/cocoon/sendmail";
    public static final String SENDMAIL_ELEMENT = "sendmail";
    public static final String MAILTO_ELEMENT = "mailto";
    public static final String MAILCC_ELEMENT = "mailcc";
    public static final String MAILSUBJECT_ELEMENT = "mailsubject";
    public static final String MAILBODY_ELEMENT = "mailbody";

    protected static final int MODE_NONE = 0;
    protected static final int MODE_TO = 1;
    protected static final int MODE_CC = 4;
    protected static final int MODE_SUBJECT = 2;
    protected static final int MODE_BODY = 3;

    protected int mode;
    protected StringBuffer toAddress;
    protected StringBuffer ccAddress;
    protected StringBuffer subject;
    protected StringBuffer body;

    protected String mailHost;
    protected String fromAddress;

    /**
     * Parameterizable
     */
    public void parameterize(Parameters parameters)
    throws ParameterException {
        this.mailHost = parameters.getParameter("mailhost");
        this.fromAddress = parameters.getParameter("from");
    }

    /**
     * Setup
     */
    public void setup(SourceResolver resolver,
                      Map            objectModel,
                      String         src,
                      Parameters     par)
    throws ProcessingException, SAXException, IOException {
        this.mode = MODE_NONE;
        this.toAddress = new StringBuffer();
        this.ccAddress = new StringBuffer();
        this.subject = new StringBuffer();
        this.body = new StringBuffer();
    }

    public void startElement(String uri, String name, String raw,
                             Attributes attr)
    throws SAXException {
        if (this.getLogger().isDebugEnabled() == true) {
            this.getLogger().debug("BEGIN startElement uri=" + uri +
               ", name=" + name + ", raw=" + raw + ", attr=" + attr);
        }

        if (uri != null && uri.equals(NAMESPACE) ) {
            if (name.equals(SENDMAIL_ELEMENT) == true) {
                // No need to do anything here
            } else if (name.equals(MAILTO_ELEMENT) == true) {
                this.mode = MODE_TO;
            } else if (name.equals(MAILCC_ELEMENT) == true) {
                this.mode = MODE_CC;
            } else if (name.equals(MAILSUBJECT_ELEMENT) == true) {
                this.mode = MODE_SUBJECT;
            } else if (name.equals(MAILBODY_ELEMENT) == true) {
                this.mode = MODE_BODY;
            } else {
                throw new SAXException("Unknown element " + name);
            }
        } else {
            // Not for us
            super.startElement(uri, name, raw, attr);
        }

        if (this.getLogger().isDebugEnabled() == true) {
            this.getLogger().debug("END startElement");
        }
    }

    public void endElement(String uri, String name, String raw)
    throws SAXException {
        if (this.getLogger().isDebugEnabled() == true) {
            this.getLogger().debug("BEGIN endTransformingElement uri=" + uri +
                              ", name=" + name + ", raw=" + raw);
        }

        if (uri != null && uri.equals(NAMESPACE) ) {
            if (name.equals(SENDMAIL_ELEMENT) == true) {
                if (this.getLogger().isDebugEnabled() == true) {
                    this.getLogger().debug("\nMail contents: \nTo: "+ this.toAddress +
                                     ", \nSubject: " + this.subject +
                                     ", \nBody: "+ this.body);
                }

                String text;
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.host", this.mailHost);
                    Session mailSession = Session.getInstance(props, null);

                    MimeMessage pm = new MimeMessage(mailSession);

                    // set from
                    pm.setFrom(new InternetAddress( this.fromAddress ));
                    // set to
                    pm.setRecipients(Message.RecipientType.TO,
                                     InternetAddress.parse( this.toAddress.toString() ));
                    
                    pm.setRecipients(Message.RecipientType.CC,
                                     InternetAddress.parse( this.ccAddress.toString() ));
                    
                    // set subject
                    pm.setSubject( this.subject.toString() );
                    // set date
                    pm.setSentDate(new Date());
                    // set content
                    pm.setText( this.body.toString() );
                    // send mail
                    Transport trans = mailSession.getTransport("smtp");
                    Transport.send(pm);
                    // success message
                    text = "Sending mail to " + this.toAddress + " (cc: " + this.ccAddress + ")" + " was successful.";
                } catch (Exception any) {
                    this.getLogger().error("Exception during sending of mail", any);
                    // failure message
                    text = "Sending mail to " + this.toAddress + " (cc: " + this.ccAddress + ")" + " failed!";
                }
                // create SAX events for success/failure
                super.startElement(NAMESPACE, "sendmail", "sendmail", new AttributesImpl());
                super.characters(text.toCharArray(), 0, text.length());
                super.endElement(NAMESPACE, "sendmail", "sendmail");

            } else if (name.equals(MAILTO_ELEMENT) == true) {
                // mailto received
                this.mode = MODE_NONE;
            } else if (name.equals(MAILCC_ELEMENT) == true) {
                // mailcc received
                this.mode = MODE_NONE;
            } else if (name.equals(MAILSUBJECT_ELEMENT) == true) {
                this.mode = MODE_NONE;
            } else if (name.equals(MAILBODY_ELEMENT) == true) {
                this.mode = MODE_NONE;
            } else {
                throw new SAXException("Unknown element " + name);
            }
        } else {
            // Not for us
            super.endElement(uri, name, raw);
        }

        if (this.getLogger().isDebugEnabled() == true) {
            this.getLogger().debug("END endElement");
        }
    }

    public void characters(char[] buffer, int start, int length)
    throws SAXException {
        switch (this.mode) {
            case MODE_NONE : super.characters(buffer, start, length);
                             break;
            case MODE_TO : this.toAddress.append(buffer, start, length);
                           break;
            case MODE_CC : this.ccAddress.append(buffer, start, length);
                           break;
            case MODE_SUBJECT : this.subject.append(buffer, start, length);
                           break;
            case MODE_BODY : this.body.append(buffer, start, length);
                           break;
        }
    }
}