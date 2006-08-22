How to configure/install:
1. download the latest release of fckeditor from http://www.fckeditor.net/

2. unzip it into the src/modules/fckeditor/resources directory
   (resources directory should now contain a directory named FCKeditor)
   
3. register the module with your publication, so that the appropriate menu
   item can be displayed: in <yourpub>/config/publication.xconf, add the line
   <module name="fckeditor"/>

4. make sure you have the appropriate usecase policies in
   <yourpub>/config/ac/usecase-policies.xconf. the following entry will allow
   the "admin" and "edit" roles access to the usecase:

   <usecase id="edit.tinymce">
     <role id="admin"/>
     <role id="edit"/>
   </usecase>
    
5. build lenya

6. try to edit your document by clicking on "With FCKEditor" in the edit menu
    