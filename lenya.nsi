; Lenya script for Nullsoft Installer
; $Id: lenya.nsi,v 1.1 2003/11/04 02:26:09 gregor Exp $

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
  !define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\webapps\ROOT\RELEASE-NOTES.txt"
  !define MUI_FINISHPAGE_RUN $INSTDIR\bin\lenyaw.exe
  !define MUI_FINISHPAGE_RUN_PARAMETERS //GT//Lenya

  !define MUI_FINISHPAGE_NOREBOOTSUPPORT

  !define MUI_LICENSEPAGE
  !define MUI_COMPONENTSPAGE
  !define MUI_DIRECTORYPAGE

  !define MUI_ABORTWARNING
  !define MUI_CUSTOMPAGECOMMANDS

  !define MUI_UNINSTALLER
  !define MUI_UNCONFIRMPAGE

  !define TEMP1 $R0
  !define TEMP2 $R1

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

  LangString TEXT_CONF_TITLE ${LANG_ENGLISH} "Configuration"
  LangString TEXT_CONF_SUBTITLE ${LANG_ENGLISH} "Lenya basic configuration."
  LangString TEXT_CONF_PAGETITLE ${LANG_ENGLISH} ": Configuration Options"

  ;Page order
  !insertmacro MUI_PAGECOMMAND_WELCOME
  !insertmacro MUI_PAGECOMMAND_LICENSE
  !insertmacro MUI_PAGECOMMAND_COMPONENTS
  !insertmacro MUI_PAGECOMMAND_DIRECTORY
  Page custom SetConfiguration "$(TEXT_CONF_PAGETITLE)"
  Page custom SetChooseJVM "$(TEXT_JVM_PAGETITLE)"
  !insertmacro MUI_PAGECOMMAND_INSTFILES
  !insertmacro MUI_PAGECOMMAND_FINISH

  ;License dialog
  LicenseData INSTALLLICENSE

  ;Component-selection page
    ;Descriptions
    LangString DESC_SecLenya ${LANG_ENGLISH} "Install the Lenya Content Mangement System"
    LangString DESC_SecLenyaCore ${LANG_ENGLISH} "Install the Lenya core."
    LangString DESC_SecLenyaService ${LANG_ENGLISH} "Automatically start Lenya when the computer is started. This requires Windows NT 4.0, Windows 2000 or Windows XP."
    LangString DESC_SecLenyaSource ${LANG_ENGLISH} "Install the Lenya source code."
    LangString DESC_SecLenyaDocs ${LANG_ENGLISH} "Install the Lenya documentation."
    LangString DESC_SecMenu ${LANG_ENGLISH} "Create a Start Menu program group for Lenya."
    LangString DESC_SecExamples ${LANG_ENGLISH} "Installs some example publications."

  ;Folder-select dialog
  InstallDir "$PROGRAMFILES\Apache Software Foundation\Lenya 1.2"

  ;Install types
  InstType Normal
  InstType Minimum
  InstType Full

  ; Main registry key
  InstallDirRegKey HKLM "SOFTWARE\Apache Software Foundation\Lenya\1.2" ""

  !insertmacro MUI_RESERVEFILE_WELCOMEFINISHPAGE
  !insertmacro MUI_RESERVEFILE_INSTALLOPTIONS
  ReserveFile "jvm.ini"
  ReserveFile "config.ini"

;--------------------------------
;Modern UI System

!insertmacro MUI_SYSTEM

;--------------------------------
;Installer Sections

SubSection "Lenya" SecLenya

