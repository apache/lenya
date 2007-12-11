@echo off

rem  Licensed to the Apache Software Foundation (ASF) under one or more
rem  contributor license agreements.  See the NOTICE file distributed with
rem  this work for additional information regarding copyright ownership.
rem  The ASF licenses this file to You under the Apache License, Version 2.0
rem  (the "License"); you may not use this file except in compliance with
rem  the License.  You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem  Unless required by applicable law or agreed to in writing, software
rem  distributed under the License is distributed on an "AS IS" BASIS,
rem  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem  See the License for the specific language governing permissions and
rem  limitations under the License.

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

if ""%1""=="""" goto runCommand

rem Change drive and directory to %1
if "%OS%"=="Windows_NT" cd /d ""%1""
if not "%OS%"=="Windows_NT" cd ""%1""
shift

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of agruments (up to the command line limit, anyway).
set ANT_RUN_CMD=%1
if ""%1""=="""" goto runCommand
shift
:loop
if ""%1""=="""" goto runCommand
set ANT_RUN_CMD=%ANT_RUN_CMD% %1
shift
goto loop

:runCommand
rem echo %ANT_RUN_CMD%
%ANT_RUN_CMD%

if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal

