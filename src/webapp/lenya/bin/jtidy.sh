#!/bin/sh

/usr/local/jdk1.3.1/bin/java -cp build/lenya/classes:src/cocoon/WEB-INF/lib/jtidy-04aug2000r7-dev.jar:src/cocoon/WEB-INF/lib/xml-apis.jar org.apache.lenya.util.TidyCommandLine file:/home/michiii/oscom-layout.html oscom-layout.xhtml error.log
