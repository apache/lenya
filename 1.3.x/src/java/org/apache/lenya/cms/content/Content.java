package org.apache.lenya.cms.content;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.publication.Publication;

public interface Content{
   public String getUNID(String structure, String id);
   public String getNewFilename(String unid, String language);
   public String getIndexFilename(String indexName, String language);
   public String[] getLanguages();
}
