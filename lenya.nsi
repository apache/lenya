;NSIS Lenya Installer script

; Copyright 1999-2004 The Apache Software Foundation
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

; $Id$

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;Configuration

  ;General
  Var product_name
  Name "Apache Lenya 1.2.2"
  OutFile "apache-lenya-1.2.2-bin.exe"
  
  CRCCheck on
  SetCompress force
  SetDatablockOptimize on

  ;Folder selection page
  InstallDir "C:\apache-lenya-1.2.2"
  
;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
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

Section "Apache Lenya 1.2.2" SecDummy

  Call findJavaPath
  StrCpy $product_name "Apache Lenya 1.2.2"
  SetOutPath $INSTDIR
  
  SetOutPath $INSTDIR\build\lenya\webapp
  File build\lenya\webapp\global-sitemap.xmap
  File build\lenya\webapp\not-found.xml
  File build\lenya\webapp\sitemap.xmap
  File build\lenya\webapp\welcome.xml
  File build\lenya\webapp\welcome.xslt
  File /r legal
  File /r build\lenya\webapp\lenya
  File /r build\lenya\webapp\resources
  File /r build\lenya\webapp\stylesheets
  File /r build\lenya\webapp\WEB-INF
  
  SetOutPath $INSTDIR
  File lenya.bat
  File build.xml
  File NOTICE.txt
  File LICENSE.txt
  File README.txt
  File CREDITS.txt
  File /r tools
  File /r legal

  CreateDirectory "$SMPROGRAMS\$product_name"
  CreateShortCut "$SMPROGRAMS\$product_name\Lenya Home Page.lnk" \
                 "http://lenya.apache.org/"

  CreateShortCut "$SMPROGRAMS\$product_name\Welcome.lnk" \
                 "http://127.0.0.1:8888"

  CreateShortCut "$SMPROGRAMS\$product_name\Apache Lenya Documentation.lnk" \
                 "http://127.0.0.1:8888/docs-new/docs/index.html"

  CreateShortCut "$SMPROGRAMS\$product_name\Uninstall Apache Lenya.lnk" \
                 "$INSTDIR\Uninstall.exe"

  CreateShortCut "$SMPROGRAMS\$product_name\Start Apache Lenya.lnk" \
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
  RMDir /r "$SMPROGRAMS\Apache Lenya 1.2.2"
  RMDir /r "$INSTDIR"

SectionEnd

; =====================
; FindJavaPath Function
; =====================
;
; Find the JAVA_HOME used on the system, and put the result on the top of the
; stack
; Will exit if the path cannot be determined
;
Function findJavaPath

  ClearErrors

  ReadEnvStr $1 JAVA_HOME

  IfErrors 0 FoundJDK

  ClearErrors

  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "JavaHome"
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$3" "RuntimeLib"

  FoundJDK:

  IfErrors 0 NoAbort
    MessageBox MB_OK "Couldn't find a Java Development Kit installed on this \
computer. Please download one from http://java.sun.com. If there is already \ a JDK installed on this computer, set an environment variable JAVA_HOME to the \ pathname of the directory where it is installed."
    Abort

  NoAbort:

  ; Put the result in the stack
  Push $1

FunctionEnd