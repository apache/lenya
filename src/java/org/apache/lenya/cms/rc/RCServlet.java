package org.wyona.cms.rc;

/*
//import org.wyona.util.MultipartRequest;
//import org.wyona.xps.authoring.MountPoint;
//import org.wyona.xps.authoring.MountPoints;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.*;


import java.io.*;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;
*/

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;

/*
import org.w3c.dom.*;
import org.wyona.security.Identification;
import org.wyona.util.FileToUpload;
import org.wyona.xml.DOMParserFactory;
import org.wyona.xml.DOMWriter;
import org.wyona.xpipe.Processor;
import org.wyona.xps.XPSAssembler;
import org.wyona.xps.XPSFileOutputStream;
import org.wyona.xps.dev.URLObjectNotification;
import org.wyona.xps.dev.URLObjectSpace;
import org.wyona.xps.repository.MountPoint;
import org.wyona.xps.repository.MountPoints;
import org.wyona.xps.signalling.StatusChangeSignalHandler;
import org.wyona.xsl.XSLProcessorFactory;
*/

/**
 * @author     Michael Wechner
 * @author     Edith Chevrier
 * @version    2002.7.16
 */
public class RCServlet extends javax.servlet.http.HttpServlet {
  static Category log=Category.getInstance(RCServlet.class);

/*
		String servletZone = "servlets";

		public MountPoints mps = null;
*/


	/**
	 *@return    The ServletInfo value
	 */
	public String getServletInfo() {
		return this.getClass().getName();
	}


