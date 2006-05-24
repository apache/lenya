How to configure/install:
1. download the latest release of fckeditor from http://www.fckeditor.net/

2. unzip it into the src/modules/fckeditor/resources directory
   (resources directory should now contain a directory named FCKeditor)
   
3. add the bxe usecase to your xhtml based resources menu.xmap

    ex: menu.xsp
    
    [...]
      <menu i18n:attr="name" name="Edit">
          <xsp:logic>
            String doctype = <input:get-attribute module="page-envelope" as="string" name="document-type"/>;
            if ("xhtml".equals(doctype)) {
                <block info="false">
                  <item wf:event="edit" uc:usecase="edit.bxe" href="?"><i18n:text>With BXE</i18n:text></item>
                  <item wf:event="edit" uc:usecase="edit.oneform" href="?"><i18n:text>With one Form</i18n:text></item>
                  <item wf:event="edit" uc:usecase="edit.fckeditor" href="?"><i18n:text>With FRED</i18n:text></item>
                </block>
            }
          </xsp:logic>
      </menu>
    [...]
    
4. build lenya

5. try to edit your document by clicking on "With FRED" in the edit menu
    