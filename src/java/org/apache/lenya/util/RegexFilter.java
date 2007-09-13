/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: RegexFilter.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * filter out all but files that match a given Regex Pattern,
 */
public class RegexFilter implements FilenameFilter {

	private Pattern pattern;

   /**
	* constructor
	*
	* @param pattern Regex pattern applied to simple filename
	*/
   public RegexFilter(String pattern)
	  {
	  if (pattern != null)
		 {
		 this.pattern = Pattern.compile(pattern);
		 }
	  }


   /**
	* Select only files that match the Regex Pattern.
	*
	* @param dir    the directory in which the file was found.
	* @param name   the simple name of the file
	* @return true if and only if the name should be included in the file list; false otherwise.
	*/
   public boolean accept(File dir, String name)
	  {
	  if (!(new File(dir, name).isFile())) return false;

	  if (pattern != null && !(pattern.matcher(name).matches())) return false;
	  
	  return true;
	  }
	  
} 

