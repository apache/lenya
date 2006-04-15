
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
