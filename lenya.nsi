;NSIS Lenya Installer script

; Licensed to the Apache Software Foundation (ASF) under one or more
; contributor license agreements.  See the NOTICE file distributed with
; this work for additional information regarding copyright ownership.
; The ASF licenses this file to You under the Apache License, Version 2.0
; (the "License"); you may not use this file except in compliance with
; the License.  You may obtain a copy of the License at
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
  Name "Apache Lenya 1.4-dev"
  OutFile "apache-lenya-1.4-dev-bin.exe"
  
  CRCCheck on
  SetCompress force
  SetDatablockOptimize on

  ;Folder selection page
  InstallDir "C:\apache-lenya-1.4-dev"
  
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

Section "Apache Lenya 1.4-dev" SecDummy

  Call findJavaPath
  StrCpy $product_name "Apache Lenya 1.4-dev"
  SetOutPath $INSTDIR
  
  SetOutPath $INSTDIR\build\lenya\webapp
  File build\lenya\webapp\global-sitemap.xmap
  File build\lenya\webapp\sitemap.xmap
  File /r build\lenya\webapp\lenya
  File /r build\lenya\webapp\WEB-INF
  
  SetOutPath $INSTDIR
  File lenya.bat
  File build.properties
  File build.xml
  File NOTICE.txt
  File LICENSE.txt
  File README.txt
  File CREDITS.txt
  File /r tools
  File /r legal

  CreateDirectory "$SMPROGRAMS\$product_name"
  CreateShortCut "$SMPROGRAMS\$product_name\Apache Lenya Home Page.lnk" \
                 "http://lenya.apache.org/"

  CreateShortCut "$SMPROGRAMS\$product_name\Welcome to $product_name.lnk" \
                 "http://127.0.0.1:8888"

  CreateShortCut "$SMPROGRAMS\$product_name\Uninstall $product_name.lnk" \
                 "$INSTDIR\Uninstall.exe"

  CreateShortCut "$SMPROGRAMS\$product_name\Start $product_name.lnk" \
                 "$INSTDIR\lenya.bat" \
                 'servlet' \
                 "$INSTDIR\lenya.bat" 1 SW_SHOWNORMAL

  ClearErrors

  ;Start Apache Lenya, wait 12 seconds to give it time to start, then launch start page
  ExecShell "open" '"$INSTDIR\lenya.bat"' 0 SW_SHOWMINIMIZED
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  Sleep 12000
  ExecShell "open" '"http://127.0.0.1:8888"' 0 SW_SHOWNORMAL
  Quit
SectionEnd

;--------------------------------
;Descriptions

  LangString DESC_SecDummy ${LANG_ENGLISH} "Installs the Apache Lenya 1.4-dev Content Management System."

  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"
  RMDir /r "$SMPROGRAMS\Apache Lenya 1.4-dev"
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
