grammar Expressions;

@header {
package org.apache.lenya.ac.attr.antlr;
import java.util.HashMap;
import org.apache.lenya.ac.ErrorHandler;
}

@lexer::header {
package org.apache.lenya.ac.attr.antlr;
import org.apache.lenya.ac.ErrorHandler;
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
    :   a=atom EQUALS b=atom {
            if ($a.value instanceof String && $b.value instanceof String) {
                $value = $a.value.equals($b.value);
            }
            else if ($a.value instanceof String && $b.value.getClass().isArray()) {
                $value = java.util.Arrays.asList((String[]) $b.value).contains($a.value);
            }
            else if ($a.value.getClass().isArray() && $b.value instanceof String) {
                $value = java.util.Arrays.asList((String[]) $a.value).contains($b.value);
            }
            else {
                throw new FailedPredicateException(input, $a.text + " == " + $b.text,
                    "Incompatible arguments for comparison: " + $a.value + ", " + $b.value);
            }
        }
    |   LEFTPAR e=orExpression RIGHTPAR {$value = $e.value;}
    ;

atom returns [Object value]
    :   quotedString {$value = $quotedString.value;}
    |   ID
        {
            Object v = memory.get($ID.text);
            if ( v != null ) {
                $value = v;
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
