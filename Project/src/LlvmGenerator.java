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
	int counter2 ; 
 	public List<Operation> stack ;  // it would be used to insert the operation for a later display
	PredefinedFunctions read_write ; 
    
	public LlvmGenerator() { 
		this.counter =0 ; 
		this.stack  = new ArrayList<Operation> (); // keeping track of the equations 
		this.read_write = new PredefinedFunctions(); 
		this.counter2 =0 ; // this counter would be used for labels in order to avoid repitition 

    	}
	   
	    
	    public Operation PROGRAM(ParseTree node) { 
	    	  // calling the predefined read and write before the main 
	    	 String equation = this.read_write.readWrite(); 
	    	 this.stack.add(new Operation(equation, ""));
		    	// Beginning  of the program 
		      equation =  "define i32 @main() {\r\n" + 
	    			     "entry: \r\n"; 
	          this.stack.add(new Operation(equation , " ")); 

	    	  try { 
				   this.CODE(node.getChild(0));
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
 			String equation = "" ; 
		    Operation opr = new Operation("", "");
 	    	for(int i=0 ;  i < node.nbChildren() ; i++) { 
	    		 LexicalUnit type  = node.getChild(i).getLabel().getType(); 
	    		 if(type == LexicalUnit.ASSIGN) equation += this.ASSIGN(node.getChild(i)).equation;
	    		 if(type == LexicalUnit.READ)   equation += this.READ(node.getChild(i)).equation;
				 if(type == LexicalUnit.PRINT)  equation += this.PRINT(node.getChild(i)).equation;
				 if(type == LexicalUnit.IF)     equation += this.IF(node.getChild(i)).equation; 
				 if(type == LexicalUnit.WHILE)  equation += this.WHILE(node.getChild(i)).equation; 

	    	}
		     opr = new Operation(equation , ""); 
 	    	return  opr;
	    	
		}
		// The If condition 
		public Operation IF(ParseTree node) { 
		  try { 
				String equation = " "; 
				String result = " "; 
				Operation opr  = new Operation(equation, result); 
				this.counter2 ++;  // this counter helps avoid the labels repetition 
				if(node.nbChildren() ==2) { 
				  Operation cond = this.COND(node.getChild(0)); 
				   equation = equation + cond.equation ;
				   equation = equation + "br i1 %"+ cond.result+ 
				   ", label %iftrue"+ Integer.toString(this.counter2)+
				   ", label %iffalse"+ Integer.toString(this.counter2)+"  \n";
				   this.counter(); // incrementing the counter just in case
				   equation = "iftrue"+ Integer.toString(this.counter2)+": \n";
				    opr  = new Operation(equation, result); 
                    this.stack.add(opr); 
				   this.CODE(node.getChild(1)); 
				   equation =  "iffalse" + Integer.toString(this.counter2)+ ": \n";
				   opr  = new Operation(equation, result); 
				   this.stack.add(opr); 
				  
				}else if(node.nbChildren() ==3){
					Operation cond = this.COND(node.getChild(0)); 
					equation = equation + cond.equation ;
					this.counter(); // incrementing the counter just in case

					equation = equation + "br i1 %"+ cond.result+ 
					", label %iftrue"+ Integer.toString(this.counter2)+
					", label %iffalse"+ Integer.toString(this.counter2)+"  \n";
					equation = equation + "iftrue"+ Integer.toString(this.counter2)+": \n";
					opr = new Operation(equation , result);
					this.stack.add(opr); 
					this.CODE(node.getChild(1));
					equation = "iffalse" + Integer.toString(this.counter2)+ ": \n";
					opr = new Operation(equation , result);
					this.stack.add(opr);  
					// Accessing the code of the else                   
					this.CODE(node.getChild(2).getChild(0));

				}
				
				return opr ; 
 		    }catch( IndexOutOfBoundsException e ) { 
		    	   System.out.print("CALLING NON EXISTING CHILD");
		        	 return null;
		     }
		}
		
		public Operation WHILE(ParseTree node){ 
			String equation =  " ";
			String result = " ";
			this.counter2++; 
			Operation cond = this.COND(node.getChild(0)); 
			equation = equation + cond.equation ;
			equation = equation + "br i1 %"+ cond.result+ 
			", label %innerWhile"+ Integer.toString(this.counter2)+
			", label %outerWhile"+ Integer.toString(this.counter2)+"  \n";
			 // incrementing the counter just in case
			equation = equation + "innerWhile"+ Integer.toString(this.counter2)+": \n";
			equation = equation + this.CODE(node.getChild(1)).equation; 
			 // checking the condition once again 
			 cond = this.COND(node.getChild(0)); 
			 equation = equation + cond.equation ;
			 equation = equation + "br i1 %"+ cond.result+ 
			 ", label %innerWhile"+ Integer.toString(this.counter2)+
			 ", label %outerWhile"+Integer.toString(this.counter2)+"  \n";
 
			equation = equation + "outerWhile" + Integer.toString(this.counter2)+ ": \n";
			Operation opr = new Operation(equation , result);
			this.stack.add(opr);
            return opr ; 

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
			    Operation child = this.EXPRARITH(node.getChild(1));
				String  var_name = this.VARNAME(node.getChild(0)); 
				if(node.getChild(1).getLabel().getType() ==LexicalUnit.MINUS ||
				   node.getChild(1).getLabel().getType() ==LexicalUnit.PLUS||
				   node.getChild(1).getLabel().getType() ==LexicalUnit.DIVIDE ||
				   node.getChild(1).getLabel().getType() ==LexicalUnit.TIMES) { 
                    child = this.EXPRARITH(node); 
				}

				 
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
 			 equation +=  this.arithmetics(node.getChild(i)).equation +"\n";
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
 				  equation = equation + childi.equation
 							+ " %"+this.counter()+" = load i32 , i32*  %"+ childi.result+"\r\n" ; 	
		    }
		     equation = equation + "  %"+this.counter()+" = "+ type +" i32 %"+ Integer.toString(this.counter-1)+", i32 %"
			                        	+Integer.toString(this.counter-2) +"\r\n" ;
		     result = Integer.toString(this.counter-2) ;
			Operation opr = new Operation(equation , result);
 			return opr;		
		
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
					equation = equation 
					+ "%" + this.counter()+ "= alloca i32 "+ "\r\n"
					+ "store i32 %" + result+ ", i32* %" +Integer.toString( this.counter) +"\r\n"
					+ " %"+this.counter()+" = load i32 , i32*  %"+Integer.toString( this.counter-1)+"\r\n"
					  ; 
					Operation opr = new Operation( equation, result);
					return  opr ; 				
					
				}else 	if(type== LexicalUnit.NUMBER  ) { 
					result = this.returnVar(node) ; 
					equation = equation 
					+ "%" + this.counter()+ "= alloca i32 "+ "\r\n"
					+ "store i32 " + result+ ", i32* %" +Integer.toString( this.counter) +"\r\n"
					+ " %"+this.counter()+" = load i32 , i32*  %"+Integer.toString( this.counter-1)+"\r\n"
					  ; 
					Operation opr = new Operation( equation, result);
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
