
  Apache Lenya - Lucene Module
  ============================


  Configuration
  -------------

  Each publication has a configuration at PUB/config/lucene_index.xconf which is being defined
  by java/src/org/apache/cocoon/components/search/components/impl/IndexManagerImpl.java


  Indexing
  --------

  Indexing is being executed by calling the lucene.index usecase which is defined
  by config/cocoon-xconf/usecase-lucene.index.xconf and uses as main
  entry point java/src/org/apache/lenya/cms/lucene/IndexDocument.java

  The usecase lucene.index is called by other usecases, e.g.
  src/webapp/lenya/config/cocoon-xconf/usecases/edit/usecase-edit-oneform.xconf

  In order to make a resource type indexable one needs to add the format luceneIndex to the
  resource type configuration (e.g. src/modules/xhtml/config/cocoon-xconf/resource-type-xhtml.xconf)
  One needs to create or reuse a pipeline for this format within the specified sitemap.


  Searching
  ---------

  TBD
