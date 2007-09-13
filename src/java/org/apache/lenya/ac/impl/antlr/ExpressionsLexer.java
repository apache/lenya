// $ANTLR 3.0 src/java/org/apache/lenya/ac/impl/antlr/Expressions.g 2007-06-19 15:30:41

package org.apache.lenya.ac.impl.antlr;
import org.apache.lenya.ac.ErrorHandler;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ExpressionsLexer extends Lexer {
    public static final int RIGHTPAR=8;
    public static final int EQUALS=6;
    public static final int AND=5;
    public static final int LITERAL=10;
    public static final int LEFTPAR=7;
    public static final int WS=12;
    public static final int EOF=-1;
    public static final int Tokens=13;
    public static final int QUOT=11;
    public static final int OR=4;
    public static final int ID=9;


        private ErrorHandler errorHandler;
        
        public void setErrorHandler(ErrorHandler handler) {
        	this.errorHandler = handler;
        }

        public void emitErrorMessage(String msg) {
        	this.errorHandler.error(msg);
        }

    public ExpressionsLexer() {;} 
    public ExpressionsLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "src/java/org/apache/lenya/ac/impl/antlr/Expressions.g"; }

    // $ANTLR start LITERAL
    public final void mLITERAL() throws RecognitionException {
        try {
            int _type = LITERAL;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:92:10: ( '\"' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | ' ' | '.' )* '\"' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:92:10: '\"' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | ' ' | '.' )* '\"'
            {
            match('\"'); 
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:92:14: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | ' ' | '.' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==' '||LA1_0=='.'||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:
            	    {
            	    if ( input.LA(1)==' '||input.LA(1)=='.'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LITERAL

    // $ANTLR start ID
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:93:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )* )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:93:5: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:93:25: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ID

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:94:9: ( '==' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:94:9: '=='
            {
            match("=="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUALS

    // $ANTLR start QUOT
    public final void mQUOT() throws RecognitionException {
        try {
            int _type = QUOT;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:95:7: ( '\"' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:95:7: '\"'
            {
            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end QUOT

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:96:5: ( '||' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:96:5: '||'
            {
            match("||"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:97:6: ( '&&' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:97:6: '&&'
            {
            match("&&"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start LEFTPAR
    public final void mLEFTPAR() throws RecognitionException {
        try {
            int _type = LEFTPAR;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:98:10: ( '(' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:98:10: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFTPAR

    // $ANTLR start RIGHTPAR
    public final void mRIGHTPAR() throws RecognitionException {
        try {
            int _type = RIGHTPAR;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:99:11: ( ')' )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:99:11: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHTPAR

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:100:5: ( ( ' ' | '\\t' )+ )
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:100:5: ( ' ' | '\\t' )+
            {
            // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:100:5: ( ' ' | '\\t' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\t'||LA3_0==' ') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            skip();

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    public void mTokens() throws RecognitionException {
        // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:10: ( LITERAL | ID | EQUALS | QUOT | OR | AND | LEFTPAR | RIGHTPAR | WS )
        int alt4=9;
        switch ( input.LA(1) ) {
        case '\"':
            {
            int LA4_1 = input.LA(2);

            if ( (LA4_1==' '||LA4_1=='\"'||LA4_1=='.'||(LA4_1>='A' && LA4_1<='Z')||LA4_1=='_'||(LA4_1>='a' && LA4_1<='z')) ) {
                alt4=1;
            }
            else {
                alt4=4;}
            }
            break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt4=2;
            }
            break;
        case '=':
            {
            alt4=3;
            }
            break;
        case '|':
            {
            alt4=5;
            }
            break;
        case '&':
            {
            alt4=6;
            }
            break;
        case '(':
            {
            alt4=7;
            }
            break;
        case ')':
            {
            alt4=8;
            }
            break;
        case '\t':
        case ' ':
            {
            alt4=9;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( LITERAL | ID | EQUALS | QUOT | OR | AND | LEFTPAR | RIGHTPAR | WS );", 4, 0, input);

            throw nvae;
        }

        switch (alt4) {
            case 1 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:10: LITERAL
                {
                mLITERAL(); 

                }
                break;
            case 2 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:18: ID
                {
                mID(); 

                }
                break;
            case 3 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:21: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 4 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:28: QUOT
                {
                mQUOT(); 

                }
                break;
            case 5 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:33: OR
                {
                mOR(); 

                }
                break;
            case 6 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:36: AND
                {
                mAND(); 

                }
                break;
            case 7 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:40: LEFTPAR
                {
                mLEFTPAR(); 

                }
                break;
            case 8 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:48: RIGHTPAR
                {
                mRIGHTPAR(); 

                }
                break;
            case 9 :
                // src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:1:57: WS
                {
                mWS(); 

                }
                break;

        }

    }


 

}