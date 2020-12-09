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
	ParseTree root ; 
    Abstracts lex ; 
    String currentDir ; // in order to be able to access the LLvm code Directory 
    int counter ; 
	public List<Operation> stack ;  // it would be used to insert the operation for a later display

	public LlvmGenerator(ParseTree root ) { 
		this.root = root ;  
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
		String equation =""  ; 
		String result = " "; 
		for(int i= 0  ; i < node.nbChildren() ; i ++) {  
			  this.arithmetics(node.getChild(i));
		}
		result  = Integer.toString(this.counter);
		return new Operation(equation , result);
	}
	public Operation arithmetics (ParseTree node) { // this part only cover the mathematical arithmetics 
		String result="" ; 
		String equation=""; 
		Abstracts type = node.getLabel().getAbstracts() ; 
	
				if(type== Abstracts.VARNAME  ) { 
					
					result = this.returnVar(node) ; 
					equation = this.counter()+"load i32 , i32* %"+result+"\r\n" ; 
					Operation opr = new Operation(equation , result);
					this.stack.add(opr); 
					return  opr ; 			
					
				}else 	if(type== Abstracts.NUMBER  ) { 
					result = this.returnVar(node) ; 
					Operation opr = new Operation("" , result);
					this.stack.add(opr); 
					return  opr ; 				

				}else if( type==Abstracts.MINUS) { 				  	 
						return this.MINUS(node) ; 				 
				}else if(type ==Abstracts.PLUS) { 
					
					return this.PLUS(node) ; 				
				}
				 else if(type ==Abstracts.TIMES) { 
						return this.TIMES(node) ; 
								
				}else if(type==Abstracts.DIVIDE) { 
					return this.DIVIDE(node) ; 
				}else { 
					return new Operation( "", "");
				}	

	}
	//addition
	public Operation allEquations(ParseTree node , String type  ) {
		String equation =""  ; 
		String result = " "; 
		for(int i= 0  ; i < node.nbChildren() ; i ++) { 
 			
			if(this.arithmetics(node.getChild(i)).equation.equals("") ) { 

			  equation = equation 
					  + "%" + this.counter()+ "= alloca i32 "+ "\r\n"
					  + "store i32 " + this.arithmetics(node.getChild(i)).result + ", i32* %" +Integer.toString( this.counter) +"\r\n"
					  + " %"+this.counter()+" = load i32 , i32*  %"+Integer.toString( this.counter-1)+"\r\n"
			  		  ; 
 		      } else { 
 				  equation = equation + this.arithmetics(node.getChild(i)).equation
 							+ " %"+this.counter()+" = load i32 , i32*  %"+ this.arithmetics(node.getChild(i)).result+"\r\n" ; 
 		      }
			
		}
		     equation = equation + "  %"+this.counter()+" = "+ type +" i32 %"+ Integer.toString(this.counter-1)+", i32 %"
				 +Integer.toString(this.counter-2) +"\r\n" ;
		     result = Integer.toString(this.counter-2) ;
			Operation opr = new Operation(equation , result);
			this.stack.add(opr); 		
			return new Operation(equation , result);		
		
	}
	public Operation PLUS(ParseTree node) { 
		return this.allEquations(node , "add"); 
	}
	public Operation  MINUS(ParseTree node) { 
	 return this.allEquations(node , "sub"); 

	}
	// multiplication 
	public Operation TIMES(ParseTree node) { 
		 return this.allEquations(node , "mul"); 
	}
	// division 
	public Operation DIVIDE(ParseTree node) { 
		 return this.allEquations(node , "sdiv"); 

	}

	
	private String  returnVar(ParseTree node) { 
		if(node.getLabel().getAbstracts() == Abstracts.NUMBER)  return this.NUMBER(node); 
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
