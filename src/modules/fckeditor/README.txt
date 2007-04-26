#
# Copyright 1999-2006 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ----------------------------------------------------------------------
#
# A note for developers: If you change this file, please keep the
# documentation on the Lenya website in sync. Thanks!
#


How to configure/install:
1. download the latest release of fckeditor from http://www.fckeditor.net/

2. unzip it into the src/modules/fckeditor/resources directory
   (resources directory should now contain a directory named fckeditor)
   
3. register the module with your publication, so that the appropriate menu
   item can be displayed: in the <modules> section of <yourpub>/config/publication.xml,
   add the line
   
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
    

