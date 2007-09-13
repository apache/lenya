grammar Expressions;

@header {
package org.apache.lenya.ac.impl.antlr;
import java.util.HashMap;
import org.apache.lenya.ac.impl.ErrorHandler;
}

@lexer::header {
package org.apache.lenya.ac.impl.antlr;
import org.apache.lenya.ac.impl.ErrorHandler;
}

@members {
/** Map variable name to object holding value */
    HashMap memory = new HashMap();
    private ErrorHandler errorHandler;
    
    public void setErrorHandler(ErrorHandler handler) {
    	this.errorHandler = handler;
    }
    
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg;
    	if (e instanceof FailedPredicateException) {
  	        msg = ((FailedPredicateException) e).predicateText;
    	}
  	    else {
            msg = getErrorMessage(e, tokenNames);
  	    }
        emitErrorMessage(hdr+" "+msg);
    }

    public void emitErrorMessage(String msg) {
    	this.errorHandler.error(msg);
    }
}

@lexer::members {

    private ErrorHandler errorHandler;
    
    public void setErrorHandler(ErrorHandler handler) {
    	this.errorHandler = handler;
    }

    public void emitErrorMessage(String msg) {
    	this.errorHandler.error(msg);
    }
}

prog returns [boolean value]
    : e=orExpression {$value = $e.value; }
    ;

orExpression returns [boolean value]
    :   e=andExpression {$value = $e.value; }
    ( OR e=andExpression {$value = $value || $e.value; } )*
    ;
    
andExpression returns [boolean value]
    :   e=comparison {$value = $e.value;}
    ( AND e=comparison {$value = $value && $e.value;} )*
    ;
    
comparison returns [boolean value]
    :   a=atom EQUALS b=atom {$value = $a.value.equals($b.value);}
    |   LEFTPAR e=orExpression RIGHTPAR {$value = $e.value;}
    ;

atom returns [String value]
    :   quotedString {$value = $quotedString.value;}
    |   ID
        {
            String s = (String) memory.get($ID.text);
            if ( s != null ) {
                $value = s;
            }
            else {
            	$value = "undefined";
                throw new FailedPredicateException(input, $ID.text,
                    "Undefined variable \"" + $ID.text + "\"");
            }
        }
    ;

quotedString returns [String value]
      : LITERAL {$value = $LITERAL.text.substring(1, $LITERAL.text.length() - 1);}
      ;
      
LITERAL: '"' ('a'..'z'|'A'..'Z'|'_'|' '|'.')* '"';
ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*;
EQUALS: '==';
QUOT: '"';
OR: '||';
AND: '&&';
LEFTPAR: '(';
RIGHTPAR: ')';
WS: (' '|'\t')+ {skip();} ;
