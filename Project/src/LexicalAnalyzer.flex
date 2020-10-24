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

// Return value of the program
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

// Extended Regular Expressions

AlphaUpperCase  = [A-Z]
AlphaLowerCase  = [a-z]
Numeric         = [0-9]
ENDLINE  		= "\n" | "\r" | "\n\r"
ProgName		= {AlphaUpperCase}+|{AlphaUpperCase}*{AlphaLowerCase}+{Numeric}*
VarName		    = {AlphaLowerCase}+|{AlphaLowerCase}+{Numeric}*
Integer         = [1-9][0-9]*|0
Decimal         = \.[0-9]*
Exponent        = [eE]{Integer}
Number          = {Integer}{Decimal}?{Exponent}?

%%// Identification of tokens

// Relational operators
"="	        	{return new Symbol(LexicalUnit.EQ,yyline, yycolumn, yytext());}
","	        	{return new Symbol(LexicalUnit.COMMA,yyline, yycolumn, yytext());}
":="	        {return new Symbol(LexicalUnit.ASSIGN,yyline, yycolumn, yytext());}
{ENDLINE}	    {return new Symbol(LexicalUnit.ENDLINE,yyline, yycolumn, "\\n");}
">"		        {return new Symbol(LexicalUnit.GT,yyline, yycolumn, yytext());}
"/"		        {return new Symbol(LexicalUnit.DIVIDE,yyline, yycolumn, yytext());}
"("		        {return new Symbol(LexicalUnit.LPAREN,yyline, yycolumn, yytext());}
")"		        {return new Symbol(LexicalUnit.RPAREN,yyline, yycolumn, yytext());}
"-"	        	{return new Symbol(LexicalUnit.MINUS,yyline, yycolumn, yytext());}
"+"	        	{return new Symbol(LexicalUnit.PLUS,yyline, yycolumn, yytext());}
"*"	        	{return new Symbol(LexicalUnit.TIMES,yyline, yycolumn, yytext());}

// Keywords
"BEGINPROG"	    {return new Symbol(LexicalUnit.BEGINPROG,yyline, yycolumn, yytext());}
"DO"	        {return new Symbol(LexicalUnit.DO,yyline, yycolumn, yytext());}
"ELSE"          {return new Symbol(LexicalUnit.ELSE,yyline, yycolumn, yytext());}
"ENDIF"	        {return new Symbol(LexicalUnit.ENDIF,yyline, yycolumn, yytext());}
"ENDPROG"	    {return new Symbol(LexicalUnit.ENDPROG,yyline, yycolumn, yytext());}
"ENDWHILE"	    {return new Symbol(LexicalUnit.ENDWHILE,yyline, yycolumn, yytext());}
"IF"	        {return new Symbol(LexicalUnit.IF,yyline, yycolumn, yytext());}
{Number}	    {return new Symbol(LexicalUnit.NUMBER,yyline, yycolumn, yytext());}
"PRINT"	        {return new Symbol(LexicalUnit.PRINT,yyline, yycolumn, yytext());}
"READ"	        {return new Symbol(LexicalUnit.READ,yyline, yycolumn, yytext());}
"THEN"          {return new Symbol(LexicalUnit.THEN,yyline, yycolumn, yytext());}
"WHILE"	        {return new Symbol(LexicalUnit.WHILE,yyline, yycolumn, yytext());}
{VarName}	    {return new Symbol(LexicalUnit.VARNAME,yyline, yycolumn, yytext());}
{ProgName}	    {return new Symbol(LexicalUnit.PROGNAME,yyline, yycolumn, yytext());}

//Comments
"//".*          { /* DO NOTHING */ }
[/][*][^*]*[*]+([^*/][^*]*[*]+)*[/]       { /* DO NOTHING */ } 

// Ignore other characters
.               {}