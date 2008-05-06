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
import org.apache.lenya.ac.ErrorHandler;
}

// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 106
LITERAL: '"' ('a'..'z'|'A'..'Z'|'_'|' '|'.')* '"';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 107
ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*;
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 108
EQUALS: '==';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 109
QUOT: '"';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 110
OR: '||';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 111
AND: '&&';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 112
LEFTPAR: '(';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 113
RIGHTPAR: ')';
// $ANTLR src "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g" 114
WS: (' '|'\t')+ {skip();} ;
