import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

    private static LexicalAnalyzer analyzer;//The Lexical Analyzer object
    private static SortedMap<String, Integer> variables = new TreeMap<>(); //The list containing the variables
    private static boolean error = false;   //Flag that signals if the parser has encountered an error
    private static boolean verbose = false; //Flag that signals the user has selected the verbose option
    public static String errorText = "";    //The message of the encountered error
    public static String rulesText = "";    //The rules that where used during the execution
    public static ParseTree root;

    /**
    * Takes as input the file to be read and uses the generated Jflex code 
    * to verify the grammar of the file
    * @param file the scource file, verbose the verbose option, write parseTree write option, treefile the file of the ParseTree
    *
    * @throws java.io.IOException if the file given doesn't exist
    */
    public static void parse(FileReader file, boolean verb, boolean write, String treeFile) throws java.io.IOException{
        verbose = verb;
        analyzer = new LexicalAnalyzer(file);
        if(verbose){
            rulesText += "[1] <S> -> <Program>$\n";
        }else{
            rulesText += "1 ";
        }
        root = new ParseTree(new Symbol(Labels.S));
        program(root);
        root.addChild(new ParseTree(new Symbol(LexicalUnit.EOS)));
        if(!error){
            System.out.println(rulesText);
        }
        if(write){
            File output = new File(".", treeFile);
            output.createNewFile();
            FileWriter writer = new FileWriter(output);
            writer.write(root.toLaTeX());
            writer.close();
        }
    }

    /**
    * Verifies the porogram production rule 
    *
    * @throws java.io.IOException
    */
    private static void program(ParseTree parent) throws java.io.IOException{
        if(verbose){
            rulesText += "[2] <Program> -> BEGINPROG [ProgName] [EndLine] <Code> ENDPROG\n";
        }else{
            rulesText += "2 ";
        }
        ParseTree tree = new ParseTree(new Symbol(Labels.PROGRAM));
        parent.addChild(tree);
        Symbol token = analyzer.nextToken();
        if(token.getType() == LexicalUnit.BEGINPROG){
            tree.addChild(new ParseTree(token));
            token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.PROGNAME){
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    tree.addChild(new ParseTree(token));
                    token = code(tree);
                    if(!error){
                        if(token.getType() != LexicalUnit.ENDPROG){
                            errorMessage("ENDPROG", token.getType().toString(), token.getLine(), token.getColumn());
                        }else{
                            tree.addChild(new ParseTree(token));
                        }
                    }
                }else{
                    errorMessage("ENDLINE", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }else{
                errorMessage("PROGNAME", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }else{
            errorMessage("BEGINPROG", token.getType().toString(), token.getLine(), token.getColumn());
        }
    }

    /**
    * Verifies the code production rule
    *
    * @throws java.io.IOException
    */
    private static Symbol code(ParseTree parent) throws java.io.IOException{
        Symbol token = analyzer.nextToken();
        if(!error){
            while(token.getType() != LexicalUnit.ENDPROG & token.getType() != LexicalUnit.EOS & token.getType() != LexicalUnit.ENDWHILE & token.getType() != LexicalUnit.ENDIF & token.getType() != LexicalUnit.ELSE){
                if(token.getType() != LexicalUnit.ENDLINE){
                    if(verbose){
                        rulesText += "[3] <Code> -> <Instruction> [EndLine] <Code>\n";
                    }else{
                        rulesText += "3 ";
                    }
                    ParseTree tree = new ParseTree(new Symbol(Labels.CODE));
                    parent.addChild(tree);
                    instruction(tree, token);
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
                rulesText += "4 ";
            }
        }
        return token;
    }

    /**
    * Verifies the instruction production rule
    *
    * @throws java.io.IOException 
    */
    private static void instruction(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.INSTRUCTION));
            parent.addChild(tree);
            LexicalUnit tok = token.getType();
            switch (tok){
            case VARNAME:
                if(verbose){
                    rulesText += "[5] <Instruction> -> <Assign>\n";
                }else{
                    rulesText += "5 ";
                }
                assign(tree, token);
                break ;
            case IF:
                if(verbose){
                    rulesText += "[6] <Instruction> -> <If>\n";
                }else{
                    rulesText += "6 ";
                }
                ifs(tree);
                break ;
            case WHILE:
                if(verbose){
                    rulesText += "[7] <Instruction> -> <While>\n";
                }else{
                    rulesText += "7 ";
                }
                whiles(tree, token);
                break ;
            case PRINT:
                if(verbose){
                    rulesText += "[8] <Instruction> -> <Print>\n";
                }else{
                    rulesText += "8 ";
                }
                prints(tree);
                break ;
            case READ:
                if(verbose){
                    rulesText += "[9] <Instruction> -> <Read>\n";
                }else{
                    rulesText += "9 ";
                }
                reads(tree);
                break ;
            }
        }
    }

    /**
    * Verifies the assign production rule
    *
    * @throws java.io.IOException
    */
    private static void assign(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[10] <Assign> -> [VarName] := <ExprArith>\n";
            }else{
                rulesText += "10 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.ASSIGN));
            parent.addChild(tree);
            tree.addChild(new ParseTree(token));
            record(token.getValue().toString(), token.getLine());
            token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.ASSIGN){
                tree.addChild(new ParseTree(token));
                exprArith(tree);
            }else{
                errorMessage("ASSIGN", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the exprArith production rule
    *
    * @throws java.io.IOException 
    */
    private static Symbol exprArith(ParseTree parent) throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[11] <ExprArith > -> <ExprArith'> < ExprArith''>\n";
            }else{
                rulesText += "11 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.EXPRARITH));
            parent.addChild(tree);
            token = exprArith1(tree);
            token = exprArith2(tree, token);
        }
        return token;
    }

    /**
    * Verifies the exprArith' production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol exprArith1(ParseTree parent) throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[15] <ExprArith'> -> <Multiplication>\n";
            }else{
                rulesText += "15 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.EXPRARITH1));
            parent.addChild(tree);
            token = multiplication(tree);
        }
        return token;
    }

    /**
    * Verifies the exprArith'' production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol exprArith2(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.EXPRARITH2));
            parent.addChild(tree);
            if(token.getType() == LexicalUnit.PLUS){
                if(verbose){
                    rulesText += "[12] <ExprArith''> -> + <Multiplication>< ExprArith''>\n";
                }else{
                    rulesText += "12 ";
                }
                tree.addChild(new ParseTree(token));
                token = multiplication(tree);
                token = exprArith2(tree, token);
            }else if(token.getType() == LexicalUnit.MINUS){
                if(verbose){
                    rulesText += "[13] <ExprArith''> -> - <Multiplication>< ExprArith''>\n";
                }else{
                    rulesText += "13 ";
                }
                tree.addChild(new ParseTree(token));
                token = multiplication(tree);
                token = exprArith2(tree, token);
            }else{
                if(verbose){
                    rulesText += "[14] <ExprArith''> -> e\n";
                }else{
                    rulesText += "14 ";
                }
                tree.addChild(new ParseTree(new Symbol(LexicalUnit.EOS)));
            }
        }
        return token;
    }

    /**
    * Verifies the multiplication production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol multiplication(ParseTree parent) throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[16] <Multiplication> -> <Multiplication'> <Multiplication''>\n";
            }else{
                rulesText += "16 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.MULTIPLICATION));
            parent.addChild(tree);
            multiplication1(tree);
            token = multiplication2(tree);
        }
        return token;
    }

    /**
    * Verifies the multiplication' production rule 
    *
    * @throws java.io.IOException
    */
    private static void multiplication1(ParseTree parent) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[20] <Multiplication'> -> <Bracket>\n";
            }else{
                rulesText += "20 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.MULTIPLICATION1));
            parent.addChild(tree);
            bracket(tree);
        }
    }

    /**
    * Verifies the multiplication'' production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol multiplication2(ParseTree parent) throws java.io.IOException{
        Symbol token = analyzer.nextToken();
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.MULTIPLICATION2));
            parent.addChild(tree);
            if(token.getType() == LexicalUnit.TIMES){
                if(verbose){
                    rulesText += "[17] <Multiplication''> -> * <Braquet> <Multiplication''>\n";
                }else{
                    rulesText += "17 ";
                }
                tree.addChild(new ParseTree(token));
                bracket(tree);
                token = multiplication2(tree);
            }else if(token.getType() == LexicalUnit.DIVIDE){
                if(verbose){
                    rulesText += "[18] <Multiplication''> -> / <Braquet> <Multiplication''>\n";
                }else{
                    rulesText += "18 ";
                }
                tree.addChild(new ParseTree(token));
                bracket(tree);
                token = multiplication2(tree);
            }else{
                if(verbose){
                    rulesText += "[19] <Multiplication''> -> e\n";
                }else{
                    rulesText += "19 ";
                }
                tree.addChild(new ParseTree(new Symbol(LexicalUnit.EOS)));
            }
        }
        return token;
    }

    /**
    * Verifies the bracket production rule 
    *
    * @throws java.io.IOException
    */
    private static void bracket(ParseTree parent) throws java.io.IOException{
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.BRACKET));
            parent.addChild(tree);
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                if(verbose){
                    rulesText += "[21] <Bracket> -> (ExprArith)\n";
                }else{
                    rulesText += "21 ";
                }
                tree.addChild(new ParseTree(token));
                token = exprArith(tree);
                if(!error){
                    if(token.getType() != LexicalUnit.RPAREN){
                        errorMessage(")", token.getType().toString(), token.getLine(), token.getColumn());
                    }else{
                        tree.addChild(new ParseTree(token));
                    }
                }
            }else{
                if(verbose){
                    rulesText += "[22] <Bracket> -> <Var>\n";
                }else{
                    rulesText += "22 ";
                }
                variable(tree, token);
            }
        }
    }

    /**
    * Verifies the variable production rule 
    *
    * @throws java.io.IOException
    */
    private static void variable(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.VAR));
            parent.addChild(tree);
            if(token.getType() == LexicalUnit.MINUS){
                if(verbose){
                    rulesText += "[25] <Var> -> - <Var>\n";
                }else{
                    rulesText += "25 ";
                }
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                variable(tree, token);
            }else{
                if(token.getType() == LexicalUnit.VARNAME){
                    if(verbose){
                        rulesText += "[23] <Var> -> [VarName]\n";
                    }else{
                        rulesText += "23 ";
                    }
                    tree.addChild(new ParseTree(token));
                }else if(token.getType() == LexicalUnit.NUMBER){
                    if(verbose){
                        rulesText += "[24] <Var> -> [Number]\n";
                    }else{
                        rulesText += "24 ";
                    }
                    tree.addChild(new ParseTree(token));
                }else{
                    errorMessage("VARNAME or NUMBER", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }
        }
    }

    /**
    * Verifies the if production rule 
    *
    * @throws java.io.IOException
    */
    private static void ifs(ParseTree parent) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[26] <If> -> IF (<Cond>) THEN [EndLine] <Code> <If''>\n";
            }else{
                rulesText += "26 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.IF));
            parent.addChild(tree);
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                tree.addChild(new ParseTree(token));
                token = cond(tree);
                if(token.getType() == LexicalUnit.RPAREN){
                    tree.addChild(new ParseTree(token));
                    token = analyzer.nextToken();
                    if(token.getType() == LexicalUnit.THEN){
                        tree.addChild(new ParseTree(token));
                        token = analyzer.nextToken();
                        if(token.getType() == LexicalUnit.ENDLINE){
                            tree.addChild(new ParseTree(token));
                            token = code(tree);
                            ifs2(tree, token);
                        }else{
                            errorMessage("ENDLINE", token.getType().toString(), token.getLine(), token.getColumn());
                        }
                    }else{
                        errorMessage("THEN", token.getType().toString(), token.getLine(), token.getColumn());
                    }
                }else{
                    errorMessage("RPAREN", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }else{
                errorMessage("LPAREN", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }            
    }

    /**
    * Verifies the if'' production rule 
    *
    * @throws java.io.IOException
    */
    private static void ifs2(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.IF2));
            parent.addChild(tree);
            if(token.getType() == LexicalUnit.ELSE){
                if(verbose){
                    rulesText += "[28] <If''> -> ELSE [EndLine] <Code> ENDIF\n";
                }else{
                    rulesText += "28 ";
                }
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    tree.addChild(new ParseTree(token));
                    token = code(tree);
                    if(!error){
                        if(token.getType() != LexicalUnit.ENDIF){
                            errorMessage("ENDIF", token.getType().toString(), token.getLine(), token.getColumn());
                        }else{
                            tree.addChild(new ParseTree(token));
                        }
                    }
                }else{
                    errorMessage("ENDLINE", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }else if(token.getType() == LexicalUnit.ENDIF){
                if(verbose){
                    rulesText += "[27] <If''> -> ENDIF\n";
                }else{
                    rulesText += "27 ";
                }
                tree.addChild(new ParseTree(token));
            }else {
                errorMessage("ELSE or ENDIF", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the condition production rule 
    *
    * @throws java.io.IOException
    */
    private static Symbol cond(ParseTree parent) throws java.io.IOException{
        Symbol token = new Symbol(LexicalUnit.EOS);
        if(!error){
            if(verbose){
                rulesText += "[29] <Cond> -> <ExprArith> <Comp> <ExprArith>\n";
            }else{
                rulesText += "29 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.COND));
            parent.addChild(tree);
            token = exprArith(tree);
            comp(tree, token);
            token = exprArith(tree);
        }
        return token;
    }

    /**
    * Verifies the comp production rule 
    *
    * @throws java.io.IOException
    */
    private static void comp(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            ParseTree tree = new ParseTree(new Symbol(Labels.COMP));
            parent.addChild(tree);
            if(token.getType() == LexicalUnit.EQ){
                if(verbose){
                    rulesText += "[30] <Comp> -> =\n";
                }else{
                    rulesText += "30 ";
                }
                tree.addChild(new ParseTree(token));
            }else if(token.getType() == LexicalUnit.GT){
                if(verbose){
                    rulesText += "[31] <Comp> -> >\n";
                }else{
                    rulesText += "31 ";
                }
                tree.addChild(new ParseTree(token));
            }else{
                errorMessage("EQ or GT", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the while production rule 
    *
    * @throws java.io.IOException
    */
    private static void whiles(ParseTree parent, Symbol token) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[32] <While> -> WHILE (<Cond>) DO [EndLine] <Code> ENDWHILE\n";
            }else{
                rulesText += "32 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.WHILE));
            parent.addChild(tree);
            tree.addChild(new ParseTree(token));
            token = cond(tree);
            if(token.getType() == LexicalUnit.DO){
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    tree.addChild(new ParseTree(token));
                    token = code(tree);
                    if(!error){
                        if(token.getType() != LexicalUnit.ENDWHILE){
                            errorMessage("ENDWHILE", token.getType().toString(), token.getLine(), token.getColumn());
                        }else{
                            tree.addChild(new ParseTree(token));
                        }
                    }
                }else{
                    errorMessage("ENDLINE", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }else{
                    errorMessage("DO", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the print production rule 
    *
    * @throws java.io.IOException
    */
    private static void prints(ParseTree parent) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[33] <Print> -> PRINT([VarName])\n";
            }else{
                rulesText += "33 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.PRINT));
            parent.addChild(tree);
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.VARNAME){
                    if(variables.containsKey(token.getValue().toString())){
                        tree.addChild(new ParseTree(token));
                        token = analyzer.nextToken();
                        if(token.getType() != LexicalUnit.RPAREN){
                            errorMessage("RPAREN", token.getType().toString(), token.getLine(), token.getColumn());
                        }else{
                            tree.addChild(new ParseTree(token));
                        }
                    }else{
                        error = true;
                        System.out.println("The variable: "+token.getType().toString()+" has not been assigned");
                    }
                }else{
                    errorMessage("VARNAME", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }else{
                    errorMessage("LPAREN", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }
    }

    /**
    * Verifies the read production rule 
    *
    * @throws java.io.IOException
    */
    private static void reads(ParseTree parent) throws java.io.IOException{
        if(!error){
            if(verbose){
                rulesText += "[34] <Read> -> READ([VarName])\n";
            }else{
                rulesText += "34 ";
            }
            ParseTree tree = new ParseTree(new Symbol(Labels.READ));
            parent.addChild(tree);
            Symbol token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.LPAREN){
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.VARNAME){
                    tree.addChild(new ParseTree(token));
                    record(token.getValue().toString(), token.getLine());
                }else{
                    errorMessage("VARNAME", token.getType().toString(), token.getLine(), token.getColumn());
                }
                token = analyzer.nextToken();
                if(token.getType() != LexicalUnit.RPAREN){
                    errorMessage("RPAREN", token.getType().toString(), token.getLine(), token.getColumn());
                }else{
                    tree.addChild(new ParseTree(token));
                }
            }else{
                errorMessage("RPAREN", token.getType().toString(), token.getLine(), token.getColumn());
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