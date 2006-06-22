package org.apache.lenya.cms.ac.usecases;


import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.*;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.ac.usecases.AccessControlUsecase;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.SessionListener;

/**
 * SessionViewer usecase which gets all session objects from the SessionListener and extract the
 * necessary informations.
 * 
 * @version $Id: SessionViewer.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class SessionViewer extends AccessControlUsecase {

    private static final String IDENTITY = Identity.class.getName();

    private static final String HISTORY = "org.apache.lenya.cms.cocoon.acting.History";

    protected static final String USERS = "users";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        this.getSourceURL();

        SessionListener sessions = new SessionListener();
        WeakHashMap allSessions = sessions.getAllSessions();
        List userList = new ArrayList();

        Iterator userit = allSessions.entrySet().iterator();
        while (userit.hasNext()) {
            Map.Entry entry = (Map.Entry) userit.next();
            HttpSession nextsession = (HttpSession) entry.getValue();

            Identity identity = (Identity) nextsession.getAttribute(IDENTITY);

            User user = identity.getUser();
            if (user != null) {
                Vector history = (Vector) nextsession.getAttribute(HISTORY);
                String publicationID = getPublicationIDfromHistory(history);
                if (publicationID.equals(getPublicationIDfromURL())) {
                    userList.add(identity.getUser());
                }
            }
        }
        setParameter(USERS, userList);
    }

    /**
     * Extract the publicationID from the history information stored in the session
     * 
     * @param history The history stored in the session.. An entry looks like \/
     *            <publication-id>/authoring/index.html
     * @return A publication ID.
     */
    private String getPublicationIDfromHistory(Vector history) {

        String firstElement = history.firstElement().toString();
        String publicationID = firstElement.substring(1, firstElement.indexOf("/", 1));

        return publicationID;
    }

    /**
     * @return The publicationID from an URL.
     */
    private String getPublicationIDfromURL() {
        URLInformation info = new URLInformation(getSourceURL());
        return info.getPublicationId();
    }
}