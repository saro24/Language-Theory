
public class Main2 {

	public static void main(String[] args) {
		 ParseTree   exparith, divide, two,  plus , plus2  , gt, eq 
		           , six , one , assign , varname , varname2 , program , code, read , print;
		 
		 
		 exparith = new ParseTree(new Symbol(LexicalUnit.EXPRARITH)) ; 

		 divide = new ParseTree(new Symbol(LexicalUnit.DIVIDE , "/")) ; 
		 two = new ParseTree(new Symbol(LexicalUnit.NUMBER , "2")) ;
	 
		 two.getLabel().setValue("2");
		 
		 plus = new ParseTree(new Symbol(LexicalUnit.PLUS , "+")) ; 

		 plus2 = new ParseTree(new Symbol(LexicalUnit.PLUS , "+")) ; 

		 six = new ParseTree(new Symbol(LexicalUnit.NUMBER,"6")) ; 
		 one = new ParseTree(new Symbol(LexicalUnit.NUMBER,"1")) ; 
		 
		 assign = new ParseTree(new Symbol(LexicalUnit.ASSIGN)); 
		 varname = new ParseTree(new Symbol(LexicalUnit.VARNAME , "number")); 
		 varname2= new ParseTree(new Symbol(LexicalUnit.VARNAME , "number2")); 

		 program = new ParseTree(new Symbol(LexicalUnit.PROGRAM)); 
		 code = new ParseTree(new Symbol(LexicalUnit.CODE)); 

		 read = new ParseTree(new Symbol(LexicalUnit.READ)); 
		 print = new ParseTree(new Symbol(LexicalUnit.PRINT)); 
		 
		 gt = new ParseTree(new Symbol(LexicalUnit.GT)); 
		 eq = new ParseTree(new Symbol(LexicalUnit.EQ)); 

		 
		 
		 read.addChild(varname2);
		 print.addChild(varname2);

		 assign.addChild(varname);
		 
           
		 plus.addChild(one);
		 plus.addChild(plus2);
		 plus2.addChild(one);
		 plus2.addChild(two);
		 exparith.addChild(plus);
	     assign.addChild(exparith);
	     program.addChild(code);
	     code.addChild(assign);
	     code.addChild(read);
	     code.addChild(print);
 		 
	     
	    // gt.addChild(exparith);
	     //gt.addChild(exparith);

		 LlvmGenerator llvm = new LlvmGenerator(); 
		 llvm.PROGRAM(program);
		 //llvm.COND(gt);
		 //System.out.print(llvm.stack.size());

	     for(int i =0 ; i< llvm.stack.size() ; i ++ ) { 

	    	 System.out.print( llvm.stack.get(i).equation);
	     }
		 
	}

}
