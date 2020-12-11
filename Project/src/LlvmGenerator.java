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
 *   specific variables from the production rules  
 */ 

public class LlvmGenerator {
    int counter ; 
	public List<Operation> stack ;  // it would be used to insert the operation for a later display
	PredefinedFunctions read_write ; 

	public LlvmGenerator() { 
		counter =0 ; 
		this.stack  = new ArrayList<Operation> (); // keeping track of the equations 
		  this.read_write = new PredefinedFunctions(); 

    	}
	   
	    
	    public Operation PROGRAM(ParseTree node) { 
	    	  // calling the predefined read and write before the main 
	    	 String equation = this.read_write.readWrite(); 
	    	 this.stack.add(new Operation(equation, ""));
		    	// Beginning  of the program 
		      equation =  "define i32 @main() {\r\n" + 
	    			     "entry: \r\n"; 
	          this.stack.add(new Operation(equation , " ")); 

	    	  try {  this.CODE(node.getChild(0));
			    	// End of the program
			    	equation = " ret i32 0\r\n" + 
			    			"}\r\n"; 
			    	this.stack.add( new Operation(equation, ""));
			    	return null ; 

	    	      }catch( IndexOutOfBoundsException e ) { 
		    	   System.out.print("CALLING NON EXISTING CHILD");
		    	 return null;
		     }    	  
	    }
	    
	    
	    public Operation CODE(ParseTree node) { 
 	    	for(int i=0 ;  i < node.nbChildren() ; i++) { 
	    		 LexicalUnit type  = node.getChild(i).getLabel().getType(); 
	    		 if(type == LexicalUnit.ASSIGN)  this.ASSIGN(node.getChild(i));
	    		 if(type == LexicalUnit.READ)  this.READ(node.getChild(i));
	    		 if(type == LexicalUnit.PRINT)  this.PRINT(node.getChild(i));

	    	}
	    	
	    	return null ; 
	    	
	    }
	    
	    public Operation COND(ParseTree node) { 
	    	String equation = "" ; 
	    	String result = ""; 
	    	LexicalUnit type = node.getLabel().getType(); 
	    	// since the comparison happens between two Arithmetics expressions 
	    	Operation child1 = this.EXPRARITH(node.getChild(0)); 
	    	Operation child2 = this.EXPRARITH(node.getChild(1));
	    	if(type == LexicalUnit.GT) { 
	    		equation = child1.equation + 
	    				   child2.equation +
	    				   "%"+this.counter() + "= icmp sgt i32 %"
	    				    + child1.result + ", %"+child2.result    
	    		            + "\r\n"; 
	    	}else if(type == LexicalUnit.EQ) { 
	    		equation = child1.equation + 
	    				   child2.equation +
	    				   "%"+this.counter() + "= icmp eq i32 %"
	    				    + child1.result + ", %"+child2.result    
	    		            + "\r\n"; 
	    	}
    		result = Integer.toString(this.counter); 
    		Operation opr = new Operation(equation, result);
    		this.stack.add(opr);
    		return opr ; 
	    }
	    
	    
	    public Operation READ(ParseTree node) { 
	    	String equation = ""; 
	    	String result = ""; 
	    	equation = "%"+this.VARNAME(node.getChild(0)) + 
	    			    " = call i32 @readInt() \r\n"; 
	    	Operation opr = new Operation(equation , result); 
	    	this.stack.add(opr);
	    	return opr ;
	    }
         
	    public Operation PRINT(ParseTree node) { 
	    	String equation = ""; 
	    	String result = ""; 
	    	equation = "call void @println(i32 %"+ this.VARNAME(node.getChild(0))+")\r\n"; 
	    	Operation opr = new Operation(equation , result); 
	    	this.stack.add(opr);
	    	return opr ;
	    }
	    
	    
		public Operation  ASSIGN(ParseTree node) {
		     try {
				String equation = "" ; 
				String result = ""; 
				Operation opr ; 	
				String  var_name = this.VARNAME(node.getChild(0)); 
				Operation child =  this.EXPRARITH(node.getChild(1));
		        equation=  "%"+ var_name +  " = alloca i32  \r\n" + 
		        	    	 child.equation +
		                  "store i32 %" +child.result+ " , i32* %"+var_name +"\r\n";
		        result = var_name ; 
		        opr = new Operation(equation , result);
		        this.stack.add(opr);
				return opr; 
				 
		     }catch( IndexOutOfBoundsException e ) { 
		    	   System.out.print("CALLING NON EXISTING CHILD");
		        	 return null;
		     }
			
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
	//  addition
	public Operation PLUS(ParseTree node) { 
		return this.allEquations(node , "add"); 
	}
	// substruction 
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
	
	/*Since all equations are similar then this cover them all and only change the type 
	* The concept lies on evaluating each child independently and appending 
	* the equation that resulted in the child node to the parent node 
	*/
	public Operation allEquations(ParseTree node , String type  ) {
		String equation =""  ; 
		String result = " "; 
		for(int i= 0  ; i < node.nbChildren() ; i ++) { 
 		  Operation childi = this.arithmetics(node.getChild(i)); // the ith child
			if(this.arithmetics(node.getChild(i)).equation.equals("") ) { 
			  equation = equation 
					  + "%" + this.counter()+ "= alloca i32 "+ "\r\n"
					  + "store i32 " + childi.result + ", i32* %" +Integer.toString( this.counter) +"\r\n"
					  + " %"+this.counter()+" = load i32 , i32*  %"+Integer.toString( this.counter-1)+"\r\n"
			  		  ; 
 		      } else { 
 				  equation = equation + childi.equation
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
	// this part only cover the mathematical arithmetics 
	public Operation arithmetics (ParseTree node) { 
		String result="" ; 
		String equation=""; 
		LexicalUnit type = node.getLabel().getType() ; 
 
				if(type== LexicalUnit.VARNAME  ) { 
					result = this.returnVar(node) ; 
					equation = this.counter()+"load i32 , i32* %"+result+"\r\n" ; 
					Operation opr = new Operation(equation , result);
					this.stack.add(opr); 
					return  opr ; 			
					
				}else 	if(type== LexicalUnit.NUMBER  ) { 
					result = this.returnVar(node) ; 
					Operation opr = new Operation("" , result);
					this.stack.add(opr); 
					return  opr ; 				

				}else if( type==LexicalUnit.MINUS) { 				  	 
						return this.MINUS(node) ; 				 
				}else if(type ==LexicalUnit.PLUS) { 
					
					return this.PLUS(node) ; 				
				}
				 else if(type ==LexicalUnit.TIMES) { 
						return this.TIMES(node) ; 
								
				}else if(type==LexicalUnit.DIVIDE) { 
					return this.DIVIDE(node) ; 
				}else { 
					return new Operation( "", "");
				}	

	}
	

	
}
