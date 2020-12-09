import java.util.ArrayList;
import java.util.List;


// this  sub class is used to store the equation and the result 
class Operation { 
	  String result ; 
	  String equation ; 
	  public Operation(String equation , String result  ) { 
		  this.equation = equation; 	
		  this.result = result ; 
		  
	  }
}
/*  this class will generate LLVM from a given AST  each node represent a
 *   specific variables from the production rules  */ 

public class LlvmGenerator {
	List<ParseTree> parse_tree ; 
    LexicalUnit lex ; 
    String currentDir ; // in order to be able to access the LLvm code Directory 
    int counter ; 
	public List<Operation> stack ;  // it would be used to insert the operation for a later display

	public LlvmGenerator(List<ParseTree> parse_tree ) { 
		this.parse_tree = parse_tree ;  
		counter =0 ; 
		this.stack  = new ArrayList<Operation> (); 

    	}
		public Operation  ASSIGN(ParseTree node) {
		String equation = "" ; 
		String result = ""; 
		Operation opr ; 
		
		  String  var_name = this.VARNAME(node.getChild(0)); 
          equation=  "%"+ var_name +  " = alloca i32  \r\n" + 
        		  this.EXPRARITH(node.getChild(1)).equation +
                 "  store i32 %" +this.EXPRARITH(node.getChild(1)).result+ " , i32* %"+var_name;
          result = var_name ; 
          opr = new Operation(equation , result);
		 return opr; 
    	}
		
		
	
		public String  VARNAME(ParseTree node) { 
			return  node.getLabel().getValue().toString(); 
		}
		
		public String NUMBER(ParseTree node) { 
			return  node.getLabel().getValue().toString(); 

		}
		
		
		
	public Operation EXPRARITH(ParseTree node) { 
		return new Operation("", "") ; 
	}
	public Operation arithmetics (ParseTree node) { // this part only cover the mathemtical artithmetics 
		String result ; 
		LexicalUnit type = node.getChild(0).getLabel().getType() ; 
	
			if(type== LexicalUnit.NUMBER ||type== LexicalUnit.VARNAME  ) { 
				result = this.returnVar(node.getChild(0)) ; 
				Operation opr = new Operation("" , result);
				this.stack.add(opr); 
				return  opr ; 				
			}else if( type==LexicalUnit.MINUS) { 				  	 
					return this.MINUS(node.getChild(0)) ; 				 
			}else if(type ==LexicalUnit.PLUS) { 
				
				return this.PLUS(node.getChild(0)) ; 				
			}
			 else if(type ==LexicalUnit.TIMES) { 
					return this.TIMES(node.getChild(0)) ; 
							
			}else if(type==LexicalUnit.DIVIDE) { 
				return this.DIVIDE(node.getChild(0)) ; 
			}else { 
				return new Operation( "", "");
			}	 
	}
	
	public Operation  MINUS(ParseTree node) { 
		String equation =""  ; 
		String result = " "; 
			equation =this.arithmetics(node.getChild(0)).equation
					+ " %"+this.counter()+" = load i32 , i32*  %"+ this.arithmetics(node.getChild(0)).result+"\r\n" 
					+  this.arithmetics(node.getChild(1)).equation + 
					 "  %"+ this.counter()+" =  %load i32, i32*  %"+ this.arithmetics(node.getChild(1)).result+"\r\n" + 
				   	 "  %"+this.counter()+" = sub i32 %"+ Integer.toString(this.counter-1)+", i32 %"
					 +Integer.toString(this.counter-2) +"r\r\n" ;
		    result = " %"+Integer.toString(this.counter); 
			Operation opr = new Operation(equation , result);
			this.stack.add(opr); 
			return  opr ; 				

		
	}
	
	//addition
	public Operation PLUS(ParseTree node) {
		String equation =""  ; 
		String result = " "; 
			equation =this.arithmetics(node.getChild(0)).equation
					+ " %"+this.counter()+" = load i32 , i32*  %"+ this.arithmetics(node.getChild(0)).result+"\r\n" 
					+  this.arithmetics(node.getChild(1)).equation + 
					 "  %"+ this.counter()+" =  %load i32, i32*  %"+ this.arithmetics(node.getChild(1)).result+"\r\n" + 
				   	 "  %"+this.counter()+" = add i32 %"+ Integer.toString(this.counter-1)+", i32 %"
					 +Integer.toString(this.counter-2) +"r\r\n" ;
		    result = " %"+Integer.toString(this.counter); 
			Operation opr = new Operation(equation , result);
			this.stack.add(opr); 
			return  opr ; 				

		
	}
	// multiplication 
	public Operation TIMES(ParseTree node) { 
		String equation =""  ; 
		String result = " "; 
			equation =this.arithmetics(node.getChild(0)).equation
					+ " %"+this.counter()+" = load i32 , i32*  %"+ this.arithmetics(node.getChild(0)).result+"\r\n" 
					+  this.arithmetics(node.getChild(1)).equation + 
					 "  %"+ this.counter()+" =  %load i32, i32*  %"+ this.arithmetics(node.getChild(1)).result+"\r\n" + 
				   	 "  %"+this.counter()+" = mul i32 %"+ Integer.toString(this.counter-1)+", i32 %"
					 +Integer.toString(this.counter-2) +"r\r\n" ;
		    result = " %"+Integer.toString(this.counter); 
			Operation opr = new Operation(equation , result);
			this.stack.add(opr); 
			return  opr ; 				
	}
	
	// division 
	public Operation DIVIDE(ParseTree node) { 
		String equation =""  ; 
		String result = " "; 
			equation =this.arithmetics(node.getChild(0)).equation
					+ " %"+this.counter()+" = load i32 , i32*  %"+ this.arithmetics(node.getChild(0)).result+"\r\n" 
					+  this.arithmetics(node.getChild(1)).equation + 
					 "  %"+ this.counter()+" =  %load i32, i32*  %"+ this.arithmetics(node.getChild(1)).result+"\r\n" + 
				   	 "  %"+this.counter()+" = sdiv i32 %"+ Integer.toString(this.counter-1)+", i32 %"
					 +Integer.toString(this.counter-2) +"r\r\n" ;
		    result = " %"+Integer.toString(this.counter); 
			Operation opr = new Operation(equation , result);
			this.stack.add(opr); 
			return  opr ; 				
	}

	
	private String  returnVar(ParseTree node) { 
		if(node.getLabel().getType() == LexicalUnit.NUMBER)  return this.NUMBER(node); 
		else  return this.VARNAME(node); 
	}
	
	// this counter used in order to avoid variables repetition since this note allowed in LLvm in 
	// each time it'is incremented when needed 
	private String counter() { 
		this.counter = this.counter +1 ; 
		 String str = Integer.toString(this.counter) ; 
		return str ; 
		
		
	}
	
}
