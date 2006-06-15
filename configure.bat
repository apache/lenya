::  Copyright 1999-2006 The Apache Software Foundation
::
::  Licensed under the Apache License, Version 2.0 (the "License");
::  you may not use this file except in compliance with the License.
::  You may obtain a copy of the License at
::
::      http://www.apache.org/licenses/LICENSE-2.0
::
::  Unless required by applicable law or agreed to in writing, software
::  distributed under the License is distributed on an "AS IS" BASIS,
::  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
::  See the License for the specific language governing permissions and
::  limitations under the License.
::
::
@echo off

:: define variables for paths
set LENYA_HOME=%CD%
set CP=%LENYA_HOME%\tools\configure\lib\apache-lenya-configure-core-1.4-dev-r414579.jar;%LENYA_HOME%\tools\configure\lib\apache-lenya-configure-impl-1.4-dev-r414579.jar
echo The classpath is set to: %CP%

:: check if JAVA_HOME is set or goto end
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end

:gotJavaHome
:: If commandline argument cmd is given goto javaCmd. Default without any argument
:: it will start the GUI.
if "%1" == "gui" goto javaGui
goto javaCmd

:javaGui
java -classpath %CP% org.apache.lenya.config.impl.ConfigureGUI %LENYA_HOME%
goto end

:javaCmd
java -classpath %CP% org.apache.lenya.config.impl.ConfigureCommandLine %LENYA_HOME%
goto end

:help
echo Usage: %0 gui or cmd
goto end


:end
:: unset used variables
set LENYA_HOME=
set CP=
