lexer grammar Expressions;
@members {

    private ErrorHandler errorHandler;
    
    public void setErrorHandler(ErrorHandler handler) {
    	this.errorHandler = handler;
    }

    public void emitErrorMessage(String msg) {
    	this.errorHandler.error(msg);
    }
}
@header {
package org.apache.lenya.ac.impl.antlr;
import org.apache.lenya.ac.impl.ErrorHandler;
}

// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 92
LITERAL: '"' ('a'..'z'|'A'..'Z'|'_'|' '|'.')* '"';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 93
ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*;
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 94
EQUALS: '==';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 95
QUOT: '"';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 96
OR: '||';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 97
AND: '&&';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 98
LEFTPAR: '(';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 99
RIGHTPAR: ')';
// $ANTLR src "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 100
WS: (' '|'\t')+ {skip();} ;