Section "Core" SecLenyaCore

  SectionIn 1 2 3

  Call checkJvm

  SetOutPath $INSTDIR
  File lenya.ico
  File LICENSE
  File /r bin
  File /r common
  File /r conf
  File /r shared
  File /r logs
  File /r server
  File /r work
  File /r temp
  SetOutPath $INSTDIR\webapps
  File /r webapps\ROOT

  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"
  CopyFiles /SILENT "$2\lib\tools.jar" "$INSTDIR\common\lib" 4500
  ClearErrors

  Call configure

  ExecWait '"$INSTDIR\bin\lenyaw.exe" //IS//Lenya5 --DisplayName "Apache Lenya" --Description "Apache Lenya @VERSION@ Server - http://jakarta.apache.org/lenya/"  --Install "$INSTDIR\bin\lenya.exe" --ImagePath "$INSTDIR\bin\bootstrap.jar" --StartupClass org.apache.catalina.startup.Bootstrap;main;start --ShutdownClass org.apache.catalina.startup.Bootstrap;main;stop --Java java --JavaOptions -Xrs --Startup manual'

SectionEnd

Section "Service" SecLenyaService

  SectionIn 3

  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"
  Push $2
  Call findJVMPath
  Pop $2

  ExecWait '"$INSTDIR\bin\lenyaw.exe" //US//Lenya5 --Startup auto'

  ClearErrors

SectionEnd

Section "Source Code" SecLenyaSource

  SectionIn 3
  SetOutPath $INSTDIR
  File /r src

SectionEnd

Section "Documentation" SecLenyaDocs

  SectionIn 1 3
  SetOutPath $INSTDIR\webapps
  File /r webapps\lenya-docs

SectionEnd

SubSectionEnd

Section "Start Menu Items" SecMenu

  SectionIn 1 2 3

  !insertmacro MUI_INSTALLOPTIONS_READ $2 "jvm.ini" "Field 2" "State"

  SetOutPath "$SMPROGRAMS\Apache Lenya 1.2"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Lenya Home Page.lnk" \
                 "http://cocoon.apache.org/lenya"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Welcome.lnk" \
                 "http://127.0.0.1:$R0/lenya/"

  IfFileExists "$INSTDIR\webapps\webapps\lenya-docs" 0 NoDocumentaion

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Lenya Documentation.lnk" \
                 "$INSTDIR\webapps\lenya\docs\index.html"

NoDocumentaion:

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Uninstall Lenya 1.2.lnk" \
                 "$INSTDIR\Uninstall.exe"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Lenya 1.2 Program Directory.lnk" \
                 "$INSTDIR"

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Start Lenya.lnk" \
                 "$INSTDIR\bin\lenyaw.exe" \
                 '//GT//Lenya' \
                 "$INSTDIR\bin\lenyaw.exe" 1 SW_SHOWNORMAL

  CreateShortCut "$SMPROGRAMS\Apache Lenya 1.2\Configure Lenya.lnk" \
                 "$INSTDIR\bin\lenyaw.exe" \
                 '//ES//Lenya' \
                 "$INSTDIR\bin\lenyaw.exe" 0 SW_SHOWNORMAL

SectionEnd

Section "Examples" SecExamples

  SectionIn 1 3

  SetOverwrite on
  SetOutPath $INSTDIR\webapps
  File /r webapps\jsp-examples
  File /r webapps\servlets-examples

SectionEnd

Section -post

  ExecWait '"$INSTDIR\bin\lenyaw.exe" //US//Lenya5 --JavaOptions -Dcatalina.home="\"$INSTDIR\""#-Djava.endorsed.dirs="\"$INSTDIR\common\endorsed\""#-Dsun.io.useCanonCaches=false#-Xrs --StdOutputFile "$INSTDIR\logs\stdout.log" --StdErrorFile "$INSTDIR\logs\stderr.log" --WorkingPath "$INSTDIR"'

  WriteUninstaller "$INSTDIR\Uninstall.exe"

  WriteRegStr HKLM "SOFTWARE\Apache Software Foundation\Lenya\1.2" "InstallPath" $INSTDIR
  WriteRegStr HKLM "SOFTWARE\Apache Software Foundation\Lenya\1.2" "Version" @VERSION@
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Apache Lenya 1.2" \
                   "DisplayName" "Apache Lenya 1.2 (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Apache Lenya 1.2" \
                   "UninstallString" '"$INSTDIR\Uninstall.exe"'

SectionEnd

Function .onInit

  ;Extract Install Options INI Files
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "config.ini"
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jvm.ini"

FunctionEnd

Function SetChooseJVM
  !insertmacro MUI_HEADER_TEXT "$(TEXT_JVM_TITLE)" "$(TEXT_JVM_SUBTITLE)"
  Call findJavaPath
  Pop $3
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jvm.ini" "Field 2" "State" $3
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "jvm.ini"
FunctionEnd

Function SetConfiguration
  !insertmacro MUI_HEADER_TEXT "$(TEXT_CONF_TITLE)" "$(TEXT_CONF_SUBTITLE)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "config.ini"
FunctionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTIONS_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenya} $(DESC_SecLenya)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenyaCore} $(DESC_SecLenyaCore)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenyaService} $(DESC_SecLenyaService)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenyaSource} $(DESC_SecLenyaSource)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecLenyaDocs} $(DESC_SecLenyaDocs)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecMenu} $(DESC_SecMenu)
  !insertmacro MUI_DESCRIPTION_TEXT ${SecExamples} $(DESC_SecExamples)
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

