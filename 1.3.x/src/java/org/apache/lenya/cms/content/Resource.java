package org.apache.lenya.cms.content;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;

public interface Resource{
 
//   public Source getSource(String translation, String revision) throws SourceNotFoundException;

   public String getUNID();
   public String getID();

   public String getType();

   public String getDocumentType();

   public String getLanguage();

   public String getDefaultLanguage();

   public String[] getLanguages();

   public String getNewFilename(String language);


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

//   public Translation getTranslation(String translation);

}
