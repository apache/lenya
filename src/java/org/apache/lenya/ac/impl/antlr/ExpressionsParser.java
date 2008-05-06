// $ANTLR 3.0.1 /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g 2008-05-06 16:50:45

package org.apache.lenya.ac.impl.antlr;
import java.util.HashMap;
import org.apache.lenya.ac.ErrorHandler;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ExpressionsParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "OR", "AND", "EQUALS", "LEFTPAR", "RIGHTPAR", "ID", "LITERAL", "QUOT", "WS"
    };
    public static final int RIGHTPAR=8;
    public static final int EQUALS=6;
    public static final int AND=5;
    public static final int LITERAL=10;
    public static final int LEFTPAR=7;
    public static final int WS=12;
    public static final int EOF=-1;
    public static final int QUOT=11;
    public static final int OR=4;
    public static final int ID=9;

        public ExpressionsParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g"; }


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



    // $ANTLR start prog
    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:53:1: prog returns [boolean value] : e= orExpression ;
    public final boolean prog() throws RecognitionException {
        boolean value = false;

        boolean e = false;


        try {
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:54:5: (e= orExpression )
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:54:7: e= orExpression
            {
            pushFollow(FOLLOW_orExpression_in_prog50);
            e=orExpression();
            _fsp--;

            value = e; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end prog


    // $ANTLR start orExpression
    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:57:1: orExpression returns [boolean value] : e= andExpression ( OR e= andExpression )* ;
    public final boolean orExpression() throws RecognitionException {
        boolean value = false;

        boolean e = false;


        try {
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:58:5: (e= andExpression ( OR e= andExpression )* )
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:58:9: e= andExpression ( OR e= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_orExpression77);
            e=andExpression();
            _fsp--;

            value = e; 
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:59:5: ( OR e= andExpression )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:59:7: OR e= andExpression
            	    {
            	    match(input,OR,FOLLOW_OR_in_orExpression87); 
            	    pushFollow(FOLLOW_andExpression_in_orExpression91);
            	    e=andExpression();
            	    _fsp--;

            	    value = value || e; 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end orExpression


    // $ANTLR start andExpression
    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:62:1: andExpression returns [boolean value] : e= comparison ( AND e= comparison )* ;
    public final boolean andExpression() throws RecognitionException {
        boolean value = false;

        boolean e = false;


        try {
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:63:5: (e= comparison ( AND e= comparison )* )
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:63:9: e= comparison ( AND e= comparison )*
            {
            pushFollow(FOLLOW_comparison_in_andExpression125);
            e=comparison();
            _fsp--;

            value = e;
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:64:5: ( AND e= comparison )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==AND) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:64:7: AND e= comparison
            	    {
            	    match(input,AND,FOLLOW_AND_in_andExpression135); 
            	    pushFollow(FOLLOW_comparison_in_andExpression139);
            	    e=comparison();
            	    _fsp--;

            	    value = value && e;

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end andExpression


    // $ANTLR start comparison
    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:67:1: comparison returns [boolean value] : (a= atom EQUALS b= atom | LEFTPAR e= orExpression RIGHTPAR );
    public final boolean comparison() throws RecognitionException {
        boolean value = false;

        atom_return a = null;

        atom_return b = null;

        boolean e = false;


        try {
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:68:5: (a= atom EQUALS b= atom | LEFTPAR e= orExpression RIGHTPAR )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=ID && LA3_0<=LITERAL)) ) {
                alt3=1;
            }
            else if ( (LA3_0==LEFTPAR) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("67:1: comparison returns [boolean value] : (a= atom EQUALS b= atom | LEFTPAR e= orExpression RIGHTPAR );", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:68:9: a= atom EQUALS b= atom
                    {
                    pushFollow(FOLLOW_atom_in_comparison173);
                    a=atom();
                    _fsp--;

                    match(input,EQUALS,FOLLOW_EQUALS_in_comparison175); 
                    pushFollow(FOLLOW_atom_in_comparison179);
                    b=atom();
                    _fsp--;


                                if (a.value instanceof String && b.value instanceof String) {
                                    value = a.value.equals(b.value);
                                }
                                else if (a.value instanceof String && b.value.getClass().isArray()) {
                                    value = java.util.Arrays.asList((String[]) b.value).contains(a.value);
                                }
                                else if (a.value.getClass().isArray() && b.value instanceof String) {
                                    value = java.util.Arrays.asList((String[]) a.value).contains(b.value);
                                }
                                else {
                                    throw new FailedPredicateException(input, input.toString(a.start,a.stop) + " == " + input.toString(b.start,b.stop),
                                        "Incompatible arguments for comparison: " + a.value + ", " + b.value);
                                }
                            

                    }
                    break;
                case 2 :
                    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:83:9: LEFTPAR e= orExpression RIGHTPAR
                    {
                    match(input,LEFTPAR,FOLLOW_LEFTPAR_in_comparison191); 
                    pushFollow(FOLLOW_orExpression_in_comparison195);
                    e=orExpression();
                    _fsp--;

                    match(input,RIGHTPAR,FOLLOW_RIGHTPAR_in_comparison197); 
                    value = e;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end comparison

    public static class atom_return extends ParserRuleReturnScope {
        public Object value;
    };

    // $ANTLR start atom
    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:86:1: atom returns [Object value] : ( quotedString | ID );
    public final atom_return atom() throws RecognitionException {
        atom_return retval = new atom_return();
        retval.start = input.LT(1);

        Token ID2=null;
        String quotedString1 = null;


        try {
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:87:5: ( quotedString | ID )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==LITERAL) ) {
                alt4=1;
            }
            else if ( (LA4_0==ID) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("86:1: atom returns [Object value] : ( quotedString | ID );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:87:9: quotedString
                    {
                    pushFollow(FOLLOW_quotedString_in_atom222);
                    quotedString1=quotedString();
                    _fsp--;

                    retval.value = quotedString1;

                    }
                    break;
                case 2 :
                    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:88:9: ID
                    {
                    ID2=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_atom234); 

                                Object v = memory.get(ID2.getText());
                                if ( v != null ) {
                                    retval.value = v;
                                }
                                else {
                                	retval.value = "undefined";
                                    throw new FailedPredicateException(input, ID2.getText(),
                                        "Undefined variable \"" + ID2.getText() + "\"");
                                }
                            

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end atom


    // $ANTLR start quotedString
    // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:102:1: quotedString returns [String value] : LITERAL ;
    public final String quotedString() throws RecognitionException {
        String value = null;

        Token LITERAL3=null;

        try {
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:103:7: ( LITERAL )
            // /Users/nobby/apache/lenya/branches/branch_1_2_x_shibboleth/src/java/org/apache/lenya/ac/impl/antlr/Expressions.g:103:9: LITERAL
            {
            LITERAL3=(Token)input.LT(1);
            match(input,LITERAL,FOLLOW_LITERAL_in_quotedString267); 
            value = LITERAL3.getText().substring(1, LITERAL3.getText().length() - 1);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end quotedString


 

    public static final BitSet FOLLOW_orExpression_in_prog50 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_orExpression77 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_OR_in_orExpression87 = new BitSet(new long[]{0x0000000000000680L});
    public static final BitSet FOLLOW_andExpression_in_orExpression91 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_comparison_in_andExpression125 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_AND_in_andExpression135 = new BitSet(new long[]{0x0000000000000680L});
    public static final BitSet FOLLOW_comparison_in_andExpression139 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_atom_in_comparison173 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_EQUALS_in_comparison175 = new BitSet(new long[]{0x0000000000000600L});
    public static final BitSet FOLLOW_atom_in_comparison179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPAR_in_comparison191 = new BitSet(new long[]{0x0000000000000680L});
    public static final BitSet FOLLOW_orExpression_in_comparison195 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHTPAR_in_comparison197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_quotedString_in_atom222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_atom234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_in_quotedString267 = new BitSet(new long[]{0x0000000000000002L});

}