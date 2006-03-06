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
echo LENYA_HOME = %LENYA_HOME%
set CP=%LENYA_HOME%\tools\configure\build\classes
echo CLASSPATH = %CP%

:: check if JAVA_HOME is set or goto end
echo JAVA_HOME = %JAVA_HOME%
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto end

:: Execute the Configuration if JAVA_HOME is set.
:gotJavaHome
java -classpath %CP% org.apache.lenya.config.ConfigureCommandLine %LENYA_HOME%
::java -classpath %CP% org.apache.lenya.config.ConfigureGUI %LENYA_HOME%


:: Terminate program
:end
:: unset used variables
set LENYA_HOME=
set CP=
