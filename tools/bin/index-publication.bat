@echo off

REM  Copyright 2001,2004-2005 The Apache Software Foundation
REM
REM  Licensed under the Apache License, Version 2.0 (the "License");
REM  you may not use this file except in compliance with the License.
REM  You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.

SET LENYAPUB=default
.\ant -f ../../build/lenya/webapp/lenya/bin/crawl_and_index.xml -Dlucene.xconf=../../build/lenya/webapp/lenya/pubs/%LENYAPUB%/config/search/lucene-live.xconf index
