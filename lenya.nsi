;NSIS Lenya Installer script
; $Id: lenya.nsi,v 1.4 2003/11/09 23:28:45 gregor Exp $

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;Configuration

  ;General
  Name "Apache Lenya 1.2"
  OutFile "Lenya-1.2-install.exe"

  ;Folder selection page
  InstallDir "C:\Lenya"
  
;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "build\lenya\webapp\legal\LICENSE.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Apache Lenya" SecDummy

  SetOutPath "$INSTDIR"
  
  SetOutPath $INSTDIR\build\lenya\webapp
  File build\lenya\webapp\global-sitemap.xmap
  File build\lenya\webapp\not-found.xml
  File build\lenya\webapp\sitemap.xmap
  File build\lenya\webapp\welcome.xml
  File build\lenya\webapp\welcome.xslt
  File /r build\lenya\webapp\docs
  File /r build\lenya\webapp\legal
  File /r build\lenya\webapp\lenya
  File /r build\lenya\webapp\resources
  File /r build\lenya\webapp\stylesheets
  File /r build\lenya\webapp\WEB-INF
  
  SetOutPath $INSTDIR
  File lenya.bat
  File README.txt
  File /r tools

  SetOutPath "$SMPROGRAMS\Apache Lenya 1.2"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Lenya Home Page.lnk" \
                 "http://cocoon.apache.org/lenya/"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Welcome.lnk" \
                 "http://127.0.0.1:8888"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Lenya Documentation.lnk" \
                 "http://127.0.0.1:8888/docs/"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Uninstall Lenya 1.2.lnk" \
                 "$INSTDIR\Uninstall.exe"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Start Lenya.lnk" \
                 "$INSTDIR\lenya.bat" \
                 'servlet' \
                 "$INSTDIR\lenya.bat" 1 SW_SHOWNORMAL

  ClearErrors

  ExecWait '"$INSTDIR\lenya.bat"'
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
;Descriptions

  LangString DESC_SecDummy ${LANG_ENGLISH} "Installs the Apache Lenya Content Management System."

  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  RMDir /r "$SMPROGRAMS\Apache Lenya 1.2"
  RMDir /r "$INSTDIR"

SectionEnd