	/**
	 *@param  config                Description of Parameter
	 *@exception  ServletException  Description of Exception
	 */
	public void init(ServletConfig config)
			 throws ServletException {
		super.init(config);
/*
		Configuration conf = new Configuration();

		mps = new MountPoints(conf.mountPoints);
		//urlSpacePassword = conf.password;
		//urlSpaceServlet = conf.servlet;
*/
	}


/**
 * @param  request               Description of Parameter
 * @param  response              Description of Parameter
 * @exception  ServletException  Description of Exception
 * @exception  IOException       Description of Exception
 */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    PrintWriter out = new PrintWriter(response.getWriter());
    out.println("<rollbackservlet/>");
/*

		HttpSession session = request.getSession(true);

		Identification identity = (Identification) session.getValue("identification");
		if (identity == null) {
			session.putValue("identification", new Identification());
			identity = (Identification) session.getValue("identification");
			identity.username.addElement("anonymous");
		}

		String repositoryId = request.getParameter("repository");
		MountPoint currentMP = null;
		if (repositoryId != null) {
			currentMP = mps.getMountPoint(repositoryId);
			if (currentMP == null) {
				out.println("EXCEPTION: No such mount point: " + repositoryId);
				log.warn("EXCEPTION: No such mount point: " + repositoryId);
				return;
			}
		} else {
			out.println("EXCEPTION: No repository specified");
			log.warn("EXCEPTION: No repository specified");
			return;
		}

		RevisionController rc = new RevisionController(currentMP.directory);
		String source = request.getParameter("source");
		String destination = request.getParameter("destination");
		String rollback = request.getParameter("rollback");
		String validate = request.getParameter("validate");

		String action = request.getParameter("action");
		if (action == null) {
			action = "edit";
		}
		log.debug("--> Action: "+action);
		if (action.equals("createNewDirectory")) {
			response.setContentType("text/html");
			String subdirectory = request.getParameter("subdirectory");
			createNewDirectory(out, currentMP, destination, subdirectory);
			out.close();
			return;
		}
		
		if (action.equals("deleteFile")) {
			response.setContentType("text/html");
			String confirmed = request.getParameter("confirmed");
			if (confirmed != null) {
				if (confirmed.equals("yes")) {
					File file = new File(currentMP.directory + source);
					if (file.isFile()) {
						file.delete();
						out.println("File deleted! Reload Directory!");
					}
				}
			} else {
				out.println("Do you really want to delete this file:<br>");
				out.println("<b>Repository:</b> " + currentMP.name);
				out.println("<br><b>File:</b> " + source);
				out.println("<p><a href=\"/servlets/org.wyona.xps.rc.RCServlet?action=deleteFile&source=" + source + "&repository=" + currentMP.id + "&confirmed=yes\">YES</a></p>");
				out.close();
			}
			return;
		} else {
		}

		if (source != null) {
			FileReader in = null;
			try {
					in = new FileReader(rc.reservedCheckOut(source, (String) identity.username.elementAt(0)));
					char[] buffer = new char[512];
					int length;
// <Martin:>
					if (action.equals("editInBrowser")) {
							response.setContentType("text/html");
							while ((length = in.read(buffer)) != -1) {
									out.write(buffer, 0, length);
							}
							return;
// </Martin:>
					} else {
							response.setContentType("text/plain");
							while ((length = in.read(buffer)) != -1) {
									out.write(buffer, 0, length);
							} 
					}
					out.close();
			}
			catch (FileReservedCheckOutException e) {
				response.setContentType("text/html");
				out.println("<HTML>");
				out.println("<BODY BGCOLOR=\"#ffffff\">");
				out.println("Document has been checked out already:");
				out.println("<P><TABLE>");
				out.println("<TR><TD>username</TD><TD><B>" + e.checkOutUsername + "</B></TD></TR>");
				out.println("<TR><TD>time</TD><TD><B>" + e.checkOutDate + "</B></TD></TR>");
				out.println("</TABLE></P>");
				String currentId = (String) identity.username.elementAt(0);
				if (currentId.equals(e.checkOutUsername)) {
					out.println("<P>" + deleteReservedCheckOut(e.source, currentMP) + "</P>");
				} else {
					out.println("Your current identity is <B>" + (String) identity.username.elementAt(0) + "</B>.<BR>");
					out.println("Do you like to switch the identity to delete the reserved check-out without uploading any document?&#160;<A HREF=org.wyona.security.LoginHandlerServlet>YES</A>");
					session.putValue("request", "/" + servletZone + "/" + this.getClass().getName() + "?source=" + source);
				}
				out.println("</BODY>");
				out.println("</HTML>");
				out.close();
			}
			catch (FileNotFoundException e) {
				response.setContentType("text/html");
				out.println("<HTML>");
				out.println("<BODY BGCOLOR=\"#ffffff\">");
				out.println("<FONT COLOR=\"red\">CHECK-OUT EXCEPTION</FONT><br>No such file or directory: " + e.getMessage());
				out.println("</BODY>");
				out.println("</HTML>");
				out.close();
			}
			catch (Exception e) {
				response.setContentType("text/html");
				out.println("<HTML>");
				out.println("<BODY BGCOLOR=\"#ffffff\">");
				out.println("<FONT COLOR=\"red\">EXCEPTION:</FONT> " + e);
				out.println("</BODY>");
				out.println("</HTML>");
				out.close();
			}
		}
// check-in document
		else if (destination != null) {
			response.setContentType("text/html");
// Delete reserved check-out without uploading any document
			if (action.equals("delete")) {
					log.debug("--> Deleting checkout: "+destination);
				try {

					rc.reservedCheckIn(destination, (String) identity.username.elementAt(0), false);
					out.println("<HTML>");
					out.println("<BODY BGCOLOR=\"#ffffff\">");
					out.println("Reserved check-out deleted without uploading any document.");
					out.println("</BODY>");
					out.println("</HTML>");
					out.close();
					return;
				}
				catch (FileReservedCheckInException e) {
					out.println("<HTML>");
					out.println("<BODY BGCOLOR=\"#ffffff\">");
					out.println("<FONT COLOR=\"red\">CHECK-IN EXCEPTION:</FONT> " + e);
					out.println("</BODY>");
					out.println("</HTML>");
					out.close();
					return;
				}
				catch (FileNotFoundException e) {
					out.println("<HTML>");
					out.println("<BODY BGCOLOR=\"#ffffff\">");
					out.println("<FONT COLOR=\"red\">CHECK-IN EXCEPTION</FONT><br>No such file or directory" + e.getMessage());
					out.println("</BODY>");
					out.println("</HTML>");
					out.close();
					return;
				}
				catch (Exception e) {
					out.println("<HTML>");
					out.println("<BODY BGCOLOR=\"#ffffff\">");
					out.println("<FONT COLOR=\"red\">EXCEPTION:</FONT> " + e);
					out.println("</BODY>");
					out.println("</HTML>");
					out.close();
					return;
				}
			}

// <Really check-in document>
			checkinHTMLForm(out, destination, action, currentMP, validate);
			out.close();
			return;
// </Really check-in document>
		}
		else if (rollback != null) {
			
			
			// This section lists the available versions
			// for a given document in a new window. It
			// uses the rcml xml data with a stylesheet
			// to create the HTML table. This is case 1.
			//
			// On that table, the user can click on one of the
			// old versions. This will cause the control flow to
			// end up here again, but the second time with
			// two additional parameters, named "rollbackTime"
			// and "action", in the request. This is case 2.
			//
			// The third possibility is that we're asked to
			// show the contents of an earlier revision,
			// indicated by the "action=view" parameter value.
			// This is case 3.
			//
			String rollbackTime = request.getParameter("rollbackTime");
			
			if (action.equals("rollback")) {
				
				// Case 2: Do the rollback to an earlier version
				//				
				long newtime;
				
				try {
					newtime = rc.rollback(rollback, (String) identity.username.elementAt(0), true, new Long(rollbackTime).longValue());
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServletException("Unable to roll back!" + e);
				}
				
				log.info("rollback complete, old (and now current) time was " + rollbackTime + " backup time is " + newtime);
	
				new Processor(currentMP.xpipeconf).process(currentMP.directory + rollback);

				String location = "org.wyona.xps.rc.RCServlet?repository=" + repositoryId + "&rollback=" + rollback;
				response.sendRedirect(location);
		

			} else if (action.equals("view")) {

				// Case 3: Show the contents of an old revision
				//
				String backupFilename = rc.getBackupFilename(new Long(rollbackTime).longValue());
				Document doc = new XPSAssembler("enclose").assemble("file:" + backupFilename);
				ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

				try {
					new DOMWriter(outBuffer).printWithoutFormatting(doc);
				} catch (Exception e) {

					out.println("RCServlet: Unable to view file, DOMWriter threw exception: " + backupFilename);
					return;
				}
				
				out.print(outBuffer.toString());

			} else {

				// Case 1: Display the list of available revisions
				//

				Document rcmlDoc = null; 
				try {
					rcmlDoc = rc.getRCML(rollback).getDOMDocumentClone();
				} catch (Exception e) {
					log.error("rollback: Unable to get DOM doc for rcml file, caught exception: " + e.toString());
					throw new ServletException("Unable to get DOM doc for rcml file, caught exception: " + e.toString());
				}

				
				// We attach some information to various elements
				// in the DOM tree. This information enables the XSL stylesheet
				// to produce a better list for the end-user.
				//
				rcmlDoc.getDocumentElement().setAttribute("repository", repositoryId);
				rcmlDoc.getDocumentElement().setAttribute("id", rollback);

				// ML: FIXME: need to set either "Checked Out" of "Checked In" here
				//            Need a bit of support for this in RCML.java
				//
				//rcmlDoc.getDocumentElement().setAttribute("currentstatus", currentStatus);
				NodeList timeElements = rcmlDoc.getElementsByTagName("Time");
				
				for (int i = 0; i < timeElements.getLength(); i++) {
					
					Element time = (Element) timeElements.item(i);
					time.setAttribute("humanreadable", new Date(new Long(time.getFirstChild().getNodeValue()).longValue()).toString());
					
				}
			
				XSLProcessorFactory xslpf = new XSLProcessorFactory();
				ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
				xslpf.processor.process(rcmlDoc, new Configuration().xslt_rcmlrollback, outBuffer);
	
				response.setContentType("text/html");
				out.print(outBuffer.toString());
				out.close();

			}

			
		} else {
			response.setContentType("text/html");
			out.println("<HTML>");
			out.println("<BODY BGCOLOR=\"#ffffff\">");
			out.println("Neither CO nor CI: " + identity);
			out.println("</BODY>");
			out.println("</HTML>");
			out.close();
		}
*/
        out.close();
	}

	/**
	 *@param  destination  Description of Parameter
	 *@param  currentMP    Description of Parameter
	 *@return              Description of the Returned Value
	 */
