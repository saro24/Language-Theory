import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

/** 
* 
* @author Orestis Tranganidas
* @author Sara Bouglam
*/

/**
*The parser class of the compiler for the programming language FORTR-S
*/
public class Parser {

    private static LexicalAnalyzer analyzer;
    private static SortedMap<String, Integer> variables = new TreeMap<>();
    private static boolean error = false;
    private static boolean verbose = false;
    private static boolean write = false;
    private static String treeFile = "";
    public static String errorText = "";
    public static String rulesText = "";

    /**
    * Takes as input the file to be read and uses the generated Jflex code 
    * to verify the grammar of the file
    * @param Filereader file , boolean verbose, boolean write, String treefile
    *
    * @throws java.io.IOException if the file given doesn't exist
    */
    public static void parse(FileReader file, boolean verb, boolean write, String treeFile) throws java.io.IOException{
        verbose = verb;
        analyzer = new LexicalAnalyzer(file);
        if(verbose){
            rulesText += "[1] <S> -> <Program>$\n";
        }else{
            rulesText += "1,";
        }
        program();
        if(!error){
            System.out.println(rulesText);
        }
    }

    /**
    * Verifies the porogram production rule 
    *
    * @throws java.io.IOException
    */
    private static void program() throws java.io.IOException{
        if(verbose){
            rulesText += "[2] <Program> -> BEGINPROG [ProgName] [EndLine] <Code> ENDPROG\n";
        }else{
            rulesText += "2,";
        }
        Symbol token = analyzer.nextToken();
        if(token.getType() == LexicalUnit.BEGINPROG){
            token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.PROGNAME){
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    token = code();
                    if(!error){
                        if(token.getType() != LexicalUnit.ENDPROG){
                            errorMessage("ENDPROG", token.getValue().toString(), token.getLine(), token.getColumn());
                        }
                    }
                }else{
                    errorMessage("ENDLINE", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }else{
                errorMessage("PROGNAME", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }else{
            errorMessage("BEGINPROG", token.getValue().toString(), token.getLine(), token.getColumn());
        }
    }

    /**
    * Verifies the code production rule
    *
    * @throws java.io.IOException
    */
    private static Symbol code() throws java.io.IOException{
        Symbol token = analyzer.nextToken();
        if(!error){
            while(token.getType() != LexicalUnit.ENDPROG & token.getType() != LexicalUnit.EOS & token.getType() != LexicalUnit.ENDWHILE & token.getType() != LexicalUnit.ENDIF){
                if(token.getType() != LexicalUnit.ENDLINE){
                    if(verbose){
                        rulesText += "[3] <Code> -> <Instruction> [EndLine] <Code>\n";
                    }else{
                        rulesText += "3,";
                    }
                    instruction(token);
                }
                if(!error){
                    token = analyzer.nextToken();
                }else{
                    break;
                }
                
            }
            if(verbose){
                rulesText += "[4] <Code> -> e\n";
            }else{
                rulesText += "4,";
            }
        }
        return token;
    }

    /**
    * Verifies the instruction production rule
    *
    * @throws java.io.IOException 
    */
    private static void instruction(Symbol token) throws java.io.IOException{
        if(!error){
            LexicalUnit tok = token.getType();
            switch (tok){
            case VARNAME:
                if(verbose){
                    rulesText += "[5] <Instruction> -> <Assign>\n";
                }else{
                    rulesText += "5,";
                }
                assign(token);
                break ;
            case IF:
                if(verbose){
                    rulesText += "[6] <Instruction> -> <If>\n";
                }else{
                    rulesText += "6,";
                }
                ifs();
                break ;
            case WHILE:
                if(verbose){
                    rulesText += "[7] <Instruction> -> <While>\n";
                }else{
                    rulesText += "7,";
                }
                whiles();
                break ;
            case PRINT:
                if(verbose){
                    rulesText += "[8] <Instruction> -> <Print>\n";
                }else{
                    rulesText += "8,";
                }
                prints();
                break ;
            case READ:
                if(verbose){
                    rulesText += "[9] <Instruction> -> <Read>\n";
                }else{
                    rulesText += "9,";
                }
                reads();
                break ;
            }
        }
    }

    /**
    * Verifies the assign production rule
    *
    * @throws java.io.IOException
    */
    private static void assign(Symbol token) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[10] <Assign> -> [VarName] := <ExprArith>\n";
            }else{
                rulesText += "10,";
            }
            record(token.getValue().toString(), token.getLine());
            token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.ASSIGN){
                exprArith();
            }else{
                errorMessage(":=", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the exprArith production rule
    *
    * @throws java.io.IOException 
    */
    private static Symbol exprArith() throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[11] <ExprArith > -> <ExprArith'> < ExprArith''>\n";
            }else{
                rulesText += "11,";
            }
            token = exprArith1();
            token = exprArith2(token);
        }
        return token;
    }

    /**
    * Verifies the exprArith' production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol exprArith1() throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[15] <ExprArith'> -> <Multiplication>\n";
            }else{
                rulesText += "15,";
            }
            token = multiplication();
        }
        return token;
    }

    /**
    * Verifies the exprArith'' production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol exprArith2(Symbol token) throws java.io.IOException{
        if(!error){
            if(token.getType() == LexicalUnit.PLUS){
                if(verbose){
                    rulesText += "[12] <ExprArith''> -> + <Multiplication>< ExprArith''>\n";
                }else{
                    rulesText += "12,";
                }
                token = multiplication();
                token = exprArith2(token);
            }else if(token.getType() == LexicalUnit.MINUS){
                if(verbose){
                    rulesText += "[13] <ExprArith''> -> - <Multiplication>< ExprArith''>\n";
                }else{
                    rulesText += "13,";
                }
                token = multiplication();
                token = exprArith2(token);
            }else{
                if(verbose){
                    rulesText += "[14] <ExprArith''> -> e\n";
                }else{
                    rulesText += "14,";
                }
            }
        }
        return token;
    }

    /**
    * Verifies the multiplication production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol multiplication() throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[16] <Multiplication> -> <Multiplication'> <Multiplication''>\n";
            }else{
                rulesText += "16,";
            }
            multiplication1();
            token = multiplication2();
        }
        return token;
    }

    /**
    * Verifies the multiplication' production rule 
    *
    * @throws java.io.IOException
    */
    private static void multiplication1() throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[20] <Multiplication'> -> <Bracket>\n";
            }else{
                rulesText += "20,";
            }
            bracket();
        }
    }

    /**
    * Verifies the multiplication'' production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol multiplication2() throws java.io.IOException{
        Symbol token = analyzer.nextToken();
        if(!error){
            if(token.getType() == LexicalUnit.TIMES){
                if(verbose){
                    rulesText += "[17] <Multiplication''> -> * <Braquet> <Multiplication''>\n";
                }else{
                    rulesText += "17,";
                }
                bracket();
                token = multiplication2();
            }else if(token.getType() == LexicalUnit.DIVIDE){
                if(verbose){
                    rulesText += "[18] <Multiplication''> -> / <Braquet> <Multiplication''>\n";
                }else{
                    rulesText += "18,";
                }
                bracket();
                token = multiplication2();
            }else{
                if(verbose){
                    rulesText += "[19] <Multiplication''> -> e\n";
                }else{
                    rulesText += "19,";
                }
            }
        }
        return token;
    }

    /**
    * Verifies the bracket production rule 
    *
    * @throws java.io.IOException
    */
    private static void bracket() throws java.io.IOException{
        if(!error){
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                if(verbose){
                    rulesText += "[21] <Bracket> -> (ExprArith)\n";
                }else{
                    rulesText += "21,";
                }
                token = exprArith();
                if(!error){
                    if(token.getType() != LexicalUnit.RPAREN){
                        errorMessage(")", token.getValue().toString(), token.getLine(), token.getColumn());
                    }
                }
            }else{
                if(verbose){
                    rulesText += "[22] <Bracket> -> <Var>\n";
                }else{
                    rulesText += "22,";
                }
                variable(token);
            }
        }
    }

    /**
    * Verifies the variable production rule 
    *
    * @throws java.io.IOException
    */
    private static void variable(Symbol token) throws java.io.IOException{
        if(!error){
            if(token.getType() == LexicalUnit.MINUS){
                if(verbose){
                    rulesText += "[25] <Var> -> - <Var>\n";
                }else{
                    rulesText += "25,";
                }
                token = analyzer.nextToken();
                variable(token);
            }else{
                if(token.getType() == LexicalUnit.VARNAME){
                    if(verbose){
                        rulesText += "[23] <Var> -> [VarName]\n";
                    }else{
                        rulesText += "23,";
                    }
                }else if(token.getType() == LexicalUnit.NUMBER){
                    if(verbose){
                        rulesText += "[24] <Var> -> [Number]\n";
                    }else{
                        rulesText += "24,";
                    }
                }else{
                    errorMessage("variable or number", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }
        }
    }

    /**
    * Verifies the if production rule 
    *
    * @throws java.io.IOException
    */
    private static void ifs() throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[26] <If> -> IF (<Cond>) THEN [EndLine] <Code> <If''>\n";
            }else{
                rulesText += "26,";
            }
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                token = cond();
                if(token.getType() == LexicalUnit.RPAREN){
                    token = analyzer.nextToken();
                    if(token.getType() == LexicalUnit.THEN){
                        token = analyzer.nextToken();
                        if(token.getType() == LexicalUnit.ENDLINE){
                                token = code();
                                ifs2(token);
                        }else{
                                errorMessage("ENDLINE", token.getValue().toString(), token.getLine(), token.getColumn());
                        }
                    }else{
                            errorMessage("THEN", token.getValue().toString(), token.getLine(), token.getColumn());
                    }
                }else{
                    errorMessage(")", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }else{
                errorMessage("(", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }            
    }

    /**
    * Verifies the if'' production rule 
    *
    * @throws java.io.IOException
    */
    private static void ifs2(Symbol token) throws java.io.IOException{
        if(!error){
            if(token.getType() == LexicalUnit.ELSE){
                if(verbose){
                    rulesText += "[28] <If''> -> ELSE [EndLine] <Code> ENDIF\n";
                }else{
                    rulesText += "28,";
                }
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    token = code();
                    if(!error){
                        if(token.getType() != LexicalUnit.ENDIF){
                                errorMessage("ENDIF", token.getValue().toString(), token.getLine(), token.getColumn());
                        }
                    }
                }else{
                    errorMessage("ENDLINE", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }else if(token.getType() == LexicalUnit.ENDIF){
                if(verbose){
                    rulesText += "[27] <If''> -> ENDIF\n";
                }else{
                    rulesText += "27,";
                }
            }else {
                errorMessage("ELSE or ENDIF", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the condition production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol cond() throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[29] <Cond> -> <ExprArith> <Comp> <ExprArith>\n";
            }else{
                rulesText += "29,";
            }
            token = exprArith();
            comp(token);
            token = exprArith();
        }
        return token;
    }

    /**
    * Verifies the comp production rule 
    *
    * @throws java.io.IOException
    */
    private static void comp(Symbol token) throws java.io.IOException{
        if(!error){
            if(token.getType() == LexicalUnit.EQ){
                if(verbose){
                    rulesText += "[30] <Comp> -> =\n";
                }else{
                    rulesText += "30,";
                }
            }else if(token.getType() == LexicalUnit.GT){
                if(verbose){
                    rulesText += "[31] <Comp> -> >\n";
                }else{
                    rulesText += "31,";
                }
            }else{
                errorMessage("= or >", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the while production rule 
    *
    * @throws java.io.IOException
    */
    private static void whiles() throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[32] <While> -> WHILE (<Cond>) DO [EndLine] <Code> ENDWHILE\n";
            }else{
                rulesText += "32,";
            }
            Symbol token = cond();
            if(token.getType() == LexicalUnit.DO){
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    token = code();
                    if(!error){
                        if(token.getType() != LexicalUnit.ENDWHILE){
                            errorMessage("ENDWHILE", token.getValue().toString(), token.getLine(), token.getColumn());
                        }
                    }
                }else{
                    errorMessage("ENDLINE", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }else{
                    errorMessage("DO", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the print production rule 
    *
    * @throws java.io.IOException
    */
    private static void prints() throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[33] <Print> -> PRINT([VarName])\n";
            }else{
                rulesText += "33,";
            }
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.VARNAME){
                    if(variables.containsKey(token.getValue().toString())){
                        token = analyzer.nextToken();
                        if(token.getType() != LexicalUnit.RPAREN){
                            errorMessage(")", token.getValue().toString(), token.getLine(), token.getColumn());
                        } 
                    }else{
                        error = true;
                        System.out.println("The variable: "+token.getValue().toString()+" has not been assigned");
                    }
                }else{
                    errorMessage("variable", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }else{
                    errorMessage("(", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the read production rule 
    *
    * @throws java.io.IOException
    */
    private static void reads() throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[34] <Read> -> READ([VarName])\n";
            }else{
                rulesText += "34,";
            }
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.VARNAME){
                    record(token.getValue().toString(), token.getLine());
                }else{
                    errorMessage("variable", token.getValue().toString(), token.getLine(), token.getColumn());
                }
                token = analyzer.nextToken();
                if(token.getType() != LexicalUnit.RPAREN){
                    errorMessage(")", token.getValue().toString(), token.getLine(), token.getColumn());
                }
            }else{
                errorMessage("(", token.getValue().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Sends an error message and set the error flag to true
    */
    private static void errorMessage(String expected, String received, int line, int column){
        error = true;
        errorText = "Error at line "+line+" at column "+column+ ": Expected " +expected+ " and instead received "+received;
        System.out.println(errorText);
    }

    /**
    * Saves the variables that appear in the file in alphabetical order alongside the line
    * they first appear in
    */
    private static void record(String var, int place){
        if(! variables.containsKey(var)){
            variables.put(var, place);
        }
    }

}