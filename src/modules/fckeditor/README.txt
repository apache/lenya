How to configure/install:
1. download the latest release of fckeditor from http://www.fckeditor.net/

2. unzip it into the src/modules/fckeditor/resources directory
   (resources directory should now contain a directory named fckeditor)
   
3. register the module with your publication, so that the appropriate menu
   item can be displayed: in <yourpub>/config/publication.xconf, add the line
   <module name="fckeditor"/>

4. make sure you have the appropriate usecase policies in
   <yourpub>/config/ac/usecase-policies.xml. the following entry will allow
   the "admin" and "edit" roles access to the usecase:

    <usecase id="fckeditor.edit">
      <role id="admin" method="grant"/>
      <role id="edit" method="grant"/>
    </usecase>

   or use the usecases option under the admin tab in lenya. 
    
5. build lenya

6. try to edit your document by clicking on "With FCKEditor" in the edit menu
    