/*
	public String deleteReservedCheckOut(String destination, MountPoint currentMP) {
		return "Do you like to delete the reserved check-out without uploading any document?&#160;<A HREF=" + this.getClass().getName() + "?destination=" + destination + "&action=delete&repository=" + currentMP.id + ">YES</A>";
	}
*/


	/**
	 *@param  out           Description of Parameter
	 *@param  mp            Description of Parameter
	 *@param  directory     Description of Parameter
	 *@param  subdirectory  Description of Parameter
	 */
/*
	public void createNewDirectory(PrintWriter out, MountPoint mp, String directory, String subdirectory) {
		File parentDirectory = new File(mp.directory + directory);

		out.println("<html>");
		out.println("<body bgcolor=\"#ffffff\">");
		if (parentDirectory.isDirectory()) {
			if (subdirectory == null) {
				out.println("<form method=\"get\">");
				out.println("<input type=\"hidden\" name=\"action\" value=\"createNewDirectory\">");
				out.println("<input type=\"hidden\" name=\"repository\" value=\"" + mp.id + "\">");
				out.println("<input type=\"hidden\" name=\"destination\" value=\"" + directory + "\">");
				out.println("Create new directoy:<br>");
				out.println(directory + "/<input type=\"textfield\" name=\"subdirectory\">");
				out.println("<input type=\"submit\" value=\"CREATE\">");
				out.println("</form>");
			}
			else {
				File newDirectory = new File(parentDirectory.getAbsolutePath() + "/" + subdirectory);
				if (newDirectory.isDirectory()) {
					out.println("Directory exist already:<br><b>" + mp.name + ":</b> " + directory + "/" + subdirectory);
				}
				else {
					newDirectory.mkdirs();
					out.println("Directory created:<br><b>" + mp.name + ":</b> " + directory + "/" + subdirectory);
				}
			}
		}
		else {
			out.println("Directory does not exist: " + parentDirectory.getAbsolutePath());
		}
		out.println("</body>");
		out.println("</html>");
	}
*/


	/**
	 *@param  out          Description of Parameter
	 *@param  destination  Description of Parameter
	 *@param  action       Description of Parameter
	 *@param  currentMP    Description of Parameter
	 *@param  validate     Description of Parameter
	 */
