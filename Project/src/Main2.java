
public class Main2 {

	public static void main(String[] args) {
		Labels lbl  ; 
		// TODO Auto-generated method stub
		 ParseTree   exparith, divide, two, five , three , plus , plus2  , six , one ;
		 exparith = new ParseTree(new Symbol()) ; 
		 exparith.getLabel().setAbstracts(Abstracts.EXPRARITH);

		 divide = new ParseTree(new Symbol()) ; 
		 two = new ParseTree(new Symbol()) ;
		 two.getLabel().setAbstracts(Abstracts.NUMBER);
		 two.getLabel().setValue("2");
		 
		 five = new ParseTree(new Symbol()) ; 
		 plus = new ParseTree(new Symbol()) ; 
		 plus.getLabel().setAbstracts(Abstracts.PLUS);

		 plus2 = new ParseTree(new Symbol()) ; 
		 plus2.getLabel().setAbstracts(Abstracts.PLUS);

		 six = new ParseTree(new Symbol()) ; 
		 one = new ParseTree(new Symbol()) ; 
		 one.getLabel().setAbstracts(Abstracts.NUMBER);
		 one.getLabel().setValue("1");
         
		 plus.addChild(one);
		 plus.addChild(plus2);
		 plus2.addChild(one);
		 plus2.addChild(two);
		 exparith.addChild(plus);
	
		 
		 LlvmGenerator llvm = new LlvmGenerator(exparith); 
		 llvm.EXPRARITH(exparith);
	     for(int i =0 ; i< llvm.stack.size() ; i ++ ) { 
	    	 System.out.println( llvm.stack.get(i).equation);
	     }
		 
	}

}