; ==================
; Configure Function
; ==================
;
; Display the configuration dialog boxes, read the values entered by the user,
; and build the configuration files
;
Function configure

  !insertmacro MUI_INSTALLOPTIONS_READ $R0 "config.ini" "Field 2" "State"
  !insertmacro MUI_INSTALLOPTIONS_READ $R1 "config.ini" "Field 5" "State"
  !insertmacro MUI_INSTALLOPTIONS_READ $R2 "config.ini" "Field 7" "State"

  StrCpy $R4 'port="$R0"'
  StrCpy $R5 '<user name="$R1" password="$R2" roles="admin,manager" />'

  DetailPrint 'HTTP/1.1 Connector configured on port "$R0"'
  DetailPrint 'Admin user added: "$R1"'

  SetOutPath $TEMP
  File /r confinstall

  ; Build final server.xml
  Delete "$INSTDIR\conf\server.xml"
  FileOpen $R9 "$INSTDIR\conf\server.xml" w

  Push "$TEMP\confinstall\server_1.xml"
  Call copyFile
  FileWrite $R9 $R4
  Push "$TEMP\confinstall\server_2.xml"
  Call copyFile

  FileClose $R9

  DetailPrint "server.xml written"

  ; Build final lenya-users.xml
  Delete "$INSTDIR\conf\lenya-users.xml"
  FileOpen $R9 "$INSTDIR\conf\lenya-users.xml" w

  Push "$TEMP\confinstall\lenya-users_1.xml"
  Call copyFile
  FileWrite $R9 $R5
  Push "$TEMP\confinstall\lenya-users_2.xml"
  Call copyFile

  FileClose $R9

  DetailPrint "lenya-users.xml written"

  RMDir /r "$TEMP\confinstall"

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

  ; Delete Lenya service
  ExecWait '"$INSTDIR\bin\lenyaw.exe" //DS//Lenya5'
  ClearErrors

  DeleteRegKey HKCR "JSPFile"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Apache Lenya 1.2"
  DeleteRegKey HKLM "SOFTWARE\Apache Software Foundation\Lenya\1.2"
  RMDir /r "$SMPROGRAMS\Apache Lenya 1.2"
  Delete "$INSTDIR\lenya.ico"
  Delete "$INSTDIR\LICENSE"
  RMDir /r "$INSTDIR\bin"
  RMDir /r "$INSTDIR\common"
  Delete "$INSTDIR\conf\*.dtd"
  RMDir /r "$INSTDIR\shared"
  RMDir "$INSTDIR\logs"
  RMDir /r "$INSTDIR\server"
  RMDir /r "$INSTDIR\webapps\ROOT"
  RMDir /r "$INSTDIR\webapps\lenya-docs"
  RMDir /r "$INSTDIR\webapps\servlets-examples"
  RMDir /r "$INSTDIR\webapps\jsp-examples"
  RMDir "$INSTDIR\webapps"
  RMDir /r "$INSTDIR\work"
  RMDir /r "$INSTDIR\temp"
  RMDir /r "$INSTDIR\src"
  RMDir "$INSTDIR"

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