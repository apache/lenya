; Lenya script for Nullsoft Installer
; $Id: lenya.nsi,v 1.2 2003/11/06 17:10:49 gregor Exp $

  ;Compression options
  CRCCheck on
  SetCompress force
  SetCompressor bzip2
  SetDatablockOptimize on

!include "MUI.nsh"

!define MUI_PRODUCT "Apache Lenya"
!define MUI_VERSION "@VERSION@"

;--------------------------------
;Configuration

  !define MUI_WELCOMEPAGE
  !define MUI_FINISHPAGE
  !define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\README.txt"
  !define MUI_FINISHPAGE_RUN $INSTDIR\lenya.bat
  !define MUI_FINISHPAGE_RUN_PARAMETERS servlet

  !define MUI_FINISHPAGE_NOREBOOTSUPPORT

  !define MUI_LICENSEPAGE
  !define MUI_COMPONENTSPAGE
  !define MUI_DIRECTORYPAGE

  !define MUI_ABORTWARNING
  !define MUI_CUSTOMPAGECOMMANDS

  !define MUI_UNINSTALLER
  !define MUI_UNCONFIRMPAGE

  !define MUI_ICON lenya.ico
  !define MUI_UNICON lenya.ico

  ;Language
  !insertmacro MUI_LANGUAGE "English"

  ;General
  OutFile lenya-installer.exe

  ;Install Options pages
  LangString TEXT_JVM_TITLE ${LANG_ENGLISH} "Java Virtual Machine"
  LangString TEXT_JVM_SUBTITLE ${LANG_ENGLISH} "Java Virtual Machine path selection."
  LangString TEXT_JVM_PAGETITLE ${LANG_ENGLISH} ": Java Virtual Machine path selection"

  ;Page order
  !insertmacro MUI_PAGECOMMAND_WELCOME
  !insertmacro MUI_PAGECOMMAND_LICENSE
  !insertmacro MUI_PAGECOMMAND_COMPONENTS
  !insertmacro MUI_PAGECOMMAND_DIRECTORY
  Page custom SetChooseJVM "$(TEXT_JVM_PAGETITLE)"
  !insertmacro MUI_PAGECOMMAND_INSTFILES
  !insertmacro MUI_PAGECOMMAND_FINISH

  ;License dialog
  LicenseData INSTALLLICENSE

  ;Component-selection page
    ;Descriptions
    LangString DESC_SecLenya ${LANG_ENGLISH} "Install the Lenya Content Management System"
    LangString DESC_SecLenyaCore ${LANG_ENGLISH} "Install the Lenya core."
    LangString DESC_SecLenyaSource ${LANG_ENGLISH} "Install the Lenya source code."
    LangString DESC_SecMenu ${LANG_ENGLISH} "Create a Start Menu program group for Lenya."

  ;Folder-select dialog
  InstallDir "C:\Lenya"

  ;Install types
  InstType Normal

  !insertmacro MUI_RESERVEFILE_WELCOMEFINISHPAGE
  !insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

;--------------------------------
;Modern UI System

!insertmacro MUI_SYSTEM

;--------------------------------
;Installer Sections

SubSection "Lenya" SecLenya

Section "Core" SecLenyaCore

  SectionIn 1 2 3

  Call checkJvm

  SetOutPath $INSTDIR\build\lenya\webapp
  File global-sitemap.xmap
  File not-found.xml
  File sitemap.xmap
  File welcome.xml
  File welcome.xslt
  File /r docs
  File /r legal
  File /r lenya
  File /r resources
  File /r stylesheets
  File /r WEB-INF
  
  SetOutPath $INSTDIR
  File lenya.bat
  File README.txt
  File /r tools
; File lenya.ico

  ClearErrors

  ExecWait '"$INSTDIR\lenya.bat" servlet --DisplayName "Apache Lenya" --Description "Apache Lenya @VERSION@ Server - http://cocoon.apache.org/lenya/"'

SectionEnd

Section "Source Code" SecLenyaSource

  SectionIn 3
  SetOutPath $INSTDIR
  File /r src

SectionEnd

SubSectionEnd

Section "Start Menu Items" SecMenu

  SectionIn 1 2 3

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

SectionEnd

