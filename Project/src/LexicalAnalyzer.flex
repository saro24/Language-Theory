%%// Options of the scanner
%class LexicalAnalyzer	//Name
%unicode				//Use unicode
%line         			//Use line counter (yyline variable)
%column       			//Use character counter by line (yycolumn variable)
%type Symbol 
%function nextToken	

// Return value of the program
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

// Extended Regular Expressions

ENDLINE  		= "\n" | "\r" | "\n\r"
ProgName        = [A-Z]{1}([A-Z]|[0-9])*[a-z]+([A-Z]|[0-9]|[a-z])*
VarName		    = [a-z]{1} ([0-9]|[a-z])* 
Integer         = [1-9][0-9]*|0
Decimal         = \.[0-9]*
Exponent        = [eE]{Integer}
Number          = {Integer}{Decimal}?{Exponent}?

%%// Identification of tokens

// Relational operators
"="	        	{return new Symbol(LexicalUnit.EQ,yyline, yycolumn, yytext());}
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

//Ignore comments
"//".*          {}
[/][*][^*]*[*]+([^*/][^*]*[*]+)*[/]       {} 

// Ignore white spaces
" "				{}

// Print an error message if the token doesn't match any entry in the LexicalUnit list
.               {System.out.println("ERROR  "+yytext());}