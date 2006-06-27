package org.apache.lenya.cms.content;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.w3c.dom.Document;

public interface Resource{
   public String getURI();
   public String getMetaURI();
   public String getNewURI();

   public String getUNID();
   public String getID();
   public String getType();
   public String getDocumentType();
   public String getLanguage();
   public String getDefaultLanguage();
   public String[] getLanguages();
   public Document getInfoDocument();
   public Document update(Document document);

/**
 * Get Navigation Title from live revision of current language.
 */
   public String getTitle();
/**
 * Get Navigation Title from live revision of specified language.
 */
   public String getTitle(String language);
/**
 * Get Navigation Title from specified revision of specified language.
 */
   public String getTitle(String language, String revision);
}