Section -post

  ExecWait '"$INSTDIR\lenya.bat" servlet'

  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Function SetConfiguration
  !insertmacro MUI_HEADER_TEXT "$(TEXT_CONF_TITLE)" "$(TEXT_CONF_SUBTITLE)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "config.ini"
FunctionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTIONS_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenya} $(DESC_SecLenya)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenyaCore} $(DESC_SecLenyaCore)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenyaSource} $(DESC_SecLenyaSource)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecMenu} $(DESC_SecMenu)
!insertmacro MUI_FUNCTIONS_DESCRIPTION_END


; =====================
; FindJavaPath Function
; =====================
;
; Find the JAVA_HOME used on the system, and put the result on the top of the
; stack
; Will return an empty string if the path cannot be determined
;
Function findJavaPath

  ClearErrors

  ReadEnvStr $1 JAVA_HOME

  IfErrors 0 FoundJDK

  ClearErrors

  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "RuntimeLib"

  FoundJDK:

  IfErrors 0 NoErrors
  StrCpy $1 ""

NoErrors:

  ClearErrors

  ; Put the result in the stack
  Push $1

FunctionEnd


; ====================
; FindJVMPath Function
; ====================
;
; Find the full JVM path, and put the result on top of the stack
; Argument: JVM base path (result of findJavaPath)
; Will return an empty string if the path cannot be determined
;
Function findJVMPath

  Pop $1

  IfFileExists "$1\jre\bin\hotspot\jvm.dll" 0 TryJDK14
    StrCpy $2 "$1\jre\bin\hotspot\jvm.dll"
    Goto EndIfFileExists
  TryJDK14:
  IfFileExists "$1\jre\bin\server\jvm.dll" 0 TryClassic
    StrCpy $2 "$1\jre\bin\server\jvm.dll"
    Goto EndIfFileExists
  TryClassic:
  IfFileExists "$1\jre\bin\classic\jvm.dll" 0 JDKNotFound
    StrCpy $2 "$1\jre\bin\classic\jvm.dll"
    Goto EndIfFileExists
  JDKNotFound:
    SetErrors
  EndIfFileExists:

  IfErrors 0 FoundJVMPath

  ClearErrors

  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "RuntimeLib"
  
  FoundJVMPath:

  IfErrors 0 NoErrors
  StrCpy $2 ""

NoErrors:

  ClearErrors

  ; Put the result in the stack
  Push $2

FunctionEnd


; ====================
; CheckJvm Function
; ====================
;
Function checkJvm

  !insertmacro MUI_INSTALLOPTIONS_READ $3 "jvm.ini" "Field 2" "State"
  IfFileExists "$3\bin\java.exe" NoErrors1
  MessageBox MB_OK "No Java Virtual Machine found."
  Quit
NoErrors1:
  Push $3
  Call findJVMPath
  Pop $4
  StrCmp $4 "" 0 NoErrors2
  MessageBox MB_OK "No Java Virtual Machine found."
  Quit
NoErrors2:

FunctionEnd

; =================
; CopyFile Function
; =================
;
; Copy specified file contents to $R9
;
Function copyFile

  ClearErrors

  Pop $0

  FileOpen $1 $0 r

 NoError:

  FileRead $1 $2
  IfErrors EOF 0
  FileWrite $R9 $2

  IfErrors 0 NoError

 EOF:

  FileClose $1

  ClearErrors

FunctionEnd


;--------------------------------
;Uninstaller Section

Section Uninstall

  Delete "$INSTDIR\modern.exe"
  Delete "$INSTDIR\Uninstall.exe"

  RMDir /r "$SMPROGRAMS\Apache Lenya 1.2"
  RMDir /r "$INSTDIR"

  ; if $INSTDIR was removed, skip these next ones
  IfFileExists "$INSTDIR" 0 Removed 
    MessageBox MB_YESNO|MB_ICONQUESTION \
      "Remove all files in your Lenya 1.2 directory? (If you have anything\
 you created that you want to keep, click No)" IDNO Removed
    Delete "$INSTDIR\*.*" ; this would be skipped if the user hits no
    RMDir /r "$INSTDIR"
    Sleep 500
    IfFileExists "$INSTDIR" 0 Removed 
      MessageBox MB_OK|MB_ICONEXCLAMATION \
                 "Note: $INSTDIR could not be removed."
  Removed:

  !insertmacro MUI_UNFINISHHEADER

SectionEnd

;eof