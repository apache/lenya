/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* $Id: HTMLParserConstants.java,v 1.9 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.html;

public interface HTMLParserConstants {
    int EOF = 0;
    int TagName = 1;
    int DeclName = 2;
    int Comment1 = 3;
    int Comment2 = 4;
    int Word = 5;
    int LET = 6;
    int NUM = 7;
    int Entity = 8;
    int Space = 9;
    int SP = 10;
    int Punct = 11;
    int ArgName = 12;
    int ArgEquals = 13;
    int TagEnd = 14;
    int ArgValue = 15;
    int ArgQuote1 = 16;
    int ArgQuote2 = 17;
    int Quote1Text = 19;
    int CloseQuote1 = 20;
    int Quote2Text = 21;
    int CloseQuote2 = 22;
    int CommentText1 = 23;
    int CommentEnd1 = 24;
    int CommentText2 = 25;
    int CommentEnd2 = 26;
    int DEFAULT = 0;
    int WithinTag = 1;
    int AfterEquals = 2;
    int WithinQuote1 = 3;
    int WithinQuote2 = 4;
    int WithinComment1 = 5;
    int WithinComment2 = 6;
    String[] tokenImage = {
        "<EOF>", "<TagName>", "<DeclName>", "\"<!--\"", "\"<!\"", "<Word>", "<LET>", "<NUM>",
        "<Entity>", "<Space>", "<SP>", "<Punct>", "<ArgName>", "\"=\"", "<TagEnd>", "<ArgValue>",
        "\"\\\'\"", "\"\\\"\"", "<token of kind 18>", "<Quote1Text>", "<CloseQuote1>",
        "<Quote2Text>", "<CloseQuote2>", "<CommentText1>", "\"-->\"", "<CommentText2>", "\">\"",
    };
}