/*
	public void checkinHTMLForm(PrintWriter out, String destination, String action, MountPoint currentMP, String validate) {
		out.println("<HTML>");
		out.println("<BODY BGCOLOR=\"#ffffff\">");
		if (action.equals("edit")) {
			out.println("<P>" + deleteReservedCheckOut(destination, currentMP) + "</P>");
		}
		out.println("<P>");
		out.println("<FORM METHOD=post ENCTYPE=multipart/form-data>");
		out.println("<INPUT TYPE=hidden NAME=repository VALUE=" + currentMP.id + ">");
		out.println("<INPUT TYPE=\"hidden\" NAME=\"validate\" VALUE=" + validate + ">");
		out.println("<TABLE BORDER=1 BGCOLOR=#eeeeee>");
		out.println("<TR><TD COLSPAN=2><B>Check in document</B></TD></TR>");
		if (action.equals("edit")) {
			out.println("<TR><TD>Destination</TD><TD>" + destination + "</TD></TR>");
			out.println("<INPUT TYPE=hidden NAME=destination VALUE=" + destination + ">");
		}
		else if (action.equals("new")) {
			String separator = null;
			if (destination.length() == 1) {
				separator = "";
			}
			else {
				separator = "/";
			}
			out.println("<TR><TD>Destination</TD><TD>" + destination + separator + "<INPUT TYPE=\"textfield\" NAME=\"destination\"></TD></TR>");
			out.println("<INPUT TYPE=\"hidden\" NAME=\"destinationDirectory\" VALUE=" + destination + separator + ">");
		}
		else {
			out.println("<TR><TD VALIGN=top><font color=red>EXCEPTION</font></TD><TD>");
		}
		out.println("<TR><TD VALIGN=top>Source</TD><TD>");
		out.println("<INPUT TYPE=file NAME=source>");
		out.println("<BR><INPUT TYPE=\"submit\" VALUE=\"Upload . . .\">");
		out.println("</TD></TR>");
		out.println("</TABLE>");
		out.println("</FORM>");
		out.println("</P>");

		out.println("</BODY>");
		out.println("</HTML>");
	}
*/
}
