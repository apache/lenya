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

/* $Id$  */

package org.apache.lenya.lucene.html;

/**
 * Constants used by the HTML parser
 */
public interface HTMLParserConstants {
    /**
     * <code>EOF</code> End of file
     */
    int EOF = 0;
    /**
     * <code>TagName</code> The tag name
     */
    int TagName = 1;
    /**
     * <code>DeclName</code> The declaration name
     */
    int DeclName = 2;
    /**
     * <code>Comment1</code> Comment 1
     */
    int Comment1 = 3;
    /**
     * <code>Comment2</code> Comment 2
     */
    int Comment2 = 4;
    /**
     * <code>Word</code> Word
     */
    int Word = 5;
    /**
     * <code>LET</code> LET
     */
    int LET = 6;
    /**
     * <code>NUM</code> NUM
     */
    int NUM = 7;
    /**
     * <code>Entity</code> The entity
     */
    int Entity = 8;
    /**
     * <code>Space</code> The space
     */
    int Space = 9;
    /**
     * <code>SP</code> SP
     */
    int SP = 10;
    /**
     * <code>Punct</code> Punct
     */
    int Punct = 11;
    /**
     * <code>ArgName</code> Argument Name
     */
    int ArgName = 12;
    /**
     * <code>ArgEquals</code> The equals sign
     */
    int ArgEquals = 13;
    /**
     * <code>TagEnd</code> The end of the tag
     */
    int TagEnd = 14;
    /**
     * <code>ArgValue</code> The argument value
     */
    int ArgValue = 15;
    /**
     * <code>ArgQuote1</code> Quote 1 of the argument
     */
    int ArgQuote1 = 16;
    /**
     * <code>ArgQuote2</code> Quote 2 of the argument
     */
    int ArgQuote2 = 17;
    /**
     * <code>Quote1Text</code> Quote 1 of the text
     */
    int Quote1Text = 19;
    /**
     * <code>CloseQuote1</code> Closing quote 1
     */
    int CloseQuote1 = 20;
    /**
     * <code>Quote2Text</code> Quote 2 of the text
     */
    int Quote2Text = 21;
    /**
     * <code>CloseQuote2</code> Closing quote 2
     */
    int CloseQuote2 = 22;
    /**
     * <code>CommentText1</code> Comment text 1
     */
    int CommentText1 = 23;
    /**
     * <code>CommentEnd1</code> Comment end 1
     */
    int CommentEnd1 = 24;
    /**
     * <code>CommentText2</code> Comment text 2
     */
    int CommentText2 = 25;
    /**
     * <code>CommentEnd2</code> Comment end 2
     */
    int CommentEnd2 = 26;
    /**
     * <code>DEFAULT</code> Default state
     */
    int DEFAULT = 0;
    /**
     * <code>WithinTag</code> Within a tag state
     */
    int WithinTag = 1;
    /**
     * <code>AfterEquals</code> After equals state
     */
    int AfterEquals = 2;
    /**
     * <code>WithinQuote1</code> Within Quote 1 state
     */
    int WithinQuote1 = 3;
    /**
     * <code>WithinQuote2</code> Within Quote 2 state
     */
    int WithinQuote2 = 4;
    /**
     * <code>WithinComment1</code> Within comment 1 state
     */
    int WithinComment1 = 5;
    /**
     * <code>WithinComment2</code> Within comment 2 state
     */
    int WithinComment2 = 6;
    /**
     * <code>tokenImage</code> Image Token
     */
    String[] tokenImage = {
        "<EOF>", "<TagName>", "<DeclName>", "\"<!--\"", "\"<!\"", "<Word>", "<LET>", "<NUM>",
        "<Entity>", "<Space>", "<SP>", "<Punct>", "<ArgName>", "\"=\"", "<TagEnd>", "<ArgValue>",
        "\"\\\'\"", "\"\\\"\"", "<token of kind 18>", "<Quote1Text>", "<CloseQuote1>",
        "<Quote2Text>", "<CloseQuote2>", "<CommentText1>", "\"-->\"", "<CommentText2>", "\">\"",
    };
}
