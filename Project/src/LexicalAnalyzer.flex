import java.util.ArrayList;
//import java_cup.runtime.*; //uncommet if you use CUP
%%// Options of the scanner

%class LexicalAnalyzer	//Name
%unicode				//Use unicode
%line         			//Use line counter (yyline variable)
%column       			//Use character counter by line (yycolumn variable)
%type Symbol    		//Says that the return type is Symbol
%function nextToken
//%standalone

%{//start adding Java code
	ArrayList<String> variables = new ArrayList<String>();
	ArrayList<Integer> 	 variablePos = new ArrayList<Integer>();

	private void record(String var, int place){
		if(variables.size() == 0){
			variables.add(var);
			variablePos.add(place);
		} else{
			int pos = variables.indexOf(var);
			if(pos == -1){
				for(int i = 0;i < variables.size(); i++){
					if(var.compareTo(variables.get(i)) < 0){
						variables.add(i, var);
						variablePos.add(i, place);
					}
				}
				if(variables.indexOf(var) == -1){
					variables.add(var);
					variablePos.add(place);
				}
			}
		}
	}
%}//end adding Java code

// Return value of the program
%eofval{
	System.out.println("");
	System.out.println("Variables");
	for(int i=0;i<variables.size();i++){
		System.out.println(variables.get(i)+" "+variablePos.get(i));
	}
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

// Extended Regular Expressions

AlphaUpperCase  = [A-Z]
AlphaLowerCase  = [a-z]
Numeric         = [0-9]
ProgName		= {AlphaUpperCase}+|{AlphaUpperCase}*{AlphaLowerCase}+{Numeric}*
VarName		    = {AlphaLowerCase}+|{AlphaLowerCase}*{Numeric}*
Integer         = [1-9][0-9]*|0

%%// Identification of tokens

// Relational operators
"="	        	{System.out.println("token: " + yytext()+"   lexical unit: EQ"); return new Symbol(LexicalUnit.EQ,yyline, yycolumn);}
","	        	{System.out.println("token: " + yytext()+"   lexical unit: COMMA"); return new Symbol(LexicalUnit.COMMA,yyline, yycolumn);}
":="	        {System.out.println("token: " + yytext()+"   lexical unit: ASSIGN"); return new Symbol(LexicalUnit.ASSIGN,yyline, yycolumn);}
"\n"	        {System.out.println("token: \\n         lexical unit: ENDLINE"); return new Symbol(LexicalUnit.ENDLINE,yyline, yycolumn);}
">"		        {System.out.println("token: " + yytext()+"   lexical unit: GT"); return new Symbol(LexicalUnit.GT,yyline, yycolumn);}
"/"		        {System.out.println("token: " + yytext()+"   lexical unit: DIVIDE"); return new Symbol(LexicalUnit.DIVIDE,yyline, yycolumn);}
"("		        {System.out.println("token: " + yytext()+"   lexical unit: LPAREN"); return new Symbol(LexicalUnit.LPAREN,yyline, yycolumn);}
")"		        {System.out.println("token: " + yytext()+"   lexical unit: RPAREN"); return new Symbol(LexicalUnit.RPAREN,yyline, yycolumn);}
"-"	        	{System.out.println("token: " + yytext()+"   lexical unit: MINUS"); return new Symbol(LexicalUnit.MINUS,yyline, yycolumn);}
"+"	        	{System.out.println("token: " + yytext()+"   lexical unit: PLUS"); return new Symbol(LexicalUnit.PLUS,yyline, yycolumn);}
"*"	        	{System.out.println("token: " + yytext()+"   lexical unit: TIMES"); return new Symbol(LexicalUnit.TIMES,yyline, yycolumn);}

// Keywords
"BEGINPROG"	    {System.out.println("token: " + yytext()+"  lexical unit: BEGINPROG"); return new Symbol(LexicalUnit.BEGINPROG,yyline, yycolumn);}
"DO"	        {System.out.println("token: " + yytext()+"  lexical unit: DO"); return new Symbol(LexicalUnit.DO,yyline, yycolumn);}
"ELSE"          {System.out.println("token: " + yytext()+"  lexical unit: ELSE"); return new Symbol(LexicalUnit.ELSE,yyline, yycolumn);}
"ENDIF"	        {System.out.println("token: " + yytext()+"  lexical unit: ENDIF"); return new Symbol(LexicalUnit.ENDIF,yyline, yycolumn);}
"ENDPROG"	    {System.out.println("token: " + yytext()+"  lexical unit: ENDPROG"); return new Symbol(LexicalUnit.ENDPROG,yyline, yycolumn);}
"ENDWHILE"	    {System.out.println("token: " + yytext()+"  lexical unit: ENDWHILE"); return new Symbol(LexicalUnit.ENDWHILE,yyline, yycolumn);}
"IF"	        {System.out.println("token: " + yytext()+"  lexical unit: IF"); return new Symbol(LexicalUnit.IF,yyline, yycolumn);}
{Integer}	    {System.out.println("token: " + yytext()+"  lexical unit: NUMBER"); return new Symbol(LexicalUnit.NUMBER,yyline, yycolumn);}
"PRINT"	        {System.out.println("token: " + yytext()+"  lexical unit: PRINT"); return new Symbol(LexicalUnit.PRINT,yyline, yycolumn);}
"READ"	        {System.out.println("token: " + yytext()+"  lexical unit: READ"); return new Symbol(LexicalUnit.READ,yyline, yycolumn);}
"THEN"          {System.out.println("token: " + yytext()+"  lexical unit: THEN"); return new Symbol(LexicalUnit.THEN,yyline, yycolumn);}
"WHILE"	        {System.out.println("token: " + yytext()+"  lexical unit: WHILE"); return new Symbol(LexicalUnit.WHILE,yyline, yycolumn);}
{VarName}	    {System.out.println("token: " + yytext()+"  lexical unit: VARNAME");record(yytext(), yyline); return new Symbol(LexicalUnit.VARNAME,yyline, yycolumn);}
{ProgName}	    {System.out.println("token: " + yytext()+"  lexical unit: PROGNAME"); return new Symbol(LexicalUnit.PROGNAME,yyline, yycolumn);}

//Comments
//"/*"			{yybegin(NESTEDCOMMENT);}
//"//"			{yybegin(COMMENT);}
"//".*          { /* DO NOTHING */ }
[/][*][^*]*[*]+([^*/][^*]*[*]+)*[/]       { /* DO NOTHING */ } 
// Ignore other characters
.               {}