import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private static boolean verbose = false; //Flag that signals the user has selected the verbose option
    public static String rulesText = "";    //The rules that where used during the execution
    public static ParseTree root;           //The root of the parseTree of the execution

    /**
    * Takes as input the file to be read and uses the generated Jflex code 
    * to verify the grammar of the file
    * @param file the scource file
    * @param verb the verbose option
    * @param write parseTree write option
    * @param treeFile the file of the ParseTree
    *
    * @throws java.io.IOException if the file given doesn't exist
    */
    public static void parse(FileReader file, boolean verb, boolean write, String treeFile) throws java.io.IOException{
        verbose = verb;
        analyzer = new LexicalAnalyzer(file);
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[1] <S> -> <Program>$\n";
        }else{
            rulesText += "1 ";
        }
        //Create the root Node that represents the ParseTree
        root = new ParseTree(new Symbol(Labels.S));
        program(root);
        root.addChild(new ParseTree(new Symbol(LexicalUnit.EOS)));
        System.out.println(rulesText);
        if(write){
            //Write the parseTree in the requested file if the option was given
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
    * @param the parent node in the ParseTree
    *
    * @throws java.io.IOException
    */
    private static void program(ParseTree parent) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[2] <Program> -> BEGINPROG [ProgName] [EndLine] <Code> ENDPROG\n";
        }else{
            rulesText += "2 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.PROGRAM));
        parent.addChild(tree);
        Symbol token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
        if(token.getType() == LexicalUnit.BEGINPROG){
            tree.addChild(new ParseTree(token));
            token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.PROGNAME){
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.ENDLINE){
                    tree.addChild(new ParseTree(token));
                    token = code(tree);
                    if(token.getType() != LexicalUnit.ENDPROG){
                        errorMessage("ENDPROG", token.getType().toString(), token.getLine(), token.getColumn());
                    }else{
                        tree.addChild(new ParseTree(token));
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
    * @param the parent node in the ParseTree
    *
    * @return the last read token
    *
    * @throws java.io.IOException
    */
    private static Symbol code(ParseTree parent) throws java.io.IOException{
        Symbol token = analyzer.nextToken();
        while(token.getType() != LexicalUnit.ENDPROG & token.getType() != LexicalUnit.EOS & token.getType() != LexicalUnit.ENDWHILE & token.getType() != LexicalUnit.ENDIF & token.getType() != LexicalUnit.ELSE){
            if(token.getType() != LexicalUnit.ENDLINE){
                //Input the current rule in the list of rules that were used
                if(verbose){
                    rulesText += "[3] <Code> -> <Instruction> [EndLine] <Code>\n";
                }else{
                    rulesText += "3 ";
                }
                //Create a new Node that represents the rule
                ParseTree tree = new ParseTree(new Symbol(Labels.CODE));
                parent.addChild(tree);
                instruction(tree, token);
            }
            token = analyzer.nextToken();
        }
        if(verbose){
            rulesText += "[4] <Code> -> e\n";
        }else{
            rulesText += "4 ";
        }
        return token;
    }

    /**
    * Verifies the instruction production rule
    *
    * @param parent the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException 
    */
    private static void instruction(ParseTree parent, Symbol token) throws java.io.IOException{
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.INSTRUCTION));
        parent.addChild(tree);
        LexicalUnit tok = token.getType();
        //Check if the next token matches one of the ones expected by the rule
        switch (tok){
        case VARNAME:
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[5] <Instruction> -> <Assign>\n";
            }else{
                rulesText += "5 ";
            }
            assign(tree, token);
            break ;
        case IF:
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[6] <Instruction> -> <If>\n";
            }else{
                rulesText += "6 ";
            }
            ifs(tree, token);
            break ;
        case WHILE:
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[7] <Instruction> -> <While>\n";
            }else{
                rulesText += "7 ";
            }
            whiles(tree, token);
            break ;
        case PRINT:
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[8] <Instruction> -> <Print>\n";
            }else{
                rulesText += "8 ";
            }
            prints(tree, token);
            break ;
        case READ:
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[9] <Instruction> -> <Read>\n";
            }else{
                rulesText += "9 ";
            }
            reads(tree, token);
            break ;
        }
    }

    /**
    * Verifies the assign production rule
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void assign(ParseTree parent, Symbol token) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[10] <Assign> -> [VarName] := <ExprArith>\n";
        }else{
            rulesText += "10 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.ASSIGN));
        parent.addChild(tree);
        tree.addChild(new ParseTree(token));
        record(token.getValue().toString(), token.getLine());
        token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
        if(token.getType() == LexicalUnit.ASSIGN){
            tree.addChild(new ParseTree(token));
            exprArith(tree);
        }else{
            errorMessage("ASSIGN", token.getType().toString(), token.getLine(), token.getColumn());
        }
    }

    /**
    * Verifies the exprArith production rule
    *
    * @param the parent node in the ParseTree
    *
    * @return the last read token
    *
    * @throws java.io.IOException 
    */
    private static Symbol exprArith(ParseTree parent) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[11] <ExprArith > -> <ExprArith'> < ExprArith''>\n";
        }else{
            rulesText += "11 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.EXPRARITH));
        parent.addChild(tree);
        Symbol token = exprArith1(tree);
        token = exprArith2(tree, token);
        return token;
    }

    /**
    * Verifies the exprArith' production rule 
    *
    * @param the parent node in the ParseTree
    *
    * @return the last read token
    *
    * @throws java.io.IOException
    */
    private static Symbol exprArith1(ParseTree parent) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[15] <ExprArith'> -> <Multiplication>\n";
        }else{
            rulesText += "15 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.EXPRARITH1));
        parent.addChild(tree);
        Symbol token = multiplication(tree);
        return token;
    }

    /**
    * Verifies the exprArith'' production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @return the last read token
    *
    * @throws java.io.IOException
    */
    private static Symbol exprArith2(ParseTree parent, Symbol token) throws java.io.IOException{
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.EXPRARITH2));
        parent.addChild(tree);
        //Check if the next token matches one of the ones expected by the rule
        if(token.getType() == LexicalUnit.PLUS){
            //Input the current rule in the list of rules that were used
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
        return token;
    }

    /**
    * Verifies the multiplication production rule 
    *
    * @param the parent node in the ParseTree
    *
    * @return the last read token
    *
    * @throws java.io.IOException
    */
    private static Symbol multiplication(ParseTree parent) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[16] <Multiplication> -> <Multiplication'> <Multiplication''>\n";
        }else{
            rulesText += "16 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.MULTIPLICATION));
        parent.addChild(tree);
        multiplication1(tree);
        Symbol token = multiplication2(tree);
        return token;
    }

    /**
    * Verifies the multiplication' production rule 
    *
    * @param the parent node in the ParseTree
    *
    * @throws java.io.IOException
    */
    private static void multiplication1(ParseTree parent) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[20] <Multiplication'> -> <Bracket>\n";
        }else{
            rulesText += "20 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.MULTIPLICATION1));
        parent.addChild(tree);
        bracket(tree);
    }

    /**
    * Verifies the multiplication'' production rule 
    *
    * @param the parent node in the ParseTree
    *
    * @return the last read token
    *
    * @throws java.io.IOException
    */
    private static Symbol multiplication2(ParseTree parent) throws java.io.IOException{
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.MULTIPLICATION2));
        parent.addChild(tree);
        Symbol token = analyzer.nextToken();
        //Check if the next token matches one of the ones expected by the rule
        if(token.getType() == LexicalUnit.TIMES){
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[17] <Multiplication''> -> * <Braquet> <Multiplication''>\n";
            }else{
                rulesText += "17 ";
            }
            tree.addChild(new ParseTree(token));
            bracket(tree);
            token = multiplication2(tree);
        }else if(token.getType() == LexicalUnit.DIVIDE){
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[18] <Multiplication''> -> / <Braquet> <Multiplication''>\n";
            }else{
                rulesText += "18 ";
            }
            tree.addChild(new ParseTree(token));
            bracket(tree);
            token = multiplication2(tree);
        }else{
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[19] <Multiplication''> -> e\n";
            }else{
                rulesText += "19 ";
            }
            tree.addChild(new ParseTree(new Symbol(LexicalUnit.EOS)));
        }
        return token;
    }

    /**
    * Verifies the bracket production rule 
    *
    * @param the parent node in the ParseTree
    *
    * @throws java.io.IOException
    */
    private static void bracket(ParseTree parent) throws java.io.IOException{
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.BRACKET));
        parent.addChild(tree);
        Symbol token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
        if(token.getType() == LexicalUnit.LPAREN){
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[21] <Bracket> -> (ExprArith)\n";
            }else{
                rulesText += "21 ";
            }
            tree.addChild(new ParseTree(token));
            token = exprArith(tree);
            if(token.getType() != LexicalUnit.RPAREN){
                errorMessage(")", token.getType().toString(), token.getLine(), token.getColumn());
            }else{
                tree.addChild(new ParseTree(token));
            }
        }else{
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[22] <Bracket> -> <Var>\n";
            }else{
                rulesText += "22 ";
            }
            variable(tree, token);
        }
    }

    /**
    * Verifies the variable production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void variable(ParseTree parent, Symbol token) throws java.io.IOException{
            //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.VAR));
        parent.addChild(tree);
        //Check if the next token matches one of the ones expected by the rule
        if(token.getType() == LexicalUnit.MINUS){
            //Input the current rule in the list of rules that were used
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
                //Verify the variable has been initialised
                if(variables.containsKey(token.getValue().toString())){
                    //Input the current rule in the list of rules that were used
                    if(verbose){
                        rulesText += "[23] <Var> -> [VarName]\n";
                    }else{
                        rulesText += "23 ";
                    }
                    tree.addChild(new ParseTree(token));
                }else{
                    System.out.println("Error at line "+token.getLine()+" and column "+token.getColumn()+": The variable "+token.getValue().toString()+" has not been initialized");
                    System.exit(1);
                }
            }else if(token.getType() == LexicalUnit.NUMBER){
                //Input the current rule in the list of rules that were used
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

    /**
    * Verifies the if production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void ifs(ParseTree parent, Symbol token) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[26] <If> -> IF (<Cond>) THEN [EndLine] <Code> <If''>\n";
        }else{
            rulesText += "26 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.IF));
        parent.addChild(tree);
        tree.addChild(new ParseTree(token));
        token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
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

    /**
    * Verifies the if'' production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void ifs2(ParseTree parent, Symbol token) throws java.io.IOException{
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.IF2));
        parent.addChild(tree);
        //Check if the next token matches the one expected by the rule
        if(token.getType() == LexicalUnit.ELSE){
            //Input the current rule in the list of rules that were used
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
                if(token.getType() != LexicalUnit.ENDIF){
                    errorMessage("ENDIF", token.getType().toString(), token.getLine(), token.getColumn());
                }else{
                    tree.addChild(new ParseTree(token));
                }
            }else{
                errorMessage("ENDLINE", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }else if(token.getType() == LexicalUnit.ENDIF){
            //Input the current rule in the list of rules that were used
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

    /**
    * Verifies the condition production rule 
    *
    * @param the parent node in the ParseTree
    *
    * @return the last read token
    *
    * @throws java.io.IOException
    */
    private static Symbol cond(ParseTree parent) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[29] <Cond> -> <ExprArith> <Comp> <ExprArith>\n";
        }else{
            rulesText += "29 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.COND));
        parent.addChild(tree);
        Symbol token = exprArith(tree);
        comp(tree, token);
        token = exprArith(tree);
        return token;
    }

    /**
    * Verifies the comp production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void comp(ParseTree parent, Symbol token) throws java.io.IOException{
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.COMP));
        parent.addChild(tree);
        //Check if the next token matches one of the ones expected by the rule
        if(token.getType() == LexicalUnit.EQ){
            //Input the current rule in the list of rules that were used
            if(verbose){
                rulesText += "[30] <Comp> -> =\n";
            }else{
                rulesText += "30 ";
            }
            tree.addChild(new ParseTree(token));
        }else if(token.getType() == LexicalUnit.GT){
            //Input the current rule in the list of rules that were used
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

    /**
    * Verifies the while production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void whiles(ParseTree parent, Symbol token) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[32] <While> -> WHILE (<Cond>) DO [EndLine] <Code> ENDWHILE\n";
        }else{
            rulesText += "32 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.WHILE));
        parent.addChild(tree);
        tree.addChild(new ParseTree(token));
        token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
        if(token.getType() == LexicalUnit.LPAREN){
            tree.addChild(new ParseTree(token));
            token = cond(tree);
            if(token.getType() == LexicalUnit.RPAREN){
                tree.addChild(new ParseTree(token));
                token = analyzer.nextToken();
                if(token.getType() == LexicalUnit.DO){
                    tree.addChild(new ParseTree(token));
                    token = analyzer.nextToken();
                    if(token.getType() == LexicalUnit.ENDLINE){
                        tree.addChild(new ParseTree(token));
                        token = code(tree);
                        if(token.getType() != LexicalUnit.ENDWHILE){
                            errorMessage("ENDWHILE", token.getType().toString(), token.getLine(), token.getColumn());
                        }else{
                            tree.addChild(new ParseTree(token));
                        }
                    }else{
                        errorMessage("ENDLINE", token.getType().toString(), token.getLine(), token.getColumn());
                    }
                }else{
                        errorMessage("DO", token.getType().toString(), token.getLine(), token.getColumn());
                }
            }else{
                errorMessage("RPAREN", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }else{
            errorMessage("LPAREN", token.getType().toString(), token.getLine(), token.getColumn());
        }
    }

    /**
    * Verifies the print production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void prints(ParseTree parent, Symbol token) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[33] <Print> -> PRINT([VarName])\n";
        }else{
            rulesText += "33 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.PRINT));
        parent.addChild(tree);
        tree.addChild(new ParseTree(token));
        token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
        if(token.getType() == LexicalUnit.LPAREN){
            tree.addChild(new ParseTree(token));
            token = analyzer.nextToken();
            if(token.getType() == LexicalUnit.VARNAME){
                //Verify the variable has been initialised
                if(variables.containsKey(token.getValue().toString())){
                    tree.addChild(new ParseTree(token));
                    token = analyzer.nextToken();
                    if(token.getType() != LexicalUnit.RPAREN){
                        errorMessage("RPAREN", token.getType().toString(), token.getLine(), token.getColumn());
                    }else{
                        tree.addChild(new ParseTree(token));
                    }
                }else{
                    System.out.println("Error at line "+token.getLine()+" and column "+token.getColumn()+": The variable "+token.getValue().toString()+" has not been initialized");
                    System.exit(1);
                }
            }else{
                errorMessage("VARNAME", token.getType().toString(), token.getLine(), token.getColumn());
            }
        }else{
                errorMessage("LPAREN", token.getType().toString(), token.getLine(), token.getColumn());
        }
    }

    /**
    * Verifies the read production rule 
    *
    * @param the parent node in the ParseTree
    * @param token the last token read by the previous function
    *
    * @throws java.io.IOException
    */
    private static void reads(ParseTree parent, Symbol token) throws java.io.IOException{
        //Input the current rule in the list of rules that were used
        if(verbose){
            rulesText += "[34] <Read> -> READ([VarName])\n";
        }else{
            rulesText += "34 ";
        }
        //Create a new Node that represents the rule
        ParseTree tree = new ParseTree(new Symbol(Labels.READ));
        parent.addChild(tree);
        tree.addChild(new ParseTree(token));
        token = analyzer.nextToken();
        //Check if the next token matches the one expected by the rule
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
            errorMessage("LPAREN", token.getType().toString(), token.getLine(), token.getColumn());
        }
    }

    /**
    * Shows an error message and sets the error flag to true
    *
    * @param expected the type of token that was expeted
    * @param received the type of token that was received
    * @param line the line of the error
    * @param column the column of the error
    *
    */
    private static void errorMessage(String expected, String received, int line, int column){
        System.out.println("Error at line "+line+" at column "+column+ ": Expected " +expected+ " and instead received "+received);
        System.exit(1);
    }

    /**
    * Saves the variables that appear in the file in alphabetical order alongside the line
    * they first appear in
    *
    * @param var the name of the variable 
    * @param place the line where it appears
    *
    */
    private static void record(String var, int place){
        if(! variables.containsKey(var)){
            variables.put(var, place);
        }
    }

